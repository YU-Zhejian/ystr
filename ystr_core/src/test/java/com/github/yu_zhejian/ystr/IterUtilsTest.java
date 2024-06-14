package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.Test;

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
    }
}
