package com.github.yu_zhejian.ystr.match;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Implements the Z algorithm for string matching. Require an additional character
 * {@link ZMatch#sep} that does not appear in both needle and haystack.
 */
public final class ZMatch implements StrMatchInterface {
    /** As described. * */
    private final byte sep;

    /**
     * Default Constructor.
     *
     * @param sep As described.
     */
    public ZMatch(final byte sep) {
        this.sep = sep;
    }

    /**
     * {@code Z(s)} generates the length of the longest prefix of {@code s[i:l]} that matches a
     * prefix of {@code s}. In other words, for any {@code i \in [1, l)}, there are {@code s[0:z[i]]
     * == s[i:i+z[i]]}.
     *
     * <p>Specially, we have {@code z[0] == 0}.
     *
     * <p>For example:
     *
     * <pre>
     *     len = 12
     *     POS 0 1 2 3 4 5 6 7 8 9 0 1 2
     *     STR a a b c a a b c a a a a b
     *     Z   0 1 0 0 6 1 0 0 2 2 3 1 0
     * </pre>
     *
     * TODO: Understand the optimized method for Z-array generation.
     *
     * @param jointArr As described.
     * @return As described.
     */
    @Contract(pure = true)
    public static int @NotNull [] generateZArray(final byte @NotNull [] jointArr) {
        var z = new int[jointArr.length];
        int l = 0;
        int r = 0;
        for (int i = 1; i < jointArr.length; i++) {
            if (z[i - l] < r - i + 1) {
                // Case 1: i is within the Z-box
                z[i] = z[i - l];
            } else {
                // Case 2: i is outside or at the end of the Z-box
                z[i] = Integer.max(r - i + 1, 0);
                while (i + z[i] < jointArr.length && jointArr[z[i]] == jointArr[i + z[i]]) {
                    z[i]++;
                }
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }

    @Override
    public @NotNull IntList applyUnchecked(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end,
            final int limitTo) {
        var validLen = end - start;
        var jointArr = new byte[validLen + needle.length + 1];
        var numMatch = 0;
        System.arraycopy(needle, 0, jointArr, 0, needle.length);
        jointArr[validLen] = sep;
        System.arraycopy(haystack, start, jointArr, needle.length + 1, validLen);
        var z = generateZArray(jointArr);
        var occurrences = new IntArrayList();
        for (int i = needle.length + 1; i < jointArr.length; i++) {
            if (z[i] == needle.length) {
                occurrences.add(i - needle.length - 1 + start);
                numMatch++;
                if (limitTo == numMatch) {
                    return occurrences;
                }
            }
        }
        return occurrences;
    }
}
