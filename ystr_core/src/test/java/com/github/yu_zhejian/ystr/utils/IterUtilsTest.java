package com.github.yu_zhejian.ystr.utils;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class IterUtilsTest {

    @Test
    void dedup() {
        assertIterableEquals(List.of(), IterUtils.dedup(List.of()));
        assertIterableEquals(List.of(1), IterUtils.dedup(List.of(1)));
        assertIterableEquals(List.of(1), IterUtils.dedup(List.of(1, 1)));
        assertIterableEquals(List.of(1, 2), IterUtils.dedup(List.of(1, 1, 2)));
        assertIterableEquals(List.of(1, 2), IterUtils.dedup(List.of(1, 1, 2, 2, 2)));
        assertIterableEquals(List.of(1, 2, 3), IterUtils.dedup(List.of(1, 1, 2, 2, 2, 3)));
        assertIterableEquals(List.of(1, 2, 3), IterUtils.dedup(List.of(1, 1, 2, 2, 2, 3)));
        assertIterableEquals(List.of(1, 2, 3, 2, 3), IterUtils.dedup(List.of(1, 1, 2, 3, 2, 3)));
    }

    @Test
    void collect() {
        assertIterableEquals(List.of(), IterUtils.collect(Collections.emptyIterator()));
        assertIterableEquals(List.of(1), IterUtils.collect(List.of(1).iterator()));
        assertIterableEquals(
                List.of(1, 3, 4), IterUtils.collect(List.of(1, 3, 4).iterator()));
    }

    @Test
    void window() {
        assertIterableEquals(
                List.of(List.of(1, 2), List.of(3, 4)),
                IterUtils.collect(IterUtils.window(List.of(1, 2, 3, 4).iterator(), 2)));
        assertIterableEquals(
                List.of(List.of(1), List.of(2), List.of(3)),
                IterUtils.collect(IterUtils.window(List.of(1, 2, 3).iterator(), 1)));
        assertIterableEquals(
                List.of(List.of(1, 2), List.of(3)),
                IterUtils.collect(IterUtils.window(List.of(1, 2, 3).iterator(), 2)));
        assertIterableEquals(
                List.of(List.of(1, 2, 3)),
                IterUtils.collect(IterUtils.window(List.of(1, 2, 3).iterator(), 3)));
        assertIterableEquals(
                List.of(List.of(1, 2, 3)),
                IterUtils.collect(IterUtils.window(List.of(1, 2, 3).iterator(), 4)));
    }
}
