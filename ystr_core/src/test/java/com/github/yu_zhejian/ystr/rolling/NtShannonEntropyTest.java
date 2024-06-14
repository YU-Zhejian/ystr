package com.github.yu_zhejian.ystr.rolling;

import static org.junit.jupiter.api.Assertions.*;

import com.github.yu_zhejian.ystr.IterUtils;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

class NtShannonEntropyTest {
    void testNtShannonEntropy(@NotNull List<Double> expected, @NotNull String bases, int k) {
        var nth = new NtShannonEntropy(bases.getBytes(StandardCharsets.UTF_8), k, 0);
        var result = IterUtils.collect(nth);
        for (var i = 0; i < result.size(); i++) {
            assertEquals(expected.get(i), result.get(i), 0.0, "");
        }
    }

    @Test
    void test() {
        testNtShannonEntropy(List.of(0.0), "NNNN", 4);
        testNtShannonEntropy(List.of(Math.log(4)), "AGCT", 4);
        testNtShannonEntropy(List.of(0.0, 0.0), "NANN", 3);
        testNtShannonEntropy(List.of(0.0, 0.0), "AAAA", 3);
        testNtShannonEntropy(List.of(0.0, 0.6365141682948128), "AAAT", 3);
    }
}
