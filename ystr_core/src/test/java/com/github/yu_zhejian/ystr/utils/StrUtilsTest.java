package com.github.yu_zhejian.ystr.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StrUtilsTest {
    @Test
    @SuppressWarnings("java:S3415")
    void testSize() {
        assertEquals(Long.SIZE, StrUtils.LONG_SIZE);
        var expectedAlphabetSize = Byte.MAX_VALUE - Byte.MIN_VALUE + 1;
        assertEquals(expectedAlphabetSize, StrUtils.ALPHABET_SIZE);
    }
}
