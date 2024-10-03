package com.github.yu_zhejian.ystr.alphabet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

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
        assertIterableEquals(new ByteArrayList(new byte[] {97, 98, 99}), imba);
        assertEquals(97, imba.at(0));
        assertThrows(IndexOutOfBoundsException.class, () -> imba.at(4));
        assertThrows(IndexOutOfBoundsException.class, () -> imba.at(-1));

        var imba2 = new Alphabet("abc1".getBytes());
        var imba3 = new Alphabet("abc1".getBytes());
        assertNotEquals(imba2.hashCode(), imba.hashCode());
        assertEquals(imba2.hashCode(), imba3.hashCode());
        assertEquals(imba2, imba3);

        assertThrows(IllegalArgumentException.class, () -> new Alphabet(new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> new Alphabet(new byte[] {1, 1}));
    }
}
