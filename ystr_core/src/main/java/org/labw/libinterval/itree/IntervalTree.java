package org.labw.libinterval.itree;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import org.jetbrains.annotations.NotNull;
import org.labw.libinterval.IntervalAlgorithms;
import org.labw.libinterval.IntervalInterface;

import java.util.Objects;

/**
 * An interval tree built from scratch. It gets slower when more elements are inserted.
 *
 * <p>Copied from <code>htsjdk.tribble.index.interval.IntervalTree</code> at <code>
 * com.github.samtools.htsjdk:4.1.0</code>. Following is the original Javadoc:
 *
 * <p>Copyright (c) 2007-2010 by The Broad Institute, Inc. and the Massachusetts Institute of
 * Technology. All Rights Reserved.
 *
 * <p>This software is licensed under the terms of the GNU Lesser General Public License (LGPL),
 * Version 2.1 which is available at <a
 * href="http://www.opensource.org/licenses/lgpl-2.1.php">...</a>.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS." THE BROAD AND MIT MAKE NO REPRESENTATIONS OR WARRANTIES OF
 * ANY KIND CONCERNING THE SOFTWARE, EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NONINFRINGEMENT, OR THE ABSENCE OF LATENT
 * OR OTHER DEFECTS, WHETHER OR NOT DISCOVERABLE. IN NO EVENT SHALL THE BROAD OR MIT, OR THEIR
 * RESPECTIVE TRUSTEES, DIRECTORS, OFFICERS, EMPLOYEES, AND AFFILIATES BE LIABLE FOR ANY DAMAGES OF
 * ANY KIND, INCLUDING, WITHOUT LIMITATION, INCIDENTAL OR CONSEQUENTIAL DAMAGES, ECONOMIC DAMAGES OR
 * INJURY TO PROPERTY AND LOST PROFITS, REGARDLESS OF WHETHER THE BROAD OR MIT SHALL BE ADVISED,
 * SHALL HAVE OTHER REASON TO KNOW, OR IN FACT SHALL KNOW OF THE POSSIBILITY OF THE FOREGOING.
 *
 * <p>An implementation of an interval tree, following the explanation. from CLR. For efficiently
 * finding all intervals which overlap a given interval or point.
 *
 * <p>References:
 *
 * <ul>
 *   <li><a href="http://en.wikipedia.org/wiki/Interval_tree">Wikipedia of Interval Tree</a>
 *   <li>Cormen, Thomas H.; Leiserson, Charles E., Rivest, Ronald L. (1990). Introduction to
 *       Algorithms (1st ed.). MIT Press and McGraw-Hill. ISBN 0-262-03141-8
 * </ul>
 */
public final class IntervalTree<T extends IntervalInterface> implements IntervalTreeInterface<T> {

    private Node<T> rootNode;

    /** See {@link #getSize()} */
    private int size;

    /** Begin with a blank interval tree. */
    public IntervalTree() {
        this.rootNode = new Node<>();
        this.size = 0;
    }

    @Override
    public void add(T interval) {
        var node = new Node<>(interval);
        insert(node);
        size++;
    }

