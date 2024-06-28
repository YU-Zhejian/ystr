package com.github.yu_zhejian.ystr.match;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.NotNull;

/**
 * Boyer-Moore-Horspool algorithm uses improved bad character rule, making it to having comparable
 * performance wile not using good-character rule.
 *
 * @see <a
 *     href="https://www.cs.emory.edu/~cheung/Courses/253/Syllabus/Text/Matching-Boyer-Moore2.html">Java
 *     source</a>
 */
public class BoyerMooreHorspool extends BaseBoyerMoore {
    @Override
    public IntList apply(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        StrMatchUtils.ensureParametersValid(haystack, needle, start, end);
        if (haystack.length == 0 || needle.length == 0) {
            return new IntArrayList(0);
        }
        final var retl = new IntArrayList();

        // Establish the bad character rule table.
        final var occ = bmBadCharacterRule(needle);
        final var needleLen = needle.length;
        var haystackPos = start;
        int needlePos;
        while (haystackPos <= (end - needleLen)) {
            // Start at last position of neeedle
            needlePos = needleLen - 1;
            while (haystack[haystackPos + needlePos] == needle[needlePos]) {
                needlePos--; // Check "next" (= previous) character
                if (needlePos < 0) {
                    retl.add(haystackPos);
                    break;
                }
            }
            haystackPos = haystackPos
                    + (needleLen - 1)
                    - occ[haystack[haystackPos + (needleLen - 1)] & 0xFF];
        }
        return retl;
    }
}
