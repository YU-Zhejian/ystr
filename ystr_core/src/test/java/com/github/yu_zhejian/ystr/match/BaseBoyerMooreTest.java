package com.github.yu_zhejian.ystr.match;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class BaseBoyerMooreTest {

    @Test
    void bmBadCharacterRule() {}

    @Test
    void bmSuffixes() {
        // Test case from <https://zhuanlan.zhihu.com/p/156757171>
        var needle = "today sunday";
        assertArrayEquals(
                new int[] {0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 12},
                BaseBoyerMoore.bmSuffixes(needle.getBytes(StandardCharsets.US_ASCII)));

        needle = "cabdabdab";
        assertArrayEquals(
                new int[] {0, 0, 2, 0, 0, 5, 0, 0, 9},
                BaseBoyerMoore.bmSuffixes(needle.getBytes(StandardCharsets.US_ASCII)));

        needle = "CAGAGAG";
        assertArrayEquals(
                new int[] {1, 0, 0, 2, 0, 4, 0, 8},
                BaseBoyerMoore.bmSuffixes(needle.getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void bmGoodSuffixRule() {
        var needle = "CAGAGAG";
        assertArrayEquals(
                new int[] {7, 7, 7, 2, 7, 4, 7, 1},
                BaseBoyerMoore.bmGoodSuffixRule(needle.getBytes(StandardCharsets.US_ASCII)));
    }
}
