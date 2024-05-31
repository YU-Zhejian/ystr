package com.github.yu_zhejian.ystr;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class StrMatch {
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
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    public static @NotNull List<Integer> bruteForceMatch(
            byte @NotNull [] haystack, byte @NotNull [] needle, int start, int end) {
        StrUtils.ensureStartEndValid(start, end, haystack.length);
        if (haystack.length == 0 || needle.length == 0) {
            return new ArrayList<>();
        }
        var pos = start;
        var retl = new ArrayList<Integer>();
        while (pos + needle.length <= end) {
            var isMatch = true;
            for (int i = 0; i < needle.length; i++) {
                if (haystack[pos + i] != needle[i]) {
                    isMatch = false;
                    break;
                }
            }
            if (isMatch) {
                retl.add(pos);
            }
            pos++;
        }
        return retl;
    }

    /**
     * A somewhat improved {@link #bruteForceMatch(byte[], byte[], int, int)}.
     *
     * <p>Warning, for demonstrative purposes only. Do not put it into production since it is ultra
     * slow.
     *
     * <ul>
     *   <li>Time complexity: {@code O(n^2)}.
     *   <li>Space complexity: {@code O(1)}.
     * </ul>
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    public static @NotNull List<Integer> naiveMatch(
            byte @NotNull [] haystack, byte @NotNull [] needle, int start, int end) {
        StrUtils.ensureStartEndValid(start, end, haystack.length);
        if (haystack.length == 0 || needle.length == 0) {
            return new ArrayList<>();
        }
        var pos = start;
        var posEnd = start + needle.length;
        var retl = new ArrayList<Integer>();
        while (posEnd <= end) {
            // Find first match
            while (haystack[pos] != needle[0] && posEnd <= end) {
                pos += 1;
                posEnd += 1;
            }
            int i;
            // Start from 2nd position since 1st position is of course match.
            for (i = 1; i < needle.length; i++) {
                if (haystack[pos + i] != needle[i]) {
                    break;
                }
            }
            if (i == needle.length) {
                retl.add(pos);
                pos++;
                posEnd++;
            } else {
                pos += i;
                posEnd += i;
            }
        }
        return retl;
    }

    private StrMatch() {}
}
