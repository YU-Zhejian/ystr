package org.labw.libinterval.itree;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import org.jetbrains.annotations.NotNull;
import org.labw.libinterval.GenomicIntervalInterface;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GenomicSimpleIntervalTree<T extends GenomicIntervalInterface>
        implements GenomicIntervalTreeInterface<T> {
    protected final Map<String, IntervalTreeInterface<T>> genomicIntervalTrees;

    public GenomicSimpleIntervalTree(@NotNull Collection<T> genomicIntervals) {
        genomicIntervalTrees = new HashMap<>();
        for (var genomicInterval : genomicIntervals) {
            var contigName = genomicInterval.getContigName();
            genomicIntervalTrees.computeIfAbsent(contigName, (String key) -> new IntervalHeap<>());
            genomicIntervalTrees.get(contigName).add(genomicInterval);
        }
        for (var intervalTree : genomicIntervalTrees.values()) {
            intervalTree.index();
        }
    }

    @Override
    public ObjectList<T> getOverlappingGenomicIntervals(
            @NotNull GenomicIntervalInterface queryGenomicInterval, boolean ignoreStrand) {
        var retl = new ObjectArrayList<T>();
        var tree = genomicIntervalTrees.getOrDefault(
                queryGenomicInterval.getContigName(), new NullIntervalTree<>());
        for (var interval : tree.overlap(queryGenomicInterval)) {
            if (ignoreStrand || interval.getStrand() == queryGenomicInterval.getStrand()) {
                retl.add(interval);
            }
        }

        return retl;
    }

    @Override
    public void add(@NotNull T genomicInterval) {
        var contigName = genomicInterval.getContigName();
        genomicIntervalTrees.computeIfAbsent(contigName, (String key) -> new IntervalHeap<>());

        var thisIntervalTree = genomicIntervalTrees.get(contigName);
        thisIntervalTree.add(genomicInterval);
        thisIntervalTree.index();
    }
}
