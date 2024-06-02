package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.*;

import com.github.yu_zhejian.ystr.rolling_hash.NtHash;
import com.github.yu_zhejian.ystr.rolling_hash.PolynomialRollingHash;

import io.vavr.Function4;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

class StrMatchTest {
    static Map<Tuple2<String, String>, List<Integer>> TEST_CASES_AGCT = Map.ofEntries(
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

    void testSS(@NotNull Function4<byte[], byte[], Integer, Integer, List<Integer>> function) {
        // Test edge cases and illegal inputs
        assertThrows(
                IllegalArgumentException.class,
                () -> function.apply(new byte[0], new byte[0], 1, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> function.apply(new byte[0], new byte[0], -1, 0));
        // Test real strings
        TEST_CASES_AGCT.forEach((key, value) -> {
            var hayStack = key._1().getBytes(StandardCharsets.UTF_8);
            var needle = key._2().getBytes(StandardCharsets.UTF_8);
            assertIterableEquals(value, function.apply(hayStack, needle, 0, hayStack.length));
        });
    }

    @Test
    void bruteForceMatch() {
        testSS(StrMatch::bruteForceMatch);
    }

    @Test
    void naiveMatch() {
        testSS(StrMatch::naiveMatch);
    }

    @Test
    void rabinKarpMatch() {
        testSS(StrMatch::rabinKarpMatch);
    }

    @Test
    void rabinKarpMatchUsingRandomPrime() {
        testSS((haystack, needle, start, end) -> StrMatch.rabinKarpMatch(
                haystack,
                needle,
                start,
                end,
                PolynomialRollingHash.class,
                PolynomialRollingHash.longRandomPrime(),
                128L));
    }

    @Test
    void rabinKarpMatchUsingNtHash() {
        testSS((haystack, needle, start, end) ->
                StrMatch.rabinKarpMatch(haystack, needle, start, end, NtHash.class));
    }
}
