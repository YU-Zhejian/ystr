package com.github.yu_zhejian.ystr.distance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class SimpleLevenshteinDistanceTest {

    /** Generated with help of TONGYI Lingma. */
    @Test
    void simpleLevenshteinDistance() {
        final var simpleLevenshteinDistance = new SimpleLevenshteinDistance();

        assertEquals(
                3,
                simpleLevenshteinDistance.apply(
                        "kitten".getBytes(StandardCharsets.UTF_8),
                        "sitting".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                8,
                simpleLevenshteinDistance.apply(
                        "rosettacode".getBytes(StandardCharsets.UTF_8),
                        "raisethysword".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                5,
                simpleLevenshteinDistance.apply(
                        "hello".getBytes(StandardCharsets.UTF_8),
                        "".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                0,
                simpleLevenshteinDistance.apply(
                        "same".getBytes(StandardCharsets.UTF_8),
                        "same".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                0,
                simpleLevenshteinDistance.apply(
                        "".getBytes(StandardCharsets.UTF_8), "".getBytes(StandardCharsets.UTF_8)));
    }
}
