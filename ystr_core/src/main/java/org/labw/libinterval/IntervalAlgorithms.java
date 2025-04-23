package org.labw.libinterval;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Utility class providing algorithms for intervals.
 *
 * <p>Note, although return types of some functions are marked as {@link IntervalInterface}, they
 * actually returns {@link SimpleInterval}, which is immutable, and would lose all data bundled with
 * the interval. In such circumstances, you are recommended to use those versions with generics.
 */
public final class IntervalAlgorithms {

    private IntervalAlgorithms() {}

    /**
     * Whether the other position was contained in this interval, allowing mismatches.
     *
     * @param interval1 As described.
     * @param interval2 As described.
     * @param allowedError As described.
     * @return As described.
     */
    public static boolean fuzzyContainsPos(
            @NotNull IntervalInterface interval1, int interval2, int allowedError) {
        return interval1.getStart() - allowedError < interval2
                && interval2 < interval1.getEnd() + allowedError;
    }

    /**
     * Whether this interval overlaps the other.
     *
     * <p>For example, the following intervals are considered overlapping: {@code [9, 10) [8, 11)}.
     *
     * <p>While the following are not: {@code [9, 10) [10, 11)}
     *
     * @param interval1 As described.
     * @param interval2 As described.
     * @return As described.
     */
    public static boolean overlaps(
            @NotNull IntervalInterface interval1, @NotNull IntervalInterface interval2) {
        return (interval1.getStart() < interval2.getEnd()
                && interval2.getStart() < interval1.getEnd());
    }
    /**
     * Whether the other interval was contained in this interval.
     *
     * <p>For example, {@code [10, 15)} are contained in {@code [8, 20)}.
     *
     * <p>Note, this implementation allows boundary overlapping. That means two equal intervals
     * contain each other. Add your own logic if you don't like this.
     *
     * @param interval As described.
     * @param other As described.
     * @return As described.
     */
    public static boolean contains(
            @NotNull IntervalInterface interval, @NotNull IntervalInterface other) {
        return interval.getStart() <= other.getStart() && other.getEnd() <= interval.getEnd();
    }

    /**
     * Whether the other interval was contained in this interval, allowing mismatches.
     *
     * @param interval As described.
     * @param other As described.
     * @param allowedError As described.
     * @return As described.
     */
    public static boolean fuzzyContains(
            @NotNull IntervalInterface interval,
            @NotNull IntervalInterface other,
            int allowedError) {
        return interval.getStart() - allowedError <= other.getStart()
                && other.getEnd() <= interval.getEnd() + allowedError;
    }

    /**
     * Whether the other interval equals to this interval, allowing mismatches.
     *
     * @param interval1 As described.
     * @param interval2 As described.
     * @param allowedError As described.
     * @return As described.
     */
    public static boolean fuzzyEquals(
            @NotNull IntervalInterface interval1,
            @NotNull IntervalInterface interval2,
            int allowedError) {
        return Math.abs(interval1.getStart() - interval2.getStart()) <= allowedError
                && Math.abs(interval1.getEnd() - interval2.getEnd()) <= allowedError;
    }

    /**
     * Whether the other position was contained in this interval.
     *
     * @param interval1 As described.
     * @param interval2 As described.
     * @return As described.
     */
    public static boolean containsPos(@NotNull IntervalInterface interval1, int interval2) {
        return interval1.getStart() <= interval2 && interval2 <= interval1.getEnd();
    }

    /**
     * Manhattan distance of two intervals, defined as the sum of distance between start sites
     * (absolute) and end sites(absolute).
     *
     * @param i1 As described.
     * @param i2 As described.
     * @return As described.
     */
    public static long manhattanDistance(
            @NotNull IntervalInterface i1, @NotNull IntervalInterface i2) {
        return Math.abs(i1.getStart() - i2.getStart()) + Math.abs(i1.getEnd() - i2.getEnd());
    }

    /**
     * Euclid distance of two intervals.
     *
     * @param i1 As described.
     * @param i2 As described.
     * @return As described.
     */
    public static double euclidDistance(
            @NotNull IntervalInterface i1, @NotNull IntervalInterface i2) {
        return Math.sqrt(Math.pow((double) i1.getStart() - i2.getStart(), 2)
                + Math.pow((double) i1.getEnd() - i2.getEnd(), 2));
    }

