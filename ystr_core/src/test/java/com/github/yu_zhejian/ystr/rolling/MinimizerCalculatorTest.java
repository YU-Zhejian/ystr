package com.github.yu_zhejian.ystr.rolling;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

class MinimizerCalculatorTest {

    @Test
    void testDebugCase() {
        // 1, 2, 3, 4, 5, 6
        // 0  1  2  3  4  5
        var hashes = List.of(1L, 2L, 3L, 4L, 5L, 6L);
        var minHash4False = List.of(0, 1, 2);
        var minHash4True = List.of(0, 0, 0, 0, 1, 2, 3, 4, 5);
        assertIterableEquals(
                minHash4False, MinimizerCalculator.getMinimizerPositions(hashes, 4, false));
        assertIterableEquals(
                minHash4True, MinimizerCalculator.getMinimizerPositions(hashes, 4, true));
    }

    @Test
    void testNormalCase() {
        // 1, 2, 3, 4, 5, 4, 3, 2, 1, 2, 3, 4, 5
        // 0  1  2  3  4  5  6  7  8  9 10 11 12

        var hashes = List.of(1L, 2L, 3L, 4L, 5L, 4L, 3L, 2L, 1L, 2L, 3L, 4L, 5L);
        var minHash3 = List.of(0, 1, 2, 3, 6, 7, 8, 8, 8, 9, 10);
        assertIterableEquals(minHash3, MinimizerCalculator.getMinimizerPositions(hashes, 3, false));
    }

    @Test
    void testWithEndHash() {
        // 1, 2, 3, 2, 1, 2, 3
        // 0  1  2  3  4  5  6
        var hashes = List.of(1L, 2L, 3L, 2L, 1L, 2L, 3L);
        var minHash3 = List.of(0, 0, 0, 1, 4, 4, 4, 5, 6);
        assertIterableEquals(minHash3, MinimizerCalculator.getMinimizerPositions(hashes, 3, true));
    }

    @Test
    void testSmaller() {
        // 1, 2, 3
        // 0  1  2
        var hashes = List.of(1L, 2L, 3L);
        var minHash2 = List.of(0, 1);
        var minHash3 = List.of(0);
        var minHash4 = List.of();

        assertIterableEquals(minHash2, MinimizerCalculator.getMinimizerPositions(hashes, 2, false));

        assertIterableEquals(minHash3, MinimizerCalculator.getMinimizerPositions(hashes, 3, false));

        assertIterableEquals(minHash4, MinimizerCalculator.getMinimizerPositions(hashes, 4, false));
    }

    @Test
    void testSmallerWithEndHash() {
        // 1, 2, 3
        // 0  1  2
        var hashes = List.of(1L, 2L, 3L);
        var minHash2 = List.of(0, 0, 1, 2);
        var minHash3 = List.of(0, 0, 0, 1, 2);
        var minHash4 = List.of(0, 0, 0, 1, 2);
        assertIterableEquals(minHash2, MinimizerCalculator.getMinimizerPositions(hashes, 2, true));
        assertIterableEquals(minHash3, MinimizerCalculator.getMinimizerPositions(hashes, 3, true));
        assertIterableEquals(minHash4, MinimizerCalculator.getMinimizerPositions(hashes, 4, true));
    }
}
