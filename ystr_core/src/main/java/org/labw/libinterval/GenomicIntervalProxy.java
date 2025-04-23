package org.labw.libinterval;

import it.unimi.dsi.fastutil.longs.LongIterable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * A proxy for some genomic interval, allowing it to carry some data to reduce chaos caused by
 * inappropriate inheritance.
 *
 * @param <T> As described.
 * @param <G> As described.
 */
public final class GenomicIntervalProxy<T, G extends GenomicIntervalInterface>
        implements GenomicIntervalInterface {
    private final T data;
    private final G genomicInterface;

    /**
     * Default constructor.
     *
     * @param data As described.
     * @param genomicInterface As described.
     */
    public GenomicIntervalProxy(T data, G genomicInterface) {
        this.data = data;
        this.genomicInterface = genomicInterface;
    }

    @Contract("_, _ -> new")
    public static <D, H extends GenomicIntervalInterface> @NotNull GenomicIntervalProxy<D, H> of(
            D data, @NotNull Function<D, H> converter) {
        return new GenomicIntervalProxy<>(data, converter.apply(data));
    }

    @Override
    public String getContigName() {
        return genomicInterface.getContigName();
    }

    @Override
    public int getStrand() {
        return genomicInterface.getStrand();
    }

    @Override
    public int compareTo(@NotNull GenomicIntervalInterface other) {
        return genomicInterface.compareTo(other);
    }

    @Override
    public String simplifiedToGenomicString() {
        return genomicInterface.simplifiedToGenomicString();
    }

    @Override
    public long getEnd() {
        return genomicInterface.getEnd();
    }

    @Override
    public long getStart() {
        return genomicInterface.getStart();
    }

    @Override
    public long getLength() {
        return genomicInterface.getLength();
    }

    @Override
    public LongIterable getPositions(long stepSize) {
        return genomicInterface.getPositions(stepSize);
    }

    @Override
    public LongIterable getOffsets(long stepSize) {
        return genomicInterface.getOffsets(stepSize);
    }

    @Override
    public int compareTo(@NotNull IntervalInterface other) {
        return genomicInterface.compareTo(other);
    }

    @Override
    public String simplifiedToString() {
        return genomicInterface.simplifiedToString();
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