    // --------------------------------------------- Overlapping Detection
    // ---------------------------------------------

    /**
     * Length of overlapping regions. Negative for non-overlapping intervals.
     *
     * @param i1 As described.
     * @param i2 As described.
     * @return As described.
     */
    public static long overlappingLength(
            @NotNull IntervalInterface i1, @NotNull IntervalInterface i2) {
        return Math.min(i1.getEnd(), i2.getEnd()) - Math.max(i1.getStart(), i2.getStart());
    }

    /**
     * Length of overlapping regions, but limit lower bound to zero.
     *
     * @param i1 As described.
     * @param i2 As described.
     * @return As described.
     */
    public static long positiveOverlappingLength(
            @NotNull IntervalInterface i1, @NotNull IntervalInterface i2) {
        return Math.max(0, overlappingLength(i1, i2));
    }

    public static double dice(@NotNull IntervalInterface i1, @NotNull IntervalInterface i2) {
        return 2.0 * positiveOverlappingLength(i1, i2) / (i1.getLength() + i2.getLength());
    }

    public static double overlapPortionOnSmaller(
            @NotNull IntervalInterface i1, @NotNull IntervalInterface i2) {
        return (double) positiveOverlappingLength(i1, i2)
                / Math.min(i1.getLength(), i2.getLength());
    }

    public static double overlapPortionOnLarger(
            @NotNull IntervalInterface i1, @NotNull IntervalInterface i2) {
        return (double) positiveOverlappingLength(i1, i2)
                / Math.max(i1.getLength(), i2.getLength());
    }

    // --------------------------------------------- Set Operations
    // ---------------------------------------------

    public static @NotNull @Unmodifiable SimpleInterval intersection(
            @NotNull IntervalInterface i1, @NotNull IntervalInterface i2) {
        if (!overlaps(i1, i2)) {
            throw new IllegalArgumentException(
                    "Interval %s and %s should overlap.".formatted(i1, i2));
        }
        return SimpleInterval.of(
                Math.max(i1.getStart(), i2.getStart()), Math.min(i1.getEnd(), i2.getEnd()));
    }

    public static @NotNull @Unmodifiable SimpleInterval union(
            @NotNull IntervalInterface i1, @NotNull IntervalInterface i2) {
        if (!overlaps(i1, i2)) {
            throw new IllegalArgumentException(
                    "Interval %s and %s should overlap.".formatted(i1, i2));
        }
        return span(i1, i2);
    }

    public static @NotNull @Unmodifiable SimpleInterval getOverlapping(
            @NotNull IntervalInterface i1, @NotNull IntervalInterface i2) {
        if (!overlaps(i1, i2)) {
            throw new IllegalArgumentException(
                    "Interval %s and %s should overlap.".formatted(i1, i2));
        }
        return SimpleInterval.of(
                Long.max(i1.getStart(), i2.getStart()), Long.min(i1.getEnd(), i2.getEnd()));
    }

    // --------------------------------------------- Span and friends
    // ---------------------------------------------

    /**
     * Create the smallest interval that covers both intervals.
     *
     * @param i1 As described.
     * @param i2 As described.
     * @return As described in format of {@link SimpleInterval}.
     */
    @Contract("_, _ -> new")
    public static @NotNull @Unmodifiable SimpleInterval span(
            @NotNull IntervalInterface i1, @NotNull IntervalInterface i2) {
        return SimpleInterval.of(
                Math.min(i1.getStart(), i2.getStart()), Math.max(i1.getEnd(), i2.getEnd()));
    }

    /**
     * Create the smallest interval that covers the given interval while containing the given
     * boundary.
     *
     * @param i1 As described.
     * @param someBoundary The boundary, inclusive.
     * @return As described in format of {@link SimpleInterval}.
     */
    @Contract("_, _ -> new")
    public static @NotNull @Unmodifiable SimpleInterval span(
            @NotNull IntervalInterface i1, int someBoundary) {
        return SimpleInterval.of(
                Math.min(i1.getStart(), someBoundary), Math.max(i1.getEnd(), someBoundary + 1));
    }

    /**
     * Run {@link #span(IntervalInterface, IntervalInterface)} on multiple intervals.
     *
     * @param is As described.
     * @return As described.
     * @throws NoSuchElementException if no element present.
     */
    @Contract("_, -> new")
    public static @NotNull @Unmodifiable SimpleInterval spanList(@NotNull IntervalInterface... is) {
        return span(Arrays.stream(is));
    }

