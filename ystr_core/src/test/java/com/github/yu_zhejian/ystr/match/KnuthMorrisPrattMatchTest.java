package com.github.yu_zhejian.ystr.match;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class KnuthMorrisPrattMatchTest {

    /**
     * Test cases from <a
     * href="https://www.geeksforgeeks.org/kmp-algorithm-for-pattern-searching/?ref=lbp">here</a>.
     */
    @Test
    void lps() {
        assertArrayEquals(
                new int[] {0, 1, 2, 3},
                KnuthMorrisPrattMatch.lps("AAAA".getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(
                new int[] {0, 0, 0, 0, 0},
                KnuthMorrisPrattMatch.lps("ABCDE".getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(
                new int[] {0, 1, 0, 1, 2, 0, 1, 2, 3, 4, 5},
                KnuthMorrisPrattMatch.lps("AABAACAABAA".getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(
                new int[] {0, 1, 2, 0, 1, 2, 3, 3, 3, 4},
                KnuthMorrisPrattMatch.lps("AAACAAAAAC".getBytes(StandardCharsets.UTF_8)));
        assertArrayEquals(
                new int[] {0, 1, 2, 0, 1, 2, 3},
                KnuthMorrisPrattMatch.lps("AAABAAA".getBytes(StandardCharsets.UTF_8)));
    }
}
