package org.labw.libinterval.itree;

import it.unimi.dsi.fastutil.objects.ObjectList;

import org.labw.libinterval.GenomicIntervalInterface;

public interface GenomicIntervalTreeInterface<T extends GenomicIntervalInterface> {
    ObjectList<T> getOverlappingGenomicIntervals(
            GenomicIntervalInterface queryGenomicInterval, boolean ignoreStrand);

    void add(T genomicInterval);
}
