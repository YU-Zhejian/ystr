package org.labw.libinterval.itree;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import org.jetbrains.annotations.NotNull;
import org.labw.libinterval.BaseInterval;
import org.labw.libinterval.IntervalInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An interval tree that is built from a static list without change throughout the execution.
 *
 * <p>This interval tree requires explicit indexing before being queried. DO NOT ADD ELEMENTS TO IT
 * AFTER BEING BUILT.
 *
 * <p>Copied from LI Heng's CGRanges at <a href="https://github.com/lh3/cgranges">...</a>, commit
 * 2fb5a2a, C++ version. Following are the original Readmes:
 *
 * <p>Suppose there are <code>N=2^(K+1)-1</code> sorted numbers in an array <code>a[]</code>. They
 * implicitly form a complete binary tree of height <code>K+1</code>. We consider leaves to be at
 * level 0. The binary tree has the following properties:
 *
 * <ol>
 *   <li>The lowest <code>k-1</code> bits of nodes at <code>k</code> level are all 1. The <code>
 *       K-th</code> bit is 0. The first node at <code>k-th</code> level is indexed by <code>2^k-1
 *       </code>. The root of the tree is indexed by <code>2^K-1</code>.
 *   <li>For a node <code>x</code> at <code>k</code> level, its left child is <code>x-2^(k-1)</code>
 *       and the right child is <code>x+2^(k-1)</code>
 *   <li>For a node <code>x</code> at <code>k</code> level, it is a left child if its <code>(k+1)-th
 *       </code> bit is 0. Its parent node is <code>x+2^k</code>. Similarly, if the <code>(k+1)-th
 *       </code> bit is 1, <code>x</code> is a right child and its parent is <code>x-2^k</code>.
 *   <li>For a node <code>x</code> at <code>k</code> level, there are <code>2^(k+1)-1</code> nodes
 *       in the subtree descending from <code>x</code> , including <code>x</code> . The left-most
 *       leaf is <code>x&amp;~(2^k-1)</code> (masking the lowest level bits to 0).
 * </ol>
 *
 * When numbers can't fill a complete binary tree, the parent of a node may not be present in the
 * array. The implementation here still mimics a complete tree, though getting the special casing
 * right is a little complex. There may be alternative solutions.
 *
 * <p>As a sorted array can be considered as a binary search tree, we can implement an interval tree
 * on top of the idea. We only need to record, for each node, the maximum value in the subtree
 * descending from the node.
 */
public final class IntervalHeap<T extends IntervalInterface> implements IntervalTreeInterface<T> {

    /**
     * Interval with additional {@link #max} property.
     *
     * @implNote This class should not be replaced by record since {@link #max} is read-write
     *     property.
     * @param <S> Data inside.
     */
    private static class NodeProxy<S extends IntervalInterface> extends BaseInterval {
        private final @NotNull S data;
        private long max;

