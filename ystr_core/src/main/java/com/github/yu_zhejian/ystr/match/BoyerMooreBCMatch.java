package com.github.yu_zhejian.ystr.match;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.NotNull;

/**
 * Simplified Boyer-Moore algorithm that uses bad character rule only.
 *
 * @see <a
 *     href="https://www.cs.emory.edu/~cheung/Courses/253/Syllabus/Text/Matching-Boyer-Moore2.html">Java
 *     source</a>
 */
public final class BoyerMooreBCMatch extends BaseBoyerMoore {
    @Override
    public @NotNull IntList applyUnchecked(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end,
            final int limitTo) {
        int numMatch = 0;
        final var retl = new IntArrayList();

        // Establish the bad character rule table.
        final var occ = bmBadCharacterRule(needle);
        final var needleLen = needle.length;
        var haystackPos = start;
        int needlePos;
        while (haystackPos <= (end - needleLen)) {
            // Start at last position of the needle
            needlePos = needleLen - 1;
            while (needlePos >= 0 && haystack[haystackPos + needlePos] == needle[needlePos]) {
                needlePos--; // Check the previous character.
            }
            if (needlePos == -1) {
                // Searched until the start of the needle, indicating a match.
                retl.add(haystackPos);

                numMatch++;
                if (limitTo == numMatch) {
                    return retl;
                }
                haystackPos += 1;
                continue;
            }
            // Not a match, we jump
            haystackPos += Integer.max(
                    1,
                    needlePos
                            - occ[
                                    haystack[haystackPos + needlePos]
                                            & StrUtils.BYTE_TO_UNSIGNED_MASK]);
        }
        return retl;
    }
}
