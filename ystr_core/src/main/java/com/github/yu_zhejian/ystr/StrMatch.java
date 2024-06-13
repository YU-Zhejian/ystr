package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.rolling.PolynomialRollingHash;
import com.github.yu_zhejian.ystr.rolling.RollingHashFactory;
import com.github.yu_zhejian.ystr.rolling.RollingHashInterface;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * String matching problem in the form of "finding {@code needle} inside a substring of a
 * {@code haystack}". Will return a list of all identified substrings.
 */
public final class StrMatch {

    /**
     * Test whether a substring on {@code haystack} from {@code skipFirst} with length
     * {@code needle.length} matches {@code needle}.
     *
     * <p>This algorithm is brute-force.
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

    private static void ensureParametersValid(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
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
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        ensureParametersValid(haystack, needle, start, end);
        if (haystack.length == 0 || needle.length == 0) {
            return List.of();
        }
        var haystackPos = start;
        final var retl = new IntArrayList();
        while (haystackPos + needle.length <= end) {
            if (isMatch(haystack, needle, haystackPos)) {
                retl.add(haystackPos);
            }
            haystackPos++;
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
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        ensureParametersValid(haystack, needle, start, end);
        if (haystack.length == 0 || needle.length == 0) {
            return List.of();
        }
        final var needleLen = needle.length;
        var haystackPos = start;
        final var retl = new IntArrayList();
        while (true) {
            // Find first match
            while (haystackPos + needleLen <= end && haystack[haystackPos] != needle[0]) {
                haystackPos += 1;
            }
            if (haystackPos + needleLen >= end) {
                break;
            }
            int needlePos;
            // Start from 2nd position since 1st position is of course match.
            for (needlePos = 1; needlePos < needleLen; needlePos++) {
                if (haystack[haystackPos + needlePos] != needle[needlePos]) {
                    break;
                }
            }
            if (needlePos == needleLen) {
                retl.add(haystackPos);
                haystackPos++;
            } else {
                haystackPos += needlePos;
            }
        }
        return retl;
    }

    /**
     * A full-born working Rabin-Karp matcher.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @param rollingHashClaz Class that is passed to {@link RollingHashFactory}.
     * @param rollingHashParams Other parameters used to initialize {@code T}.
     * @return As described.
     * @param <T> Some Rabin-Karp-compatible rolling hash calculator.
     */
    public static <T extends RollingHashInterface> @NotNull List<Integer> rabinKarpMatch(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end,
            final Class<T> rollingHashClaz,
            final Object... rollingHashParams) {
        ensureParametersValid(haystack, needle, start, end);
        if (haystack.length == 0 || needle.length == 0) {
            return List.of();
        }
        // Create initial hash
        var haystackPos = start;
        final var needleLen = needle.length;
        final var rhHaystack = RollingHashFactory.newRollingHash(
                rollingHashClaz, haystack, needleLen, start, rollingHashParams);
        final var needleHash = RollingHashFactory.newRollingHash(
                        rollingHashClaz, needle, needleLen, 0, rollingHashParams)
                .nextUnboxed();

        final var retl = new IntArrayList();
        while (haystackPos + needleLen <= end) {
            final var nextHash = rhHaystack.nextUnboxed();
            if (nextHash == needleHash && isMatch(haystack, needle, haystackPos)) {
                retl.add(haystackPos);
            }
            haystackPos++;
        }
        return retl;
    }

