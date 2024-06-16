package com.github.yu_zhejian.ystr;

import io.vavr.Function6;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** Various utility functions concerning strings to support other classes. */
public final class StrUtils {
    /** Defunct constructor */
    private StrUtils() {}

    /**
     * Convert a signed byte to unsigned int.
     *
     * @param b As described.
     * @return As described.
     */
    public static int byteToUnsigned(final byte b) {
        return b & 0xFF;
    }

    /**
     * As described.
     *
     * @param b As described.
     * @return As described.
     */
    public static int byteToUnsigned(final int b) {
        return b & 0xFF;
    }

    /**
     * Integer power mimicking {@link Math#pow(double, double)}.
     *
     * <p><b>Implementation Limitations</b>
     *
     * <ul>
     *   <li>This method does not perform overflow detection.
     *   <li>This supports non-negative {@code p} and {@code q} only.
     * </ul>
     *
     * @param p The base.
     * @param q The exponent.
     * @return As described.
     */
    public static int pow(final int p, final int q) {
        if (p < 0 || q < 0) {
            throw new IllegalArgumentException(
                    "p and q should be non-negative. Actual: %d, %d".formatted(p, q));
        }
        int retv = 1;
        for (int i = q; i > 0; i--) {
            retv = retv * p;
        }
        return retv;
    }

    /**
     * Long version of {@link #pow(int, int)}
     *
     * @param p As described.
     * @param q As described.
     * @return As described.
     */
    public static long pow(final long p, final int q) {
        if (p < 0 || q < 0) {
            throw new IllegalArgumentException(
                    "p and q should be non-negative. Actual: %d, %d".formatted(p, q));
        }
        long retv = 1;
        for (int i = q; i > 0; i--) {
            retv = retv * p;
        }
        return retv;
    }

    /**
     * Ensure skipFirst and end is valid for some open-close interval.
     *
     * @param start As described.
     * @param end As described.
     */
    public static void ensureStartEndValid(final int start, final int end) {
        if (start > end) {
            throw new IllegalArgumentException(
                    "start must be less than end. Actual: %d vs %d".formatted(start, end));
        }
        if (start < 0) {
            throw new IllegalArgumentException(
                    "start must be greater than or equal to zero. Actual: %d".formatted(start));
        }
    }

    /**
     * Ensure skipFirst and end is valid for some open-close interval within a string.
     *
     * @param start As described.
     * @param end As described.
     * @param strLen As described.
     */
    public static void ensureStartEndValid(final int start, final int end, final int strLen) {
        ensureStartEndValid(start, end);
        if (end > strLen) {
            throw new IllegalArgumentException(
                    "end must be less than strLen. Actual: %d vs %d".formatted(end, strLen));
        }
    }

    /**
     * Similar function as is implemented in C standard libraries.
     *
     * <p>Implemented with the help of TONGYI Lingma.
     *
     * @return As described.
     */
    public static int strcmp(final byte @NotNull [] array1, final byte @NotNull [] array2) {
        // FIXME: Negative byte values.
        final int minLength = Math.min(array1.length, array2.length);
        for (int i = 0; i < minLength; i++) {
            if (!Objects.equals(array1[i], array2[i])) {
                // If bytes are not equal, return the difference (this mimics the behavior of
                // strcmp)
                return Byte.compare(array1[i], array2[i]);
            }
        }
        // If we didn't find any differences in the common prefix, the shorter array is
        // lexicographically less
        return Integer.compare(array1.length, array2.length);
    }

    /**
     * Calculate Hamming distance, which is the number of different characters in two strings of
     * equal length.
     *
     * @param array1 As described.
     * @param array2 As described.
     * @param start1 As described.
     * @param end1 As described.
     * @param start2 As described.
     * @param end2 As described.
     * @return As described.
     */
    public static int hammingDistance(
            final byte @NotNull [] array1,
            final byte @NotNull [] array2,
            final int start1,
            final int end1,
            final int start2,
            final int end2) {
        ensureStartEndValid(start1, end1, array1.length);
        ensureStartEndValid(start2, end2, array2.length);
        if (end2 - start2 != end1 - start1) {
            throw new IllegalArgumentException(
                    "Compared region length difference! Actual: [%d, %d) (%d) vs. [%d, %d) (%d)"
                            .formatted(start1, end1, end1 - start1, start2, end2, end2 - start2));
        }
        var reti = 0;
        for (int i = 0; i < end1 - start1; i++) {
            if (array1[start1 + i] != array2[start1 + i]) {
                reti++;
            }
        }
        return reti;
    }

    /**
     * Simple Levenshtein distance calculator without accelerations.
     *
     * <p>Generated with the help of TONGYI Lingma.
     *
     * @param array1 As described.
     * @param array2 As described.
     * @param start1 As described.
     * @param end1 As described.
     * @param start2 As described.
     * @param end2 As described.
     * @return As described.
     */
    public static int simpleLevenshteinDistance(
            final byte @NotNull [] array1,
            final byte @NotNull [] array2,
            final int start1,
            final int end1,
            final int start2,
            final int end2) {
        ensureStartEndValid(start1, end1, array1.length);
        ensureStartEndValid(start2, end2, array2.length);
        var array1Len = end1 - start1;
        var array2Len = end2 - start2;

        int[][] dp = new int[array1Len + 1][array2Len + 1];

        // Initialize the first row and column
        for (int i = 0; i <= array1Len; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= array2Len; j++) {
            dp[0][j] = j;
        }

        // Compute the Levenshtein distance
        for (int i = 1 + start1; i <= end1; i++) {
            for (int j = 1 + start2; j <= end2; j++) {
                if (array1[i - 1] == array2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }
        return dp[array1Len][array2Len];
    }

    /**
     * Full-length distance calculator.
     *
     * @param calculator Functions like {@link #simpleLevenshteinDistance(byte[], byte[], int, int, int, int)} or {@link #hammingDistance(byte[], byte[], int, int, int, int)}.
     * @param array1 As described.
     * @param array2 As described.
     * @return As described.
     */
    public static int fullLengthDistance(
            final @NotNull
                    Function6<byte[], byte[], Integer, Integer, Integer, Integer, Integer>
                            calculator,
            final byte @NotNull [] array1,
            final byte @NotNull [] array2) {
        return calculator.apply(array1, array2, 0, array1.length, 0, array2.length);
    }
}
