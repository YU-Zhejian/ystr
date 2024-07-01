package com.github.yu_zhejian.ystr.match;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.NotNull;

/**
 * A somewhat improved {@link BruteForceMatch}.
 *
 * <p>This algorithm is brute-force. For demonstrative purposes only. It will be extremely slow, so
 * do not use it in production environments.
 *
 * <p>Use {@link ShiftOrMatch} for needles that are small enough.
 *
 * <ul>
 *   <li>Time complexity: {@code O(n^2)}.
 *   <li>Space complexity: {@code O(1)}.
 * </ul>
 */
public final class NaiveMatch implements StrMatchInterface {
    @Override
    public @NotNull IntList applyUnchecked(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        final var needleLen = needle.length;
        final var retl = new IntArrayList();

        var haystackPos = start;
        int needlePos;
        while (true) {
            // Find first match
            while (haystackPos + needleLen <= end && haystack[haystackPos] != needle[0]) {
                haystackPos += 1;
            }
            // Boundary check.
            if (haystackPos + needleLen > end) {
                break;
            }
            // Start from 2nd position since 1st position is of course match.
            needlePos = 1;
            while (needlePos < needleLen
                    && haystack[haystackPos + needlePos] == needle[needlePos]) {
                needlePos++;
            }
            if (needlePos == needleLen) {
                // No mismatch occurred.
                retl.add(haystackPos);
                haystackPos++;
            } else {
                haystackPos += needlePos;
            }
        }
        return retl;
    }
}
