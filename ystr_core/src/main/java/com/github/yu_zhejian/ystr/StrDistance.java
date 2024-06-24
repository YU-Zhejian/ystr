package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import io.vavr.Function6;

import org.jetbrains.annotations.NotNull;

/** Algorithms for distances between strings. */
public final class StrDistance {
    private StrDistance() {}

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
        StrUtils.ensureStartEndValid(start1, end1, array1.length);
        StrUtils.ensureStartEndValid(start2, end2, array2.length);
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
        StrUtils.ensureStartEndValid(start1, end1, array1.length);
        StrUtils.ensureStartEndValid(start2, end2, array2.length);
        final var array1Len = end1 - start1;
        final var array2Len = end2 - start2;

        final int[][] dp = new int[array1Len + 1][array2Len + 1];

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
     * @param calculator Functions like {@link #simpleLevenshteinDistance(byte[], byte[], int, int,
     *     int, int)} or {@link #hammingDistance(byte[], byte[], int, int, int, int)}.
     * @param array1 As described.
     * @param array2 As described.
     * @return As described.
     */
    public static int fullLengthDistance(
            final @NotNull Function6<byte[], byte[], Integer, Integer, Integer, Integer, Integer>
                            calculator,
            final byte @NotNull [] array1,
            final byte @NotNull [] array2) {
        return calculator.apply(array1, array2, 0, array1.length, 0, array2.length);
    }
}