        private NodeProxy(@NotNull S data) {
            this.data = data;
            this.max = this.getEnd();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NodeProxy<?> nodeProxy)) return false;
            if (!super.equals(o)) return false;
            return max == nodeProxy.max && Objects.equals(data, nodeProxy.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), data, max);
        }

        @Override
        public long getEnd() {
            return data.getEnd();
        }

        @Override
        public long getStart() {
            return data.getStart();
        }

        public @NotNull S getData() {
            return data;
        }
    }

    /**
     * @implNote This class should not be replaced by record since all properties are read-write
     *     properties.
     */
    private static class StackCell {
        private int nodePositionInHeap; // node
        private int level;
        private boolean isLeftChildProcessed;

        private void set(int level, int nodePositionInHeap, boolean isLeftChildProcessed) {
            this.nodePositionInHeap = nodePositionInHeap;
            this.level = level;
            this.isLeftChildProcessed = isLeftChildProcessed;
        }
    }

    private final List<NodeProxy<T>> intervalHeap;
    private int maxLevel = -1;
    private boolean isIndexed;

    private int indexCore() {
        int i;
        int lastI = -1; // last_i points to the rightmost node in the tree
        long last = -1; // last is the max value at node last_i
        int k;
        if (intervalHeap.isEmpty()) {
            return -1;
        }
        for (i = 0; i < intervalHeap.size(); i += 2) {
            // leaves (i.e. at level 0)
            lastI = i;
            intervalHeap.get(i).max = intervalHeap.get(i).getEnd();
            last = intervalHeap.get(i).max;
        }
        for (k = 1; 1 << k <= intervalHeap.size(); ++k) {
            // process internal nodes in the bottom-up order
            int x = 1 << (k - 1);
            int offsetOfFirstNode = (x << 1) - 1;
            int step = x << 2; // i0 is the first node
            for (i = offsetOfFirstNode; i < intervalHeap.size(); i += step) {
                // traverse all nodes at level k
                intervalHeap.get(i).max =
                        getFinalMaxValue(i, x, last); // set the max value for node i
            }
            // last_i now points to the parent of the original last_i
            lastI = (lastI >> k & 1) > 0 ? lastI - x : lastI + x;
            if (lastI < intervalHeap.size() && intervalHeap.get(lastI).max > last) {
                // update last accordingly
                last = intervalHeap.get(lastI).max;
            }
        }
        return k - 1;
    }

    private long getFinalMaxValue(int i, int x, long last) {
        // max value of the left child
        long maxValueOfLeftChild = intervalHeap.get(i - x).max;
        long maxValueOfRightChild =
                i + x < intervalHeap.size() ? intervalHeap.get(i + x).max : last;

        return Collections.max(
                List.of(intervalHeap.get(i).getEnd(), maxValueOfLeftChild, maxValueOfRightChild));
    }

    /** Begins with an empty interval heap. */
    public IntervalHeap() {
        this.intervalHeap = new ArrayList<>();
        this.isIndexed = true;
    }

    @Override
    public void add(T interval) {
        intervalHeap.add(new NodeProxy<>(interval));
        this.isIndexed = false;
    }

    @Override
    public void index() {
        intervalHeap.sort(null);
        maxLevel = indexCore();
        this.isIndexed = true;
    }

    @Override
    public int getSize() {
        return intervalHeap.size();
    }

    @Override
    public @NotNull ObjectList<T> overlap(IntervalInterface query) {
        if (!this.isIndexed) {
            throw new RuntimeException("Refuse to work on unindexed interval tree.");
        }
        int currentLevel = 0;
        StackCell[] stack = new StackCell[64];
        for (var i = 0; i < stack.length; i++) {
            stack[i] = new StackCell();
        }
        var out = new ObjectArrayList<T>();
        if (maxLevel < 0) {
            return out;
        }

        // push the root; this is a top-down traversal
        stack[currentLevel++].set(maxLevel, (1 << maxLevel) - 1, false);
        while (currentLevel != 0) {
            // the following guarantees that numbers in out[] are always sorted
            StackCell currentCellStack = stack[--currentLevel];
            if (currentCellStack.level <= 3) {
                // we are in a small subtree; traverse every node in this subtree
                int offsetOfFirstNode = currentCellStack.nodePositionInHeap
                        >> currentCellStack.level
                        << currentCellStack.level;
                int offsetOfLastNode = Math.min(
                        offsetOfFirstNode + (1 << (currentCellStack.level + 1)) - 1,
                        intervalHeap.size());
                for (var i = offsetOfFirstNode;
                        i < offsetOfLastNode && intervalHeap.get(i).getStart() < query.getEnd();
                        ++i)
                    if (query.getStart() < intervalHeap.get(i).getEnd()) {
                        // if overlapped, append to out[]
                        out.add(intervalHeap.get(i).data);
                    }
            } else if (!currentCellStack.isLeftChildProcessed) {
                // the left child of z.x; NB: y may be out of
                // range (i.e. y>=a.size())
                int y = currentCellStack.nodePositionInHeap - (1 << (currentCellStack.level - 1));
                // re-add node z.x, but mark the left child having been processed
                stack[currentLevel++].set(
                        currentCellStack.level, currentCellStack.nodePositionInHeap, true);
                if (y >= intervalHeap.size() || intervalHeap.get(y).max > query.getStart()) {
                    // push the left child if y is out of range or
                    // may overlap with the query
                    stack[currentLevel++].set(currentCellStack.level - 1, y, false);
                }
            } else if (currentCellStack.nodePositionInHeap < intervalHeap.size()
                    && intervalHeap.get(currentCellStack.nodePositionInHeap).getStart()
                            < query.getEnd()) {
                // need to push the right child
                if (query.getStart()
                        < intervalHeap.get(currentCellStack.nodePositionInHeap).getEnd()) {
                    // test if z.x overlaps the query; if yes, append to out[]
                    out.add(intervalHeap.get(currentCellStack.nodePositionInHeap).data);
                }
                // push the right child
                stack[currentLevel++].set(
                        currentCellStack.level - 1,
                        currentCellStack.nodePositionInHeap + (1 << (currentCellStack.level - 1)),
                        false);
            }
        }
        return out;
    }

    @Override
    public @NotNull ObjectList<T> getIntervals() {
        var retl = new ObjectArrayList<T>(this.getSize());
        for (final var tNodeProxy : intervalHeap) {
            retl.add(tNodeProxy.data);
        }
        return retl;
    }
}
