package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class StrDistanceTest {

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
