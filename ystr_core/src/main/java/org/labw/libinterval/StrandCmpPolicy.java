package org.labw.libinterval;
/** How strands are compared in {@link StrandUtils#strandCmp}. */
public enum StrandCmpPolicy {
    /**
     * Evaluate to {@code false} if one strand is {@link StrandUtils#STRAND_POSITIVE} with another
     * {@link StrandUtils#STRAND_NEGATIVE}. Useful when {@link StrandUtils#STRAND_UNKNOWN} should be
     * tolerated.
     */
    NOT_ON_OPPOSITE_STRAND,
    /** Evaluate to {@code true} if and only if two strands are equal. */
    ON_SAME_STRAND_ONLY,
    /** Evaluate to {@code true}. */
    IGNORED
}
