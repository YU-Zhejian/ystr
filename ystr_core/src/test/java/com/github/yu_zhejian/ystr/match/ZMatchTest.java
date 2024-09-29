package com.github.yu_zhejian.ystr.match;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class ZMatchTest {

    @Test
    void generateZArray() {
        // From https://zhuanlan.zhihu.com/p/403256847
        assertArrayEquals(
                new int[] {0, 1, 0, 0, 6, 1, 0, 0, 2, 2, 3, 1, 0},
                ZMatch.generateZArray("aabcaabcaaaab".getBytes(StandardCharsets.US_ASCII)));
    }
}
