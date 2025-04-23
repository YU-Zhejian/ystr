package org.labw.libinterval;

import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;

import org.jetbrains.annotations.NotNull;

/**
 * Interface of an open-close (<code>[)</code> -- i.e., including the start but not the end)
 * interval.
 */
public interface IntervalInterface extends Comparable<IntervalInterface> {
    /**
     * Get end point, exclusive.
     *
     * @return As described.
     */
    long getEnd();

    /**
     * Get start point, inclusive.
     *
     * @return As described.
     */
    long getStart();

    /**
     * Calculate the length of the interval. Should be {@code end - start}.
     *
     * <p>For example, the interval {@code [1, 4)} contains 1, 2, 3, so the length should be 3.
     *
     * @return As described.
     */
    default long getLength() {
        return this.getEnd() - this.getStart();
    }

    /**
     * Generating an iterable from {@link #getStart()} to {@link #getEnd()} (inclusive, exclusive)
     * with step sized {stepSize}.
     *
     * @param stepSize A positive integer.
     * @return As described.
     */
    default LongIterable getPositions(long stepSize) {
        return new LongIterable() {
            @Override
            public @NotNull LongIterator iterator() {
                return new LongIterator() {
                    long current = getStart();
                    final long max = getEnd();

                    @Override
                    public long nextLong() {
                        final long retv = current;
                        current += stepSize;
                        return retv;
                    }

                    @Override
                    public boolean hasNext() {
                        return current < max;
                    }
                };
            }
        };
    }

    /**
     * Generating an iterable from 0 to {@link #getLength()} (inclusive, inclusive) with step sized
     * <code>stepSize</code>.
     *
     * @param stepSize A positive integer.
     * @return As described.
     */
    default LongIterable getOffsets(long stepSize) {
        return new LongIterable() {
            @Override
            public @NotNull LongIterator iterator() {
                return new LongIterator() {
                    long current = 0;
                    final long max = getLength();

                    @Override
                    public long nextLong() {
                        long retv = current;
                        current += stepSize;
                        return retv;
                    }

                    @Override
                    public boolean hasNext() {
                        return current < max;
                    }
                };
            }
        };
    }

    /**
     * @apiNote All implementations should at least perform equality check on {@link #getStart()}
     *     and {@link #getEnd()}, in order. Implementations may also check other properties before
     *     or after checking {@link #getStart()} and {@link #getEnd()}.
     * @return As described.
     */
    @Override
    boolean equals(Object other);

    @Override
    int hashCode();

    /**
     * @apiNote All implementations should at least perform equality check on {@link #getStart()}
     *     and {@link #getEnd()}, in order. Implementations may also check other properties before
     *     or after checking {@link #getStart()} and {@link #getEnd()}.
     * @param other As described.
     * @return As described.
     */
    @Override
    int compareTo(@NotNull IntervalInterface other);

    /**
     * Convert the current interval to String allows debugging.
     *
     * @see #simplifiedToString() for serialization-to-string purposes.
     * @apiNote All implementations should contain class name and other useful information for debug
     *     purpose.
     * @return As described.
     */
    @Override
    String toString();

    /**
     * Human-readable serialization with minimum information.
     *
     * @return As described.
     */
    default String simplifiedToString() {
        return "%d-%d".formatted(this.getStart(), this.getEnd());
    }
}