    @Override
    public void index() {
        // No need to index. It is self-balanced.
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public @NotNull ObjectList<T> overlap(IntervalInterface interval) {

        if (root().isNil) {
            return new ObjectArrayList<>();
        }

        var results = new ObjectArrayList<T>();
        searchAll(interval, root(), results);
        return results;
    }

    private void searchAll(
            IntervalInterface interval, @NotNull Node<T> node, ObjectList<T> results) {
        if (IntervalAlgorithms.overlaps(Objects.requireNonNull(node.interval), interval)) {
            results.add(node.interval);
        }
        if (!node.left.isNil && node.left.max >= interval.getStart()) {
            searchAll(interval, node.left, results);
        }
        if (!node.right.isNil && node.right.min <= interval.getEnd()) {
            searchAll(interval, node.right, results);
        }
    }

    @Override
    public @NotNull ObjectList<T> getIntervals() {
        if (root().isNil) {
            return new ObjectArrayList<>();
        }
        var results = new ObjectArrayList<T>(size);
        getAll(root(), results);
        return results;
    }

    /**
     * Get all nodes that are descendants of {@code node}, inclusive. {@code results} is modified in
     * place
     */
    private void getAll(@NotNull Node<T> node, @NotNull ObjectList<T> results) {
        results.add(node.interval);
        if (!node.left.isNil) {
            getAll(node.left, results);
        }
        if (!node.right.isNil) {
            getAll(node.right, results);
        }
    }

    private void insert(Node<T> x) {
        treeInsert(x);
        x.color = Node.RED;
        while (x != this.rootNode && x.parent.color == Node.RED) {
            if (x.parent == x.parent.parent.left) {
                var y = x.parent.parent.right;
                if (y.color == Node.RED) {
                    x.parent.color = Node.BLACK;
                    y.color = Node.BLACK;
                    x.parent.parent.color = Node.RED;
                    x = x.parent.parent;
                } else {
                    if (x == x.parent.right) {
                        x = x.parent;
                        this.leftRotate(x);
                    }
                    x.parent.color = Node.BLACK;
                    x.parent.parent.color = Node.RED;
                    this.rightRotate(x.parent.parent);
                }
            } else {
                var y = x.parent.parent.left;
                if (y.color == Node.RED) {
                    x.parent.color = Node.BLACK;
                    y.color = Node.BLACK;
                    x.parent.parent.color = Node.RED;
                    x = x.parent.parent;
                } else {
                    if (x == x.parent.left) {
                        x = x.parent;
                        this.rightRotate(x);
                    }
                    x.parent.color = Node.BLACK;
                    x.parent.parent.color = Node.RED;
                    this.leftRotate(x.parent.parent);
                }
            }
        }
        this.rootNode.color = Node.BLACK;
    }

    private Node<T> root() {
        return this.rootNode;
    }

    private void leftRotate(@NotNull Node<T> x) {
        var y = x.right;
        x.right = y.left;
        if (!y.left.isNil) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent.isNil) {
            this.rootNode = y;
        } else {
            if (x.parent.left == x) {
                x.parent.left = y;
            } else {
                x.parent.right = y;
            }
        }
        y.left = x;
        x.parent = y;

        applyUpdate(x);
        // no need to apply update on y, since it'll y is an ancestor
        // of x, and will be touched by applyUpdate().
    }

    private void rightRotate(@NotNull Node<T> x) {
        var y = x.left;
        x.left = y.right;
        if (!y.right.isNil) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent.isNil) {
            this.rootNode = y;
        } else {
            if (x.parent.right == x) {
                x.parent.right = y;
            } else {
                x.parent.left = y;
            }
        }
        y.right = x;
        x.parent = y;

        applyUpdate(x);
        // no need to apply update on y, since it'll y is an ancestor
        // of x, and will be touched by applyUpdate().
    }

    /** Note: Does not maintain RB constraints, this is done post insert */
    private void treeInsert(Node<T> x) {
        var node = this.rootNode;
        Node<T> y = new Node<>();
        while (!node.isNil) {
            y = node;
            if (Objects.requireNonNull(x.interval).getStart()
                    <= Objects.requireNonNull(node.interval).getStart()) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        x.parent = y;

        if (y.isNil) {
            this.rootNode = x;
            x.left = x.right = new Node<>();
        } else {
            if (Objects.requireNonNull(x.interval).getStart()
                    <= Objects.requireNonNull(y.interval).getStart()) {
                y.left = x;
            } else {
                y.right = x;
            }
        }

        this.applyUpdate(x);
    }

    // Applies the statistic update on the node and its ancestors.

    private void applyUpdate(@NotNull Node<T> node) {
        while (!node.isNil) {
            this.update(node);
            node = node.parent;
        }
    }

    private void update(@NotNull Node<T> node) {
        node.max = Math.max(
                Math.max(node.left.max, node.right.max),
                Objects.requireNonNull(node.interval).getEnd());
        node.min = Math.min(Math.min(node.left.min, node.right.min), node.interval.getStart());
    }

    private static class Node<S extends IntervalInterface> {
        private static final boolean BLACK = false;
        private static final boolean RED = true;

        private final S interval;
        private final boolean isNil;
        private long min;
        private long max;
        private Node<S> left;
        private Node<S> right;
        private boolean color;
        private Node<S> parent;

        private Node() {
            // Used to generate Nil node only
            this.max = Integer.MIN_VALUE;
            this.min = Integer.MAX_VALUE;
            this.isNil = true;
            this.left = this;
            this.right = this;
            this.parent = this;
            this.interval = null;
        }

        private Node(S interval) {
            this.max = Integer.MIN_VALUE;
            this.min = Integer.MAX_VALUE;
            this.parent = new Node<>();
            this.left = new Node<>();
            this.right = new Node<>();
            this.interval = interval;
            this.color = RED;
            this.isNil = false;
        }
    }
}
