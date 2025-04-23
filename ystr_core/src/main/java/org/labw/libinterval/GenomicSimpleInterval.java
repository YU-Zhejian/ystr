package org.labw.libinterval;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public final class GenomicSimpleInterval extends GenomicBaseInterval implements Serializable {
    private final String contigName;
    private final long start;
    private final long end;
    private final int strand;

    @Override
    public int hashCode() {
        return Objects.hash(start, end, contigName, strand);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) {
            return false;
        }
        if (other instanceof GenomicIntervalInterface otherInterval) {
            return (this.start == otherInterval.getStart() && this.end == otherInterval.getEnd())
                    && Objects.equals(this.contigName, otherInterval.getContigName())
                    && this.strand == otherInterval.getStrand();
        }
        return false;
    }

    /**
     * Convert string like "chr1:1-100:+" or "chr1:1-100" to {@link GenomicSimpleInterval}.
     *
     * <p>If a strand is not provided, it will be interpreted as {@link StrandUtils#STRAND_UNKNOWN}.
     *
     * @param coordStr As described.
     * @return As described.
     * @throws IllegalArgumentException On malformed coordinates. Also see
     *     {@link SimpleInterval#lex(String)}.
     */
    public static @NotNull GenomicSimpleInterval lex(@NotNull String coordStr) {
        var strSplit = coordStr.split(":");
        int inputStrand;
        if (strSplit.length == 3) {
            inputStrand = StrandUtils.strandStrToInt(strSplit[2]);
        } else {
            inputStrand = StrandUtils.STRAND_UNKNOWN;
        }
        if (strSplit.length < 2) {
            throw new IllegalArgumentException(
                    "Should contain at least 2 items separated by ':', but given is '%s'"
                            .formatted(coordStr));
        }
        return new GenomicSimpleInterval(strSplit[0], SimpleInterval.lex(strSplit[1]), inputStrand);
    }

    @Contract("_ -> new")
    public static @NotNull GenomicSimpleInterval coerce(
            @NotNull GenomicIntervalInterface genomicIntervalInterface) {
        return new GenomicSimpleInterval(
                genomicIntervalInterface.getContigName(),
                genomicIntervalInterface.getStart(),
                genomicIntervalInterface.getEnd(),
                genomicIntervalInterface.getStrand());
    }

    /**
     * See {@link SimpleInterval#offsetBy}
     *
     * @param offset As described.
     * @return As described.
     */
    @Contract("_ -> new")
    public @NotNull GenomicSimpleInterval offsetBy(long offset) {
        return new GenomicSimpleInterval(
                contigName, SimpleInterval.coerce(this).offsetBy(offset), strand);
    }

    public GenomicSimpleInterval(String contigName, long start, long end, int strand) {
        if (start > end) {
            throw new IllegalArgumentException(
                    "start (%d) must be less than end (%d)".formatted(start, end));
        }
        if (start < 0) {
            throw new IllegalArgumentException("start (%d) must be positive".formatted(start));
        }
        if (strand != StrandUtils.STRAND_UNKNOWN
                && strand != StrandUtils.STRAND_POSITIVE
                && strand != StrandUtils.STRAND_NEGATIVE) {
            throw new IllegalArgumentException("strand (%d) must be -1, 0 or 1".formatted(strand));
        }
        this.start = start;
        this.end = end;
        this.contigName = contigName;
        this.strand = strand;
    }

    public GenomicSimpleInterval(
            String contigName, @NotNull IntervalInterface interval, int strand) {
        if (interval.getStart() > interval.getEnd()) {
            throw new IllegalArgumentException("start (%d) must be less than end (%d)"
                    .formatted(interval.getStart(), interval.getEnd()));
        }
        this.start = interval.getStart();
        this.end = interval.getEnd();
        this.contigName = contigName;
        this.strand = strand;
    }

    @Override
    public String getContigName() {
        return this.contigName;
    }

    @Override
    public long getEnd() {
        return this.end;
    }

    @Override
    public int getStrand() {
        return this.strand;
    }

    @Override
    public long getStart() {
        return this.start;
    }
}