    /**
     * {@link #rabinKarpMatch(byte[], byte[], int, int, Class, Object...)} with
     * {@link PolynomialRollingHash} using its default version for rolling hash.
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    public static @NotNull List<Integer> rabinKarpMatch(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        return rabinKarpMatch(haystack, needle, start, end, PolynomialRollingHash.class);
    }

    /**
     * LPS -- Longest proper Prefix which is also the Suffix.
     *
     * <p>An array of integers, each meaning the length of the longest proper prefix, which is also
     * a proper suffix, at {@code s[0: k]}.
     *
     * <p>That is, for any {@code k} in range {@code [0, l)}, {@code s[0: ips[k]] == s[k - ips[k] +
     * 1: k + 1]}.
     *
     * <p><b>Implementation</b> All notations below are 0-based. All intervals are cose-open
     * intervals.
     *
     * <p>Given {@code ips[i]}. The following properties can be assured:
     *
     * <ol>
     *   <li>We say {@code ips[i + 1] = ips[i] + 1} if {@code s[ips[i]] == s[i + 1]}.
     *       <p>Proof:
     *       <ol>
     *         <li>From {@code ips[i]}, we can by definition know that {@code s[0: ips[i]] == s[i -
     *             ips[i] + 1: i + 1]}.
     *         <li>If {@code s[ips[i]] == s[i + 1]}, we will have {@code s[0: ips[i] + 1] == s[i -
     *             ips[i] + 1: i + 2]}.
     *         <li>Replacing {@code ips[i] + 1} as {@code ips[i + 1]}, we get {@code s[0: ips[i + 1]
     *             == s[i - ips[i + 1] + 2: i + 2]}.
     *         <li>Which by definition proves {@code ips[i + 1] = ips[i] + 1}.
     *       </ol>
     *   <li>If {@code s[ips[i]] != s[i]}, we would found the second largest number {@code j} in
     *       range {@code [0, i + 1)} that allows {@code s[0: j] == s[i + 1 - j: i + 1]}.
     *       <p>Then we only need to compare {@code s[j]} and {@code s[i + 1]}.
     *       <ul>
     *         <li>If equal, we would have {@code s[0: j + 1] == s[i + 1 - j: i + 2}.
     *         <li>Otherwise, we need to find another smaller {@code j}.
     *       </ul>
     *       <p>Since {@code s[i - lps[i] + 1: i + 1] == s[0: lps[i]] == s[i - lps[i] + 1: i + 1] +
     *       lps[i] - i - 1}, for {@code j < i},
     *       <pre>
     *  s[0: j]
     * = s[i - j + 1: i + 1] // By definition of {@code lps[j]}
     * = s[i - j + 1: i + 1] + lps[i] - i - 1 // By definition of {@code lps[i]}
     * = s[lps[i] - j: lps[j]]
     *         </pre>
     *       So, {@code j} would be the {@code lps} of {@code s[0: lps[i]]}. So, next valid
     *       {@code j} is {@code lps[lps[i] - 1]}.
     *       <p>If we need to further search for smaller {@code j}, this rule applies until
     *       {@code j} reaches 0.
     * </ol>
     *
     * <p><b>Example</b>
     *
     * <pre>
     *     l = 10
     * s   A A A C A A A A A C
     * idx 0 1 2 3 4 5 6 7 8 9
     * ips 0 1 2 0 1 2 3 3 3 4
     * </pre>
     *
     * <pre>
     *     l = 10
     * s   A B A B C A B A B C
     * idx 0 1 2 3 4 5 6 7 8 9
     * ips 0 0 1 2 0 1 2 3 4 5
     * </pre>
     *
     * <pre>
     *     l = 15
     * s   T  C  C  C  G  A  G  T  C  C  A  A  T  C  C
     * idx 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14
     * ips 0  0  0  0  0  0  0  1  2  3  0  0  1  2  3
     * </pre>
     *
     * @param string As described. Assumed to be no-empty.
     * @return As described.
     * @see <a href="https://oi-wiki.org/string/kmp/">OIWiki</a>. Note that they do not use the same
     *     notation as ours.
     */
    @Contract(value = "_ -> new", pure = true)
    public static int @NotNull [] lps(final byte @NotNull [] string) {
        final var retl = new int[string.length];
        retl[0] = 0; // By definition
        for (int i = 1; i < string.length; i++) {
            int j = retl[i - 1];
            while (j > 0 && string[i] != string[j]) {
                j = retl[j - 1];
            }
            if (string[i] == string[j]) {
                j++;
            }
            retl[i] = j;
        }
        return retl;
    }

    /**
     * The standard KMP algorithm.
     *
     * <p><b>References</b>
     *
     * <ul>
     *   <li>D. E. Knuth, J. H. Morris, Jr., and V. R. Pratt, “Fast Pattern Matching in Strings,”
     *       SIAM J. Comput., vol. 6, no. 2, pp. 323–350, Jun. 1977, <a
     *       href="https://doi.org/10.1137/0206024">DOI</a>
     *   <li>Introduction in <a href="https://en.oi-wiki.org/string/kmp/#_6">OIWiki</a>.
     *   <li>Implementation at <a
     *       href="https://www.scaler.com/topics/data-structures/kmp-algorithm/">Scaler Topics</a>
     * </ul>
     *
     * @param haystack As described.
     * @param needle As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    public static @NotNull List<Integer> knuthMorrisPrattMatch(
            final byte @NotNull [] haystack,
            final byte @NotNull [] needle,
            final int start,
            final int end) {
        ensureParametersValid(haystack, needle, start, end);
        if (haystack.length == 0 || needle.length == 0) {
            return List.of();
        }
        final var retl = new IntArrayList();
        final var lpsNeedle = lps(needle);
        var needlePos = 0;
        var haystackPos = start;
        final var needleLen = needle.length;
        while (haystackPos < end) {
            if (needle[needlePos] == haystack[haystackPos]) {
                needlePos++;
                haystackPos++;
            }
            if (needlePos == needleLen) {
                retl.add(haystackPos - needlePos);
                needlePos = lpsNeedle[needlePos - 1];
            } else if (haystackPos < end && needle[needlePos] != haystack[haystackPos]) {
                if (needlePos != 0) {
                    needlePos = lpsNeedle[needlePos - 1];
                } else {
                    haystackPos += 1;
                }
            }
        }
        return retl;
    }

    private StrMatch() {}
}
