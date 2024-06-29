package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class StrDistanceTest {

    @Test
    void hammingDistance() {
        assertEquals(
                0,
                StrDistance.fullLengthDistance(
                        StrDistance::hammingDistance, new byte[0], new byte[0]));
        assertEquals(
                0,
                StrDistance.fullLengthDistance(
                        StrDistance::hammingDistance, new byte[] {1, 2, 3}, new byte[] {1, 2, 3}));
        assertEquals(
                1,
                StrDistance.fullLengthDistance(
                        StrDistance::hammingDistance, new byte[] {1, 2, 4}, new byte[] {1, 2, 3}));
        assertEquals(
                2,
                StrDistance.fullLengthDistance(
                        StrDistance::hammingDistance, new byte[] {1, 2, 4}, new byte[] {1, 3, 3}));
        assertEquals(
                3,
                StrDistance.fullLengthDistance(
                        StrDistance::hammingDistance, new byte[] {1, 2, 4}, new byte[] {4, 3, 3}));
        assertThrows(
                IllegalArgumentException.class,
                () -> StrDistance.fullLengthDistance(
                        StrDistance::hammingDistance, new byte[] {1, 2, 4}, new byte[] {4, 3, 3, 4
                        }));
    }

    /** Generated with help of TONGYI Lingma. */
    @Test
    void simpleLevenshteinDistance() {
        assertEquals(
                3,
                StrDistance.fullLengthDistance(
                        StrDistance::simpleLevenshteinDistance,
                        "kitten".getBytes(StandardCharsets.UTF_8),
                        "sitting".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                8,
                StrDistance.fullLengthDistance(
                        StrDistance::simpleLevenshteinDistance,
                        "rosettacode".getBytes(StandardCharsets.UTF_8),
                        "raisethysword".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                5,
                StrDistance.fullLengthDistance(
                        StrDistance::simpleLevenshteinDistance,
                        "hello".getBytes(StandardCharsets.UTF_8),
                        "".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                0,
                StrDistance.fullLengthDistance(
                        StrDistance::simpleLevenshteinDistance,
                        "same".getBytes(StandardCharsets.UTF_8),
                        "same".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                0,
                StrDistance.fullLengthDistance(
                        StrDistance::simpleLevenshteinDistance,
                        "".getBytes(StandardCharsets.UTF_8),
                        "".getBytes(StandardCharsets.UTF_8)));
    }
}
