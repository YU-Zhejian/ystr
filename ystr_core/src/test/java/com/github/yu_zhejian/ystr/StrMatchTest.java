package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.*;

import com.github.yu_zhejian.ystr.rolling.NtHash;
import com.github.yu_zhejian.ystr.rolling.PolynomialRollingHash;

import io.vavr.Function4;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/** TODO: Add test cases from <a href="https://github.com/smart-tool/smart/">...</a>. */
class StrMatchTest {
    static final Map<Tuple2<String, String>, List<Integer>> TEST_CASES_AGCT = Map.ofEntries(
            Map.entry(Tuple.of("ATTCCGTAAATTCCAAAATTCCGATTCTCC", "TTCC"), List.of(1, 10, 18)),
            Map.entry(
                    Tuple.of("AAAAAATTCCGTCCCCCAAAAACCCATTCCGTCCCAAAAAGGGTTT", "ATTCCGT"),
                    List.of(5, 25)),
            Map.entry(Tuple.of("AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT", "AAAAACCCCC"), List.of(0, 10)),
            Map.entry(Tuple.of("AAAA", "A"), List.of(0, 1, 2, 3)),
            Map.entry(Tuple.of("AAAA", "AA"), List.of(0, 1, 2)),
            Map.entry(Tuple.of("AAAA", "AAC"), List.of()),
            Map.entry(Tuple.of("ACTC", "ACTC"), List.of(0)),
            Map.entry(Tuple.of("AAAA", ""), List.of()),
            Map.entry(Tuple.of("", ""), List.of()));
    static final Map<Tuple2<String, String>, List<Integer>> TEST_CASES_STRANGE_ENCODING =
            Map.ofEntries(
                    Map.entry(
                            Tuple.of("\0\0\u00c0\0\0", "\0"),
                            List.of(0, 1, 4, 5)) // byte[] { 0, 0, -61, -128, 0, 0 }
                    );

    @Test
    void testIsMatch() {
        TEST_CASES_AGCT.forEach((key, value) -> {
            var hayStack = key._1().getBytes(StandardCharsets.UTF_8);
            var needle = key._2().getBytes(StandardCharsets.UTF_8);
            for (var i : value) {
                assertTrue(StrMatch.isMatch(hayStack, needle, i));
            }
        });
    }

    void testSS(
            @NotNull Function4<byte[], byte[], Integer, Integer, List<Integer>> function,
            @NotNull Map<Tuple2<String, String>, List<Integer>> testCases) {
        // Test edge cases and illegal inputs
        assertThrows(
                IllegalArgumentException.class,
                () -> function.apply(new byte[0], new byte[0], 1, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> function.apply(new byte[0], new byte[0], -1, 0));
        // Test real strings
        testCases.forEach((key, value) -> {
            var hayStack = key._1().getBytes(StandardCharsets.UTF_8);
            var needle = key._2().getBytes(StandardCharsets.UTF_8);
            assertIterableEquals(
                    value,
                    function.apply(hayStack, needle, 0, hayStack.length),
                    "Error at case %s in %s".formatted(key._2(), key._1()));
        });
    }

    @Test
    void bruteForceMatch() {
        testSS(StrMatch::bruteForceMatch, TEST_CASES_AGCT);
        testSS(StrMatch::bruteForceMatch, TEST_CASES_STRANGE_ENCODING);
    }

    @Test
    void naiveMatch() {
        testSS(StrMatch::naiveMatch, TEST_CASES_AGCT);
        testSS(StrMatch::naiveMatch, TEST_CASES_STRANGE_ENCODING);
    }

    @Test
    void rabinKarpMatch() {
        testSS(StrMatch::rabinKarpMatch, TEST_CASES_AGCT);
        testSS(StrMatch::rabinKarpMatch, TEST_CASES_STRANGE_ENCODING);
    }

    @Test
    void rabinKarpMatchUsingRandomPrime() {
        testSS(
                (haystack, needle, start, end) -> StrMatch.rabinKarpMatch(
                        haystack,
                        needle,
                        start,
                        end,
                        PolynomialRollingHash.supply(
                                PolynomialRollingHash.longRandomPrime(),
                                PolynomialRollingHash.DEFAULT_POLYNOMIAL_ROLLING_HASH_RADIX_P)),
                TEST_CASES_AGCT);
    }

    @Test
    void rabinKarpMatchUsingNtHash() {
        testSS(
                (haystack, needle, start, end) ->
                        StrMatch.rabinKarpMatch(haystack, needle, start, end, NtHash::new),
                TEST_CASES_AGCT);
    }

    /**
     * Test cases from <a
     * href="https://www.geeksforgeeks.org/kmp-algorithm-for-pattern-searching/?ref=lbp">here</a>.
     */
    @Test
    void lps() {
        assertArrayEquals(
                new int[] {0, 1, 2, 3}, StrMatch.lps("AAAA".getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(
                new int[] {0, 0, 0, 0, 0}, StrMatch.lps("ABCDE".getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(
                new int[] {0, 1, 0, 1, 2, 0, 1, 2, 3, 4, 5},
                StrMatch.lps("AABAACAABAA".getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(
                new int[] {0, 1, 2, 0, 1, 2, 3, 3, 3, 4},
                StrMatch.lps("AAACAAAAAC".getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(
                new int[] {0, 1, 2, 0, 1, 2, 3},
                StrMatch.lps("AAABAAA".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void knuthMorrisPrattMatch() {
        testSS(StrMatch::knuthMorrisPrattMatch, TEST_CASES_AGCT);
        testSS(StrMatch::knuthMorrisPrattMatch, TEST_CASES_STRANGE_ENCODING);
    }

    @Test
    void shiftOrMatch() {
        testSS(StrMatch::shiftOrMatch, TEST_CASES_AGCT);
        testSS(StrMatch::shiftOrMatch, TEST_CASES_STRANGE_ENCODING);
    }
}
