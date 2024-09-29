package com.github.yu_zhejian.ystr.match;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.NotNull;

/**
 * The plain old good brute-force.
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
public final class BruteForceMatch implements StrMatchInterface {

    @Override
    public @NotNull IntList applyUnchecked(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end,
            final int limitTo) {
        int numMatch = 0;
        var haystackPos = start;
        final var retl = new IntArrayList();
        while (haystackPos + needle.length <= end) {
            if (StrMatchUtils.isMatch(haystack, needle, haystackPos)) {
                retl.add(haystackPos);
                numMatch++;
                if (limitTo == numMatch) {
                    return retl;
                }
            }
            haystackPos++;
        }
        return retl;
    }
}
