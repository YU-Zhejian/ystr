package com.github.yu_zhejian.ystr.match;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.jetbrains.annotations.NotNull;

public final class StrMatchUtils {
    private StrMatchUtils() {}

    /**
     * Ensure {@code start} and {@code end} is valid on {@code haystack}, and is longer than
     * {@code needle}.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @throws IllegalArgumentException If otherwise.
     */
    public static void ensureParametersValid(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        StrUtils.ensureStartEndValid(start, end, haystack.length);
        if (needle.length == 0 || end - start == 0) {
            throw new IllegalArgumentException("needle or haystack is empty!");
        }
        if (needle.length > end - start) {
            throw new IllegalArgumentException(
                    "Needle length larger than valid haystack length! Are: %d vs. %d"
                            .formatted(needle.length, end - start));
        }
    }

    /**
     * Test whether a substring on {@code haystack} from {@code skipFirst} with length
     * {@code needle.length} matches {@code needle}.
     *
     * <p>This algorithm is brute-force. For demonstrative purposes only. It will be extremely slow,
     * so do not use it in production environments.
     *
     * <p>Use {@link ShiftOrMatch} for needles that are small enough.
     *
     * <ul>
     *   <li>Time complexity: {@code O(n^2)}.
     *   <li>Space complexity: {@code O(1)}.
     * </ul>
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @return As described.
     */
    public static boolean isMatch(
            final byte @NotNull [] haystack, final byte @NotNull [] needle, final int start) {
        for (int needlePos = 0; needlePos < needle.length; needlePos++) {
            if (haystack[needlePos + start] != needle[needlePos]) {
                return false;
            }
        }
        return true;
    }
}
