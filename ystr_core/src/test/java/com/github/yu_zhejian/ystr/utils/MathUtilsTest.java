package com.github.yu_zhejian.ystr.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MathUtilsTest {

    @Test
    void pow() {
        assertEquals(2, MathUtils.pow(2, 1));
        assertEquals(1, MathUtils.pow(1, 1));
        assertEquals(4, MathUtils.pow(2, 2));
        assertEquals(8, MathUtils.pow(2, 3));
        assertEquals(9, MathUtils.pow(3, 2));
        assertEquals(1, MathUtils.pow(3, 0));
        assertEquals(0, MathUtils.pow(0, 3));
        assertEquals(1, MathUtils.pow(0, 0));

        assertEquals(2L, MathUtils.pow(2L, 1));
        assertEquals(1L, MathUtils.pow(1L, 1));
        assertEquals(4L, MathUtils.pow(2L, 2));
        assertEquals(8L, MathUtils.pow(2L, 3));
        assertEquals(9L, MathUtils.pow(3L, 2));
        assertEquals(1L, MathUtils.pow(3L, 0));
        assertEquals(0L, MathUtils.pow(0L, 3));
        assertEquals(1L, MathUtils.pow(0L, 0));
    }
}
