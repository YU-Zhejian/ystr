package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.*;

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
            Map.entry(Tuple.of("AAAA", ""), List.of()),
            Map.entry(Tuple.of("", ""), List.of()));

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
}
