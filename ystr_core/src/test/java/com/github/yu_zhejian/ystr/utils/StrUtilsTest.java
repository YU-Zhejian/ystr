package com.github.yu_zhejian.ystr.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class StrUtilsTest {
    @Test
    @SuppressWarnings("java:S3415")
    void testSize() {
        assertEquals(Long.SIZE, StrUtils.LONG_SIZE);
        var expectedAlphabetSize = Byte.MAX_VALUE - Byte.MIN_VALUE + 1;
        assertEquals(expectedAlphabetSize, StrUtils.ALPHABET_SIZE);
    }
    @Test
    void test(){
        var bytes = new byte[StrUtils.ALPHABET_SIZE];
        int i = 0;
        for (byte j = Byte.MIN_VALUE; j <Byte.MAX_VALUE; j++){
            bytes[i] = j;
            j++;
        }
        var ints = StrUtils.byteToUnsigned(bytes);
        System.out.println(Arrays.toString(ints));
    }
}
