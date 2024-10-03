package com.github.yu_zhejian.ystr.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unimi.dsi.fastutil.bytes.Byte2ByteOpenHashMap;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

class NtUtilsTest {

    @Test
    void mapBasedTranslateInPlace() {
        for (final var translTable : List.of(
                NtUtils.makeTrans(new byte[] {'0', '1'}, new byte[] {'2', '3'}),
                NtUtils.makeTrans(Map.of((byte) '0', (byte) '2', (byte) '1', (byte) '3')),
                NtUtils.makeTrans(
                        new Byte2ByteOpenHashMap(new byte[] {'0', '1'}, new byte[] {'2', '3'})))) {
            final var test1 = new byte[] {'0', '1', '2', 0, -124};
            NtUtils.mapBasedTranslateInPlace(test1, 0, test1.length, translTable);
            assertArrayEquals(new byte[] {'2', '3', '2', 0, -124}, test1);
            final var test2 = "AAAA0101AAAA0101AAAA".getBytes(StandardCharsets.US_ASCII);
            NtUtils.mapBasedTranslateInPlace(test2, 2, 6, translTable);
            assertEquals("AAAA2301AAAA0101AAAA", new String(test2, StandardCharsets.US_ASCII));
        }
        assertThrows(
                IllegalArgumentException.class,
                () -> NtUtils.makeTrans(new byte[0], new byte[] {0}));
    }

    @Test
    void toUpperInPlace() {
        final var test1 = "abcdeABCDE0!@#$\0".getBytes(StandardCharsets.US_ASCII);
        NtUtils.toUpperInPlace(test1, 0, test1.length);
        assertEquals("ABCDEABCDE0!@#$\0", new String(test1, StandardCharsets.US_ASCII));
    }

    @Test
    void toLowerInPlace() {
        final var test1 = "abcdeABCDE0!@#$\0".getBytes(StandardCharsets.US_ASCII);
        NtUtils.toLowerInPlace(test1, 0, test1.length);
        assertEquals("abcdeabcde0!@#$\0", new String(test1, StandardCharsets.US_ASCII));
    }

    @Test
    void maskLowerInPlace() {
        final var test1 = "abcdeABCDE0!@#$\0".getBytes(StandardCharsets.US_ASCII);
        NtUtils.maskLowerInPlace(test1, 0, test1.length, (byte) 'X');
        assertEquals("XXXXXABCDE0!@#$\0", new String(test1, StandardCharsets.US_ASCII));
    }

    @Test
    void reverseInPlace() {
        var original = "abcdeABCDE0!@#$\0".getBytes(StandardCharsets.US_ASCII);
        var expectedReversed = "\0$#@!0EDCBAedcba".getBytes(StandardCharsets.US_ASCII);
        NtUtils.reverseInPlace(original, 0, original.length);
        assertArrayEquals(expectedReversed, original);
        var test = "abcdef".getBytes(StandardCharsets.US_ASCII);
        NtUtils.reverseInPlace(test, 0, 3);
        assertArrayEquals("cbadef".getBytes(StandardCharsets.US_ASCII), test);
        test = new byte[0];
        NtUtils.reverseInPlace(test, 0, 0);
        assertArrayEquals(new byte[0], test);
        test = "a".getBytes(StandardCharsets.US_ASCII);
        NtUtils.reverseInPlace(test, 0, 1);
        assertArrayEquals("a".getBytes(StandardCharsets.US_ASCII), test);
    }

    @Test
    void complementaryInPlace() {
        // TODO
    }

    @Test
    void reverseComplementaryInPlace() {
        // TODO
    }
}
