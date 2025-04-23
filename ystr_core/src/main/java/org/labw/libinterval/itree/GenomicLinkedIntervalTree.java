package org.labw.libinterval.itree;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import org.jetbrains.annotations.NotNull;
import org.labw.libinterval.GenomicIntervalInterface;

import java.util.LinkedList;

public class GenomicLinkedIntervalTree<T extends GenomicIntervalInterface>
        implements GenomicIntervalTreeInterface<T> {
    protected final Object2ObjectMap<String, IntervalTreeInterface<T>> stableGenomicIntervalTrees;
    protected final Object2ObjectMap<String, LinkedList<IntervalTreeInterface<T>>>
            updatedGenomicIntervalTrees;
    protected final int treeLen;

    public GenomicLinkedIntervalTree(@NotNull ObjectList<T> genomicIntervals, int treeLen) {
        stableGenomicIntervalTrees = new Object2ObjectOpenHashMap<>();
        updatedGenomicIntervalTrees = new Object2ObjectOpenHashMap<>();
        for (var genomicInterval : genomicIntervals) {
            var contigName = genomicInterval.getContigName();
            stableGenomicIntervalTrees.computeIfAbsent(
                    contigName, (String key) -> new IntervalHeap<>());
            stableGenomicIntervalTrees.get(contigName).add(genomicInterval);
        }
        for (String contigName : stableGenomicIntervalTrees.keySet()) {
            updatedGenomicIntervalTrees.put(contigName, new LinkedList<>());
        }
        for (var intervalTree : stableGenomicIntervalTrees.values()) {
            intervalTree.index();
        }
        this.treeLen = treeLen;
    }

    protected ObjectList<IntervalTreeInterface<T>> getIntervalTreesForContig(String contigName) {
        var retl = new ObjectArrayList<IntervalTreeInterface<T>>();
        if (stableGenomicIntervalTrees.containsKey(contigName)) {
            retl.add(stableGenomicIntervalTrees.get(contigName));
        }
        if (updatedGenomicIntervalTrees.containsKey(contigName)) {
            retl.addAll(updatedGenomicIntervalTrees.get(contigName));
        }
        return retl;
    }

    @Override
    public ObjectList<T> getOverlappingGenomicIntervals(
            @NotNull GenomicIntervalInterface queryGenomicInterval, boolean ignoreStrand) {
        var retl = new ObjectArrayList<T>();
        for (var intervalTree : getIntervalTreesForContig(queryGenomicInterval.getContigName())) {
            for (var interval : intervalTree.overlap(queryGenomicInterval)) {
                if (ignoreStrand || interval.getStrand() == queryGenomicInterval.getStrand()) {
                    retl.add(interval);
                }
            }
        }
        return retl;
    }

    @Override
    public void add(@NotNull T genomicInterval) {
        var contigName = genomicInterval.getContigName();
        updatedGenomicIntervalTrees.computeIfAbsent(contigName, (String key) -> new LinkedList<>());
        var updatedIntervalTreeForThisContig = updatedGenomicIntervalTrees.get(contigName);
        if (updatedIntervalTreeForThisContig.isEmpty()) {
            updatedIntervalTreeForThisContig.add(new IntervalHeap<>());
        }
        var lastIntervalTree = updatedIntervalTreeForThisContig.getLast();
        if (lastIntervalTree.getSize() > treeLen) {
            updatedIntervalTreeForThisContig.add(new IntervalHeap<>());
            lastIntervalTree = updatedIntervalTreeForThisContig.getLast();
        }
        lastIntervalTree.add(genomicInterval);
        lastIntervalTree.index();
    }
}