    /**
     * Run {@link #span(IntervalInterface, IntervalInterface)} on multiple intervals.
     *
     * @param is As described.
     * @return As described.
     * @throws NoSuchElementException if no element present.
     */
    @Contract("_, -> new")
    public static @NotNull @Unmodifiable SimpleInterval spanArray(@NotNull IntervalInterface[] is) {
        return span(Arrays.stream(is));
    }

    /**
     * Run {@link #span(IntervalInterface, IntervalInterface)} on multiple intervals.
     *
     * @param is As described.
     * @return As described.
     * @throws NoSuchElementException if no element present.
     */
    @Contract("_, -> new")
    public static @NotNull @Unmodifiable SimpleInterval span(
            @NotNull Stream<IntervalInterface> is) {
        return SimpleInterval.coerce(is.reduce(IntervalAlgorithms::span).orElseThrow());
    }

    /**
     * Run {@link #span(IntervalInterface, IntervalInterface)} on multiple intervals.
     *
     * @param is As described.
     * @return As described.
     * @throws NoSuchElementException if no element present.
     */
    @Contract("_, -> new")
    public static @NotNull @Unmodifiable SimpleInterval span(
            @NotNull Collection<IntervalInterface> is) {
        return span(is.stream());
    }

    // --------------------------------------------- Complicated big heads
    // ---------------------------------------------

    /**
     * Cluster alignments based on their position on the query sequence. Is a progressive algorithm
     * which merges all overlapping intervals.
     *
     * <pre>
     *     =============
     *     --1--
     *     --2--
     *      --3--
     *            --4--
     * </pre>
     *
     * generates: [1, 2, 3], [4]
     *
     * <p>This function is a generic function.
     *
     * @param values As described.
     * @param extractor Function that extracts a {@link IntervalInterface} out of type T.
     * @param <T> Data type that could be converted to instances of {@link IntervalInterface}.
     * @return Clustered alignments.
     */
    public static @NotNull <T> List<List<T>> binIntervals(
            @NotNull List<T> values, Function<T, IntervalInterface> extractor) {
        var valueSorted = new LinkedList<>(values.stream()
                .sorted(Comparator.comparingLong(it -> extractor.apply(it).getStart()))
                .toList());
        // Binning based on positions on the query
        var disjointValueBin = new ArrayList<List<T>>();
        IntervalInterface currentBinPos;

        while (!valueSorted.isEmpty()) {
            var baseFeature = valueSorted.getFirst();
            var thisValueBin = new ArrayList<T>();
            thisValueBin.add(baseFeature);
            valueSorted.removeFirst();
            currentBinPos = SimpleInterval.coerce(extractor.apply(baseFeature));

            while (!valueSorted.isEmpty()) {
                var nextFeature = valueSorted.getFirst();
                if (overlaps(extractor.apply(nextFeature), currentBinPos)) {
                    currentBinPos = span(extractor.apply(nextFeature), currentBinPos);
                    valueSorted.removeFirst();
                    thisValueBin.add(nextFeature);
                } else {
                    break;
                }
            }
            disjointValueBin.add(thisValueBin);
        }
        return disjointValueBin;
    }

