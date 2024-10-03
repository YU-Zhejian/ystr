package com.github.yu_zhejian.ystr.alphabet;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RandomKmerGeneratorTest {
    @Test
    void test() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RandomKmerGenerator(AlphabetConstants.DNA5_ALPHABET_BC, -1));
        assertThrows(
                IllegalArgumentException.class,
                () -> new RandomKmerGenerator(AlphabetConstants.DNA5_ALPHABET_BC, -0));
    }
}
