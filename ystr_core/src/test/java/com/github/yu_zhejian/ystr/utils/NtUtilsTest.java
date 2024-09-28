package com.github.yu_zhejian.ystr.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class NtUtilsTest {

    @Test
    void mapBasedTranslateInPlace() {
        final var translTable = NtUtils.makeTrans(new byte[] {'0', '1'}, new byte[] {'2', '3'});
        final var test1 = new byte[] {'0', '1', '2', 0, -124};
        NtUtils.mapBasedTranslateInPlace(test1, 0, test1.length, translTable);
        assertArrayEquals(new byte[] {'2', '3', '2', 0, -124}, test1);
        final var test2 = "AAAA0101AAAA0101AAAA".getBytes(StandardCharsets.US_ASCII);
        NtUtils.mapBasedTranslateInPlace(test2, 2, 6, translTable);
        assertEquals("AAAA2301AAAA0101AAAA", new String(test2, StandardCharsets.US_ASCII));
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
        // TODO
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
