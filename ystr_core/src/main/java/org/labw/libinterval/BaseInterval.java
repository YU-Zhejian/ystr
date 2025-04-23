package org.labw.libinterval;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A partial implementation that implements most default methods for {@link IntervalInterface}.
 * Self-created concrete intervals should inherit this class.
 *
 * <p>The interval is comparable. By default, comparison should be firstly on starting site, then on
 * ending site.
 *
 * <p>The interval can be hashed using its start and end sites.
 *
 * <p>Two intervals are equal if and only if they have the same start and end sites.
 */
public abstract class BaseInterval implements IntervalInterface {

    /**
     * Perform boundary check.
     *
     * @throws IllegalArgumentException if {@code start > end} or {@code start < 0}.
     */
    protected void boundaryCheck() {
        if (getStart() > getEnd()) {
            throw new IllegalArgumentException(
                    "start (%d) must be less than end (%d)".formatted(getStart(), getEnd()));
        }
        if (getStart() < 0) {
            throw new IllegalArgumentException("start (%d) must be positive".formatted(getStart()));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getStart(), this.getEnd());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) {
            return false;
        }
        if (other instanceof IntervalInterface otherInterval) {
            return (this.getStart() == otherInterval.getStart()
                    && this.getEnd() == otherInterval.getEnd());
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull IntervalInterface other) {
        var startCmpResult = Long.compare(this.getStart(), other.getStart());
        if (startCmpResult != 0) {
            return startCmpResult;
        }
        return Long.compare(this.getEnd(), other.getEnd());
    }

    @Override
    public String toString() {
        return "%s:%d-%d(%d)"
                .formatted(
                        this.getClass().getSimpleName(),
                        this.getStart(),
                        this.getEnd(),
                        this.getLength());
    }
}
