package org.labw.libinterval;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A partial implementation that implements most default methods for
 * {@link GenomicIntervalInterface}. Self-created concrete intervals should inherit this class.
 *
 * <p>Please notice that both {@link #hashCode}, {@link #equals} are stranded while
 * {@link #compareTo} is not.
 */
public abstract class GenomicBaseInterval extends BaseInterval implements GenomicIntervalInterface {

    @Override
    public int hashCode() {
        return Objects.hash(getStart(), getEnd(), getContigName(), getStrand());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) {
            return false;
        }
        if (other instanceof GenomicIntervalInterface otherInterval) {
            return super.equals(other)
                    && this.getStrand() == otherInterval.getStrand()
                    && Objects.equals(this.getContigName(), otherInterval.getContigName());
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull GenomicIntervalInterface other) {
        var contigNameCmpResult = this.getContigName().compareTo((other).getContigName());
        if (contigNameCmpResult != 0) {
            return contigNameCmpResult;
        }
        return super.compareTo(other);
    }

    @Override
    public String toString() {
        return "%s:%s:%d-%d:%s(%d)"
                .formatted(
                        this.getClass().getSimpleName(),
                        this.getContigName(),
                        this.getStart(),
                        this.getEnd(),
                        StrandUtils.strandRepr(this.getStrand()),
                        this.getLength());
    }
}
