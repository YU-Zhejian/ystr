package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.rolling_hash.PolynomialRollingHash;
import com.github.yu_zhejian.ystr.rolling_hash.RollingHashFactory;
import com.github.yu_zhejian.ystr.rolling_hash.RollingHashInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * String matching problem in the form of "finding {@code needle} inside a substring of a
 * {@code haystack}".
 */
public final class StrMatch {

    /**
     * Test whether a substring on {@code haystack} from {@code start} with length
     * {@code needle.length} matehes {@code needle}.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @return As described.
     */
    public static boolean isMatch(byte @NotNull [] haystack, byte @NotNull [] needle, int start) {
        for (int i = 0; i < needle.length; i++) {
            if (haystack[i + start] != needle[i]) {
                return false;
            }
        }
        return true;
    }

    private static void ensureParametersValid(
            byte @NotNull [] haystack, byte @NotNull [] needle, int start, int end) {
        StrUtils.ensureStartEndValid(start, end, haystack.length);
        if (needle.length > end - start) {
            throw new IllegalArgumentException(
                    "Needle length larger than valid haystack length! Are: %d vs. %d"
                            .formatted(needle.length, end - start));
        }
    }

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
        ensureParametersValid(haystack, needle, start, end);
        if (haystack.length == 0 || needle.length == 0) {
            return new ArrayList<>();
        }
        var pos = start;
        var retl = new ArrayList<Integer>();
        while (pos + needle.length <= end) {
            if (isMatch(haystack, needle, pos)) {
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
        ensureParametersValid(haystack, needle, start, end);
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

    public static <T extends RollingHashInterface> @NotNull List<Integer> rabinKarpMatch(
            byte @NotNull [] haystack,
            byte @NotNull [] needle,
            int start,
            int end,
            Class<T> rollingHashClaz,
            Object... rollingHashParams) {
        ensureParametersValid(haystack, needle, start, end);
        if (haystack.length == 0 || needle.length == 0) {
            return new ArrayList<>();
        }
        // Create initial hash
        var pos = start;
        var rhHaystack = RollingHashFactory.newRollingHash(
                rollingHashClaz, haystack, needle.length, start, rollingHashParams);
        var needleHash = RollingHashFactory.newRollingHash(
                        rollingHashClaz, needle, needle.length, 0, rollingHashParams)
                .next();

        var retl = new ArrayList<Integer>();
        while (pos + needle.length <= end) {
            var nextHash = rhHaystack.next();
            if (Objects.equals(nextHash, needleHash)) {
                if (isMatch(haystack, needle, pos)) {
                    retl.add(pos);
                }
            }
            pos++;
        }
        return retl;
    }

    public static @NotNull List<Integer> rabinKarpMatch(
            byte @NotNull [] haystack, byte @NotNull [] needle, int start, int end) {
        return rabinKarpMatch(haystack, needle, start, end, PolynomialRollingHash.class);
    }

    private StrMatch() {}
}
