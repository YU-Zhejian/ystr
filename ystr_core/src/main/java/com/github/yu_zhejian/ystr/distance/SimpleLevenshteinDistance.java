package com.github.yu_zhejian.ystr.distance;

import org.jetbrains.annotations.NotNull;

/** Simple Levenshtein distance calculator without accelerations. */
public final class SimpleLevenshteinDistance extends BaseDistance {
    /** Default initializer. */
    public SimpleLevenshteinDistance() {}

    @Override
    public long applyUnchecked(
            byte @NotNull [] string1,
            byte @NotNull [] string2,
            int start1,
            int end1,
            int start2,
            int end2) {
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
                if (string1[i - 1] == string2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }
        return dp[array1Len][array2Len];
    }
}
