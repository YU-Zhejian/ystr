package com.github.yu_zhejian.ystr.distance;

import org.jetbrains.annotations.NotNull;

/**
 * For comparing 2 strings.
 */
public interface DistanceInterface {

    /**
     * Calculate distance between 2 strings without checking start/end.
     *
     * @param string1 As described.
     * @param string2 As described.
     * @param start1 As described.
     * @param end1 As described.
     * @param start2 As described.
     * @param end2 As described.
     * @return As described.
     */
    long applyUnchecked(
        final byte @NotNull [] string1,
        final byte @NotNull [] string2,
        final int start1,
        final int end1,
        final int start2,
        final int end2);

    /**
     * Calculate distance between 2 strings.
     *
     * @param string1 As described.
     * @param string2 As described.
     * @param start1 As described.
     * @param end1 As described.
     * @param start2 As described.
     * @param end2 As described.
     * @return As described.
     */
    long apply(
        final byte @NotNull [] string1,
        final byte @NotNull [] string2,
        final int start1,
        final int end1,
        final int start2,
        final int end2);
    /**
     * Calculate distance between 2 strings using full length.
     *
     * @param string1 As described.
     * @param string2 As described.
     * @return As described.
     */
    long apply(
        final byte @NotNull [] string1,
        final byte @NotNull [] string2);
}
