package com.github.yu_zhejian.ystr.alphabet;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class AlphabetTest {
    @Test
    void test() {
        var imba = new Alphabet("abc".getBytes());
        assertEquals("Alphabet{value=[97, 98, 99]}", imba.toString());
        assertEquals(3, imba.length());
        assertEquals("abc", imba.encode(StandardCharsets.US_ASCII));
        assertEquals(imba, new Alphabet(new byte[] {97, 98, 99}));
        assertFalse(imba.isEmpty());
        assertEquals(97, imba.at(0));
        assertThrows(IndexOutOfBoundsException.class, () -> imba.at(4));
        assertThrows(IndexOutOfBoundsException.class, () -> imba.at(-1));
    }
}