    /**
     * Merge overlapping intervals.
     *
     * <p>Copied from <a href="https://www.geeksforgeeks.org/merging-intervals/">...</a>. The idea
     * is copied as follows:
     *
     * <p>To solve this problem optimally, we have to first sort the intervals according to the
     * starting time. Once we have the sorted intervals, we can combine all intervals in a linear
     * traversal. The idea is, in a sorted array of intervals, if <code>interval[i]</code> doesn’t
     * overlap with <code>interval[i-1]</code>, then <code>interval[i+1]</code> cannot overlap with
     * <code>interval[i-1]</code> because starting time of <code>interval[i+1]</code> must be
     * greater than or equal to <code>interval[i]</code>.
     *
     * <p>The above solution requires O(n) extra space for the stack. We can avoid the use of extra
     * space by doing merge operations in place.
     *
     * <p>Follow the steps mentioned below to implement the approach:
     *
     * <ol>
     *   <li>Return empty list on empty input.
     *   <li>Sort all intervals in increasing order of start time.
     *   <li>Traverse sorted intervals starting from the first interval,
     *   <li>Do the following for every interval:
     *       <ul>
     *         <li>If the current interval is not the first interval and it overlaps with the
     *             previous interval, then merge it with the previous interval. Keep doing it while
     *             the interval overlaps with the previous one.
     *         <li>Otherwise, Add the current interval to the output list of intervals.
     *       </ul>
     * </ol>
     *
     * @param intervals As described.
     * @return A list of {@link SimpleInterval}.
     */
    public static List<SimpleInterval> mergeIntervals(@NotNull List<IntervalInterface> intervals) {
        if (intervals.isEmpty()) {
            return List.of();
        }
        // Sorting based on the increasing order of the start intervals
        var intervalsCopied = new ArrayList<>(
                intervals.stream().map(SimpleInterval::coerce).sorted().toList());
        // Stores index of last element in output array (modified arr[])
        var index = 0;
        // Traverse all input Intervals starting from the second interval
        for (int i = 1; i < intervalsCopied.size(); i++) {
            // If this is not the first Interval and overlaps with the previous one,
            // Merge previous and current Intervals
            if (intervalsCopied.get(index).getEnd() >= intervalsCopied.get(i).getStart()) {
                intervalsCopied.set(
                        index,
                        SimpleInterval.of(
                                intervalsCopied.get(index).getStart(),
                                Math.max(
                                        intervalsCopied.get(index).getEnd(),
                                        intervalsCopied.get(i).getEnd())));
            } else {
                index += 1;
                intervalsCopied.set(index, intervalsCopied.get(i));
            }
        }
        return intervalsCopied.subList(0, index + 1);
    }

    /**
     * Detect co-linear chaining, a commonly used function for seed-and-extend based alignments.
     *
     * <p>Defined as:
     *
     * <ol>
     *   <li>All alignments on same strand of same contig.
     *   <li>If the alignments are on the positive strand, the order on query should equal to the
     *       order on reference. vise visa.
     *   <li>Alignments do not overlap.
     *   <li>Empty list considered linear.
     * </ol>
     *
     * Warning, this version does not support alignments on {@link StrandUtils#STRAND_UNKNOWN}!
     *
     * @param alignments Alignments sorted on starting position on the query.
     * @param <T> The data structure that supports extraction of alignment coordinates on reference
     *     (as {@link GenomicIntervalInterface}) and on query (as {@link IntervalInterface}).
     * @param getAlnCoordOnQuery Extractor as described.
     * @param getAlnCoordOnRef Extractor as described.
     * @return As described.
     */
    public static <T> boolean isLinear(
            @NotNull List<T> alignments,
            Function<T, GenomicIntervalInterface> getAlnCoordOnRef,
            @NotNull Function<T, IntervalInterface> getAlnCoordOnQuery) {
        var validAlignments = alignments.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(getAlnCoordOnQuery))
                .toList();
        if (validAlignments.size() <= 1) {
            return true;
        }
        for (int i = 0; i < validAlignments.size() - 1; i++) {
            var prevValue = validAlignments.get(i);
            var j = i + 1;
            var currentValue = validAlignments.get(j);
            var curRefAlnPos = getAlnCoordOnRef.apply(currentValue);
            var prevRefAlnPos = getAlnCoordOnRef.apply(prevValue);
            var curQueryAlnPos = getAlnCoordOnQuery.apply(currentValue);
            var prevQueryAlnPos = getAlnCoordOnQuery.apply(prevValue);
            var onSameContigStrand =
                    Objects.equals(prevRefAlnPos.getContigName(), curRefAlnPos.getContigName())
                            && prevRefAlnPos.getStrand() == curRefAlnPos.getStrand();
            if (!onSameContigStrand) {
                return false;
            }
            var isLinear = prevQueryAlnPos.getEnd() <= curQueryAlnPos.getStart()
                    && ((prevRefAlnPos.getStrand() == StrandUtils.STRAND_POSITIVE
                                    && prevRefAlnPos.getEnd() <= curRefAlnPos.getStart())
                            || (prevRefAlnPos.getStrand() == StrandUtils.STRAND_NEGATIVE
                                    && curRefAlnPos.getEnd() <= prevRefAlnPos.getStart()));
            if (!isLinear) {
                return false;
            }
        }
        return true;
    }
}
