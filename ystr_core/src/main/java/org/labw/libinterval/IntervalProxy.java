package org.labw.libinterval;

import it.unimi.dsi.fastutil.longs.LongIterable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * A proxy for some interval, allowing it to carry some data {@link #data} to reduce chaos caused by
 * inappropriate inheritance.
 *
 * <p>Mind your step while using functions provided in {@link IntervalAlgorithms} and
 * {@link IRangesUtils}. They may strip off your data and return {@link SimpleInterval} instead.
 *
 * @param <T> As described.
 * @param <G> As described.
 */
public final class IntervalProxy<T, G extends IntervalInterface> implements IntervalInterface {
    private final T data;
    private final G locus;

    /**
     * Default constructor.
     *
     * @param data As described.
     * @param locus As described.
     */
    public IntervalProxy(T data, G locus) {
        this.data = data;
        this.locus = locus;
    }

    @Contract("_, _ -> new")
    public static <D, H extends IntervalInterface> @NotNull IntervalProxy<D, H> of(
            D data, @NotNull Function<D, H> converter) {
        return new IntervalProxy<>(data, converter.apply(data));
    }

    @Override
    public long getEnd() {
        return locus.getEnd();
    }

    @Override
    public long getStart() {
        return locus.getStart();
    }

    @Override
    public long getLength() {
        return locus.getLength();
    }

    @Override
    public LongIterable getPositions(long stepSize) {
        return locus.getPositions(stepSize);
    }

    @Override
    public LongIterable getOffsets(long stepSize) {
        return locus.getOffsets(stepSize);
    }

    @Override
    public int compareTo(@NotNull IntervalInterface other) {
        return locus.compareTo(other);
    }

    @Override
    public String simplifiedToString() {
        return locus.simplifiedToString();
    }

    /**
     * As described.
     *
     * @return As described.
     */
    public T getData() {
        return data;
    }
}
