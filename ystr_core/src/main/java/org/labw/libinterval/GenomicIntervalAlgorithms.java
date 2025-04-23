package org.labw.libinterval;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class GenomicIntervalAlgorithms {
    private GenomicIntervalAlgorithms() {}

    public static boolean overlaps(
            @NotNull GenomicIntervalInterface interval1,
            @NotNull GenomicIntervalInterface interval2,
            StrandCmpPolicy strandCmpPolicy) {
        return Objects.equals(interval1.getContigName(), interval2.getContigName())
                && StrandUtils.strandCmp(
                        interval1.getStrand(), interval2.getStrand(), strandCmpPolicy)
                && IntervalAlgorithms.overlaps(interval1, interval2);
    }

    public static List<GenomicIntervalInterface> addContigStrandInfoToSubIntervals(
            GenomicIntervalInterface interval,
            @NotNull List<IntervalInterface> intervalInterfaces) {
        return intervalInterfaces.stream()
                .map(it -> (GenomicIntervalInterface) new GenomicSimpleInterval(
                        interval.getContigName(), it, interval.getStrand()))
                .toList();
    }
}
