package com.github.yu_zhejian.ystr.rolling;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.yu_zhejian.ystr.utils.IterUtils;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

class NtShannonEntropyTest {
    void testNtShannonEntropy(@NotNull List<Double> expected, @NotNull String string, int k) {
        var nth = new NtShannonEntropy();
        nth.attach(string.getBytes(StandardCharsets.UTF_8), k);
        var result = IterUtils.collect(nth);
        nth.detach();
        assertEquals(expected.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(expected.get(i), result.getDouble(i), 0.0, "");
        }
    }

    @Test
    void test() {
        testNtShannonEntropy(List.of(0.0), "NNNN", 4);
        testNtShannonEntropy(List.of(Math.log(4)), "AGCT", 4);
        testNtShannonEntropy(List.of(0.0, 0.0), "NANN", 3);
        testNtShannonEntropy(List.of(0.0, 0.0), "AAAA", 3);
        testNtShannonEntropy(List.of(0.0, 0.6365141682948128), "AAAT", 3);
        testNtShannonEntropy(
                List.of(
                        0.0,
                        0.6365141682948128,
                        1.0986122886681096,
                        1.0986122886681096,
                        0.6365141682948128,
                        0.6365141682948128,
                        1.0986122886681096,
                        0.0),
                "AAATCGCGA\0",
                3);
    }
}
