package com.github.yu_zhejian.ystr.match;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.yu_zhejian.ystr.container.Tuple;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class StrMatchUtilsTest {

    public static final Map<Tuple.Tuple2<String, String>, List<Integer>> TEST_CASES_AGCT =
            Map.ofEntries(
                    Map.entry(
                            Tuple.of("ATTCCGTAAATTCCAAAATTCCGATTCTCC", "TTCC"), List.of(1, 10, 18)),
                    Map.entry(
                            Tuple.of("AAAAAATTCCGTCCCCCAAAAACCCATTCCGTCCCAAAAAGGGTTT", "ATTCCGT"),
                            List.of(5, 25)),
                    Map.entry(
                            Tuple.of("AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT", "AAAAACCCCC"),
                            List.of(0, 10)),
                    Map.entry(Tuple.of("AAAA", "A"), List.of(0, 1, 2, 3)),
                    Map.entry(Tuple.of("AAAA", "AA"), List.of(0, 1, 2)),
                    Map.entry(Tuple.of("AAAA", "AAC"), List.of()),
                    Map.entry(Tuple.of("ACTC", "ACTC"), List.of(0)));
    public static final Map<Tuple.Tuple2<String, String>, List<Integer>>
            TEST_CASES_STRANGE_ENCODING = Map.ofEntries(
                    Map.entry(
                            Tuple.of("\0\0\u00c0\0\0", "\0"),
                            List.of(0, 1, 4, 5)) // byte[] { 0, 0, -61, -128, 0, 0 }
                    );

    @Test
    void testIsMatch() {
        TEST_CASES_AGCT.forEach((key, value) -> {
            var hayStack = key.e1().getBytes(StandardCharsets.UTF_8);
            var needle = key.e2().getBytes(StandardCharsets.UTF_8);
            for (var i : value) {
                assertTrue(StrMatchUtils.isMatch(hayStack, needle, i));
            }
        });
    }
}
