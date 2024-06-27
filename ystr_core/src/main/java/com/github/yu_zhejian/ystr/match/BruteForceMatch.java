package com.github.yu_zhejian.ystr.match;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.NotNull;

/**
 * The plain old good brute-force.
 *
 * <p>Warning, for demonstrative purposes only. Do not put it into production since it is ultra
 * slow.
 *
 * <ul>
 *   <li>Time complexity: {@code O(n^2)}.
 *   <li>Space complexity: {@code O(1)}.
 * </ul>
 */
public final class BruteForceMatch implements StrMatchInterface {

    @Override
    public @NotNull IntList apply(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        StrMatchUtils.ensureParametersValid(haystack, needle, start, end);
        if (haystack.length == 0 || needle.length == 0) {
            return new IntArrayList();
        }
        var haystackPos = start;
        final var retl = new IntArrayList();
        while (haystackPos + needle.length <= end) {
            if (StrMatchUtils.isMatch(haystack, needle, haystackPos)) {
                retl.add(haystackPos);
            }
            haystackPos++;
        }
        return retl;
    }
}
