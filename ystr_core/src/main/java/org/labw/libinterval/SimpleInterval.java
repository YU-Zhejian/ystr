package org.labw.libinterval;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * The simplest full-born interval implementation with cached length. Optimized for functional
 * programming.
 *
 * <p>Please note that this class does not allow inheritance. If you wish to extend this class,
 * inherit {@link BaseInterval} or implement {@link IntervalInterface}. If you only want to attach
 * some data, you may use {@link IntervalProxy}.
 *
 * <p>Please note that this class is immutable.
 */
public final class SimpleInterval extends BaseInterval implements Serializable {
    private final long start;
    private final long end;
    /** Cached length to reduce computational time. */
    private final long length;

    /**
     * Create new instance from serialization created by {@link #simplifiedToString()}.
     *
     * @param coordStr As described.
     * @return As described.
     * @throws IllegalArgumentException On malformed coordinates.
     */
    public static @NotNull SimpleInterval lex(@NotNull String coordStr) {
        var startEndSplit = coordStr.split("-");
        try {
            return new SimpleInterval(
                    Integer.parseInt(startEndSplit[0]), Integer.parseInt(startEndSplit[1]));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Should contain at least 2 integer separated by '-', but given is '%s'"
                            .formatted(coordStr));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) {
            return false;
        }
        if (other instanceof IntervalInterface otherInterval) {
            return (this.start == otherInterval.getStart() && this.end == otherInterval.getEnd());
        }
        return false;
    }

    @Override
    public long getEnd() {
        return this.end;
    }

    @Override
    public long getStart() {
        return this.start;
    }

    /**
     * A simple method that casts some other interval to myself.
     *
     * @param interval As described.
     * @return As described.
     */
    @Contract("_ -> new")
    public static @NotNull SimpleInterval coerce(@NotNull IntervalInterface interval) {
        return SimpleInterval.of(interval.getStart(), interval.getEnd());
    }

    /**
     * Shift the interval by given offset. The offset could be negative.
     *
     * @param offset As described.
     * @return As described.
     * @throws IllegalArgumentException If the interval is shifted to negative coordinates.
     */
    @Contract("_ -> new")
    public @NotNull SimpleInterval offsetBy(long offset) {
        return SimpleInterval.of(this.start + offset, this.end + offset);
    }

    /**
     * Create some simple interval.
     *
     * @param start As described.
     * @param end As described.
     * @throws IllegalArgumentException see {@link #boundaryCheck()} for boundary checking.
     */
    public SimpleInterval(long start, long end) {
        this.start = start;
        this.end = end;
        boundaryCheck();
        this.length = super.getLength();
    }

    @Override
    public long getLength() {
        return length;
    }

    /**
     * A simple method that mimics {@link List#of}.
     *
     * @param start As described.
     * @param end As described.
     * @return Constructed new instances.
     * @throws IllegalArgumentException see {@link #boundaryCheck()} for boundary checking.
     */
    @Contract("_, _ -> new")
    public static @NotNull SimpleInterval of(long start, long end) {
        return new SimpleInterval(start, end);
    }

    /**
     * A simple method that mimics {@link List#of} which sorts start and end automatically.
     *
     * @param bound1 As described.
     * @param bound2 As described.
     * @return As described.
     * @throws IllegalArgumentException see {@link #boundaryCheck()} for boundary checking.
     */
    @Contract("_, _ -> new")
    public static @NotNull SimpleInterval ofAuto(long bound1, long bound2) {
        return new SimpleInterval(Long.min(bound1, bound2), Long.max(bound1, bound2));
    }
}
