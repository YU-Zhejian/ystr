package org.labw.libinterval;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Utilities that mimic IRanges interfaces. IRanges is a popular R package that is used to
 * manipulate intervals.
 *
 * <p>Please note that there are large differences between our intervals and the IRanges intervals.
 * Our intervals are zero-based open-close immutable intervals, where IRanges may not follow this
 * notation.
 *
 * @see <a href="https://bioconductor.org/packages/release/bioc/html/IRanges.html">IRanges on
 *     BioConductor</a>
 * @see <a href="https://doi.org/doi:10.18129/B9.bioc.IRanges">DOI for IRanges</a>
 */
public final class IRangesUtils {

    private IRangesUtils() {}

    /**
     * Create using starts and ends.
     *
     * @param starts As described.
     * @param ends As described.
     * @return As described.
     */
    public static List<SimpleInterval> createSE(
            @NotNull List<Integer> starts, @NotNull List<Integer> ends) {
        if (starts.size() != ends.size()) {
            throw new IllegalArgumentException(
                    "starts and ends must be the same size. Actual: %d vs. %d"
                            .formatted(starts.size(), ends.size()));
        }
        if (starts.isEmpty()) {
            return List.of();
        }
        List<SimpleInterval> intervals = new ArrayList<>();
        for (int i = 0; i < starts.size(); i++) {
            intervals.add(new SimpleInterval(starts.get(i), ends.get(i)));
        }
        return intervals;
    }

    /**
     * {@link #createSE(List, List)} with fixed start.
     *
     * @param start As described.
     * @param ends As described.
     * @return As described.
     */
    public static List<SimpleInterval> createSE(int start, @NotNull List<Integer> ends) {
        var starts = Stream.generate(() -> start).limit(ends.size()).toList();
        return createSE(starts, ends);
    }

    /**
     * {@link #createSE(List, List)} with fixed end.
     *
     * @param starts As described.
     * @param end As described.
     * @return As described.
     */
    public static List<SimpleInterval> createSE(@NotNull List<Integer> starts, int end) {
        var ends = Stream.generate(() -> end).limit(starts.size()).toList();
        return createSE(starts, ends);
    }

    /**
     * Shift the interval by given offset.
     *
     * @param interval As described.
     * @param offset As described.
     * @return As described.
     */
    @Contract("_, _ -> new")
    public static @NotNull @Unmodifiable SimpleInterval shift(
            @NotNull IntervalInterface interval, int offset) {
        return SimpleInterval.coerce(interval).offsetBy(offset);
    }

    /**
     * A generic version of interval shifting. See {@link #shift(IntervalInterface, int)}.
     *
     * <p>This function mutates input data structure in place.
     *
     * <p>This function do NOT support R-like vectorization.
     *
     * @param interval As described.
     * @param offset As described.
     * @param intervalGetter The function that extracts an {@link IntervalInterface} from the
     *     provided data structure.
     * @param intervalSetter The function that sets an {@link IntervalInterface} to the provided
     *     data structure.
     * @param <T> The data structure containing an interval.
     */
    public static <T> void shift(
            T interval,
            int offset,
            @NotNull Function<T, IntervalInterface> intervalGetter,
            @NotNull BiFunction<T, IntervalInterface, Void> intervalSetter) {
        intervalSetter.apply(interval, shift(intervalGetter.apply(interval), offset));
    }

    /**
     * The {@link #shift(IntervalInterface, int)} function supporting multiple intervals.
     *
     * @param intervals As described.
     * @param offset As described.
     * @return As described.
     */
    public static List<SimpleInterval> shift(
            @NotNull List<IntervalInterface> intervals, int offset) {
        return intervals.stream().map(it -> shift(it, offset)).toList();
    }

    /**
     * The {@link #shift(IntervalInterface, int)} function supporting multiple intervals and
     * multiple offsets.
     *
     * @param intervals As described.
     * @param offsets As described.
     * @return As described.
     */
    public static @NotNull List<SimpleInterval> shift(
            @NotNull List<IntervalInterface> intervals, @NotNull List<Integer> offsets) {
        if (offsets.size() != intervals.size()) {
            throw new IllegalArgumentException(
                    "offsets and intervals must be the same size. Actual: %d vs %d"
                            .formatted(offsets.size(), intervals.size()));
        }
        var retl = new ArrayList<SimpleInterval>();
        for (int i = 0; i < offsets.size(); i++) {
            retl.add(shift(intervals.get(i), offsets.get(i)));
        }
        return retl;
    }

    /**
     * Alias for {@link IntervalAlgorithms#mergeIntervals}.
     *
     * @param intervals As described.
     * @return As described.
     */
    public static List<SimpleInterval> reduce(@NotNull List<IntervalInterface> intervals) {
        return IntervalAlgorithms.mergeIntervals(intervals);
    }
}
