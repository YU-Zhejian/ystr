package org.labw.libinterval;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility functions and constants related to strandness. This library uses the same strand notation
 * as BioJava.
 */
public final class StrandUtils {
    /** Indicator of current strand is on the positive (aka., sense, "+") strand. */
    public static final int STRAND_POSITIVE = 1;
    /** Indicator of current strand is on the negative (aka., antisense, reverse, "-") strand. */
    public static final int STRAND_NEGATIVE = -1;
    /** Indicator of current strand is on the unknown (or neutral, ".") strand. */
    public static final int STRAND_UNKNOWN = 0;

    private StrandUtils() {}

    /**
     * Convert strand integer to string. Will convert all values other than {@link #STRAND_NEGATIVE}
     * and {@link #STRAND_POSITIVE} to ".".
     *
     * @param strand As described
     * @return As described.
     */
    public static String strandRepr(int strand) {
        String retv;
        switch (strand) {
            case STRAND_POSITIVE -> retv = "+";
            case STRAND_NEGATIVE -> retv = "-";
            default -> retv = ".";
        }
        return retv;
    }

    /**
     * Compare two strands using the desired policy.
     *
     * @param strand1 As described.
     * @param strand2 As described.
     * @param strandPolicy As described.
     * @return As described.
     */
    @Contract(pure = true)
    public static boolean strandCmp(
            int strand1, int strand2, @NotNull StrandCmpPolicy strandPolicy) {
        switch (strandPolicy) {
            case NOT_ON_OPPOSITE_STRAND -> {
                return strand1 * strand2 != -1;
            }
            case ON_SAME_STRAND_ONLY -> {
                return strand1 == strand2;
            }

            default -> {
                return true;
            }
        }
    }

    /**
     * Convert strand string in text files (e.g., GTF, GFF, GFF3, BED) to integer. Will convert all
     * values other than "+" or "-" to {@link #STRAND_UNKNOWN}.
     *
     * @param strandStr As described.
     * @return As described.
     */
    @Contract(pure = true)
    public static int strandStrToInt(@NotNull String strandStr) {
        return switch (strandStr) {
            case "+" -> STRAND_POSITIVE;
            case "-" -> STRAND_NEGATIVE;
            default -> STRAND_UNKNOWN;
        };
    }

    /**
     * Convert strand boolean to string. To be compatible with {@code labw_utils} in Python. That
     * is, {@code true} for {@link #STRAND_POSITIVE}, {@code false} for {@link #STRAND_NEGATIVE},
     * {@code null} for {@link #STRAND_UNKNOWN}.
     *
     * @param strand As described.
     * @return As described.
     */
    @Contract(pure = true)
    public static @NotNull String strandRepr(@Nullable Boolean strand) {
        String retv;
        if (strand == null) {
            retv = ".";
        } else if (strand) {
            retv = "+";
        } else {
            retv = "-";
        }
        return retv;
    }
}
