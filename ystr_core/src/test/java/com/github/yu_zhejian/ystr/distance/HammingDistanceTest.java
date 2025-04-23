package com.github.yu_zhejian.ystr.distance;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HammingDistanceTest {

    @Test
    void apply() {
        final var hammingDistance = new HammingDistance();
        assertEquals(0, hammingDistance.apply(new byte[0], new byte[0]));
        assertEquals(0, hammingDistance.apply(new byte[] {1, 2, 3}, new byte[] {1, 2, 3}));
        assertEquals(1, hammingDistance.apply(new byte[] {1, 2, 4}, new byte[] {1, 2, 3}));
        assertEquals(2, hammingDistance.apply(new byte[] {1, 2, 4}, new byte[] {1, 3, 3}));
        assertEquals(3, hammingDistance.apply(new byte[] {1, 2, 4}, new byte[] {4, 3, 3}));
        assertThrows(
                IllegalArgumentException.class,
                () -> hammingDistance.apply(new byte[] {1, 2, 4}, new byte[] {4, 3, 3, 4}));
    }
}
