package com.github.yu_zhejian.ystr.rolling_hash;

import static org.junit.jupiter.api.Assertions.*;

import com.github.yu_zhejian.ystr.StrUtils;

import io.vavr.Tuple;

import org.junit.jupiter.api.Test;

import java.util.List;

class MinimizerCalculatorTest {

    @Test
    void testDebugCase() {
        // 1, 2, 3, 4, 5, 6
        // 0  1  2  3  4  5
        var hashes = new long[] {1, 2, 3, 4, 5, 6};
        var minHash4False = List.of(
                Tuple.of(1L, 0), // 1, 2, 3, 4
                Tuple.of(2L, 1), // 2, 3, 4, 5
                Tuple.of(3L, 2) // 3, 4, 5, 6
                );
        var minHash4True = List.of(
                Tuple.of(1L, 0), // 1,
                Tuple.of(1L, 0), // 1, 2,
                Tuple.of(1L, 0), // 1, 2, 3,
                Tuple.of(1L, 0), // 1, 2, 3, 4
                Tuple.of(2L, 1), // 2, 3, 4, 5
                Tuple.of(3L, 2), // 3, 4, 5, 6
                Tuple.of(4L, 3), // 4, 5, 6
                Tuple.of(5L, 4), // 5, 6
                Tuple.of(6L, 5) // 6
                );
        assertIterableEquals(
                minHash4False,
                StrUtils.iterable(
                        new MinimizerCalculator(StrUtils.arrayToIterator(hashes), 4, 0, false)));
        assertIterableEquals(
                minHash4True,
                StrUtils.iterable(
                        new MinimizerCalculator(StrUtils.arrayToIterator(hashes), 4, 0, true)));
    }

    @Test
    void testNormalCase() {
        // 1, 2, 3, 4, 5, 4, 3, 2, 1, 2, 3, 4, 5
        // 0  1  2  3  4  5  6  7  8  9 10 11 12
        var hashes = new long[] {1, 2, 3, 4, 5, 4, 3, 2, 1, 2, 3, 4, 5};
        var minHash3 = List.of(
                Tuple.of(1L, 0), // 1, 2, 3
                Tuple.of(2L, 1), // 2, 3, 4
                Tuple.of(3L, 2), // 3, 4, 5
                Tuple.of(4L, 3), // 4, 5, 4
                Tuple.of(3L, 6), // 5, 4, 3
                Tuple.of(2L, 7), // 4, 3, 2
                Tuple.of(1L, 8), // 3, 2, 1
                Tuple.of(1L, 8), // 2, 1, 2
                Tuple.of(1L, 8), // 1, 2, 3
                Tuple.of(2L, 9), // 2, 3, 4
                Tuple.of(3L, 10) // 3, 4, 5
                );
        assertIterableEquals(
                minHash3,
                StrUtils.iterable(
                        new MinimizerCalculator(StrUtils.arrayToIterator(hashes), 3, 0, false)));
    }

    @Test
    void testWithEndHash() {
        // 1, 2, 3, 2, 1, 2, 3
        // 0  1  2  3  4  5  6
        var hashes = new long[] {1, 2, 3, 2, 1, 2, 3};
        var minHash3 = List.of(
                Tuple.of(1L, 0), // 1
                Tuple.of(1L, 0), // 1, 2
                Tuple.of(1L, 0), // 1, 2, 3
                Tuple.of(2L, 1), // 2, 3, 2
                Tuple.of(1L, 4), // 3, 2, 1
                Tuple.of(1L, 4), // 2, 1, 2
                Tuple.of(1L, 4), // 1, 2, 3
                Tuple.of(2L, 5), // 2, 3
                Tuple.of(3L, 6) // 3
                );
        assertIterableEquals(
                minHash3,
                StrUtils.iterable(
                        new MinimizerCalculator(StrUtils.arrayToIterator(hashes), 3, 0, true)));
    }

    @Test
    void testSmaller() {
        // 1, 2, 3
        // 0  1  2
        var hashes = new long[] {1, 2, 3};
        var minHash2 = List.of(
                Tuple.of(1L, 0), // 1, 2
                Tuple.of(2L, 1) // 2, 3
                );
        var minHash3 = List.of(
                Tuple.of(1L, 0) // 1, 2, 3
                );
        var minHash4 = List.of();
        assertIterableEquals(
                minHash2,
                StrUtils.iterable(
                        new MinimizerCalculator(StrUtils.arrayToIterator(hashes), 2, 0, false)));
        assertIterableEquals(
                minHash3,
                StrUtils.iterable(
                        new MinimizerCalculator(StrUtils.arrayToIterator(hashes), 3, 0, false)));
        assertIterableEquals(
                minHash4,
                StrUtils.iterable(
                        new MinimizerCalculator(StrUtils.arrayToIterator(hashes), 4, 0, false)));
    }

    @Test
    void testSmallerWithEndHash() {
        // 1, 2, 3
        // 0  1  2
        var hashes = new long[] {1, 2, 3};
        var minHash2 = List.of(
                Tuple.of(1L, 0), // 1
                Tuple.of(1L, 0), // 1, 2
                Tuple.of(2L, 1), // 2, 3
                Tuple.of(3L, 2) // 3
                );
        var minHash3 = List.of(
                Tuple.of(1L, 0), // 1
                Tuple.of(1L, 0), // 1, 2
                Tuple.of(1L, 0), // 1, 2, 3
                Tuple.of(2L, 1), // 2, 3
                Tuple.of(3L, 2) // 3
                );
        var minHash4 = List.of(
                Tuple.of(1L, 0), // 1
                Tuple.of(1L, 0), // 1, 2
                Tuple.of(1L, 0), // 1, 2, 3
                Tuple.of(2L, 1), // 2, 3
                Tuple.of(3L, 2) // 3
                );
        assertIterableEquals(
                minHash2,
                StrUtils.iterable(
                        new MinimizerCalculator(StrUtils.arrayToIterator(hashes), 2, 0, true)));
        assertIterableEquals(
                minHash3,
                StrUtils.iterable(
                        new MinimizerCalculator(StrUtils.arrayToIterator(hashes), 3, 0, true)));
        assertIterableEquals(
                minHash4,
                StrUtils.iterable(
                        new MinimizerCalculator(StrUtils.arrayToIterator(hashes), 4, 0, true)));
    }
}
