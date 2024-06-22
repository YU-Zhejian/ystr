package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class StrUtilsTest {

    @Test
    void strcmp() {
        assertEquals(
                0,
                StrUtils.strcmp(
                        "AGCT".getBytes(StandardCharsets.UTF_8),
                        "AGCT".getBytes(StandardCharsets.UTF_8)));
        assertTrue(StrUtils.strcmp(
                        "AGCTA".getBytes(StandardCharsets.UTF_8),
                        "AGCT".getBytes(StandardCharsets.UTF_8))
                > 0);
        assertTrue(StrUtils.strcmp(
                        "AGCTA".getBytes(StandardCharsets.UTF_8),
                        "AGCTT".getBytes(StandardCharsets.UTF_8))
                < 0);
    }

    @Test
    void pow() {
        assertEquals(2, StrUtils.pow(2, 1));
        assertEquals(1, StrUtils.pow(1, 1));
        assertEquals(4, StrUtils.pow(2, 2));
        assertEquals(8, StrUtils.pow(2, 3));
        assertEquals(9, StrUtils.pow(3, 2));
        assertEquals(1, StrUtils.pow(3, 0));
        assertEquals(0, StrUtils.pow(0, 3));
        assertEquals(1, StrUtils.pow(0, 0));

        assertEquals(2L, StrUtils.pow(2L, 1));
        assertEquals(1L, StrUtils.pow(1L, 1));
        assertEquals(4L, StrUtils.pow(2L, 2));
        assertEquals(8L, StrUtils.pow(2L, 3));
        assertEquals(9L, StrUtils.pow(3L, 2));
        assertEquals(1L, StrUtils.pow(3L, 0));
        assertEquals(0L, StrUtils.pow(0L, 3));
        assertEquals(1L, StrUtils.pow(0L, 0));
    }

    @Test
    void hammingDistance() {
        assertEquals(
                0,
                StrUtils.fullLengthDistance(StrUtils::hammingDistance, new byte[0], new byte[0]));
        assertEquals(
                0,
                StrUtils.fullLengthDistance(
                        StrUtils::hammingDistance, new byte[] {1, 2, 3}, new byte[] {1, 2, 3}));
        assertEquals(
                1,
                StrUtils.fullLengthDistance(
                        StrUtils::hammingDistance, new byte[] {1, 2, 4}, new byte[] {1, 2, 3}));
        assertEquals(
                2,
                StrUtils.fullLengthDistance(
                        StrUtils::hammingDistance, new byte[] {1, 2, 4}, new byte[] {1, 3, 3}));
        assertEquals(
                3,
                StrUtils.fullLengthDistance(
                        StrUtils::hammingDistance, new byte[] {1, 2, 4}, new byte[] {4, 3, 3}));
        assertThrows(
                IllegalArgumentException.class,
                () -> StrUtils.fullLengthDistance(
                        StrUtils::hammingDistance, new byte[] {1, 2, 4}, new byte[] {4, 3, 3, 4}));
    }

    /** Generated with help of TONGYI Lingma. */
    @Test
    void simpleLevenshteinDistance() {
        assertEquals(
                3,
                StrUtils.fullLengthDistance(
                        StrUtils::simpleLevenshteinDistance,
                        "kitten".getBytes(StandardCharsets.UTF_8),
                        "sitting".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                8,
                StrUtils.fullLengthDistance(
                        StrUtils::simpleLevenshteinDistance,
                        "rosettacode".getBytes(StandardCharsets.UTF_8),
                        "raisethysword".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                5,
                StrUtils.fullLengthDistance(
                        StrUtils::simpleLevenshteinDistance,
                        "hello".getBytes(StandardCharsets.UTF_8),
                        "".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                0,
                StrUtils.fullLengthDistance(
                        StrUtils::simpleLevenshteinDistance,
                        "same".getBytes(StandardCharsets.UTF_8),
                        "same".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                0,
                StrUtils.fullLengthDistance(
                        StrUtils::simpleLevenshteinDistance,
                        "".getBytes(StandardCharsets.UTF_8),
                        "".getBytes(StandardCharsets.UTF_8)));
    }
}
