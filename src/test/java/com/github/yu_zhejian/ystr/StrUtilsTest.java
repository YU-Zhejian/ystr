package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StrUtilsTest {

    @Test
    void pow() {
        assertEquals(2, StrUtils.pow(2, 1));
        assertEquals(1, StrUtils.pow(1, 1));
        assertEquals(4, StrUtils.pow(2, 2));
        assertEquals(8, StrUtils.pow(2, 3));
        assertEquals(9, StrUtils.pow(3, 2));
        assertEquals(1, StrUtils.pow(3, 0));
    }
}
