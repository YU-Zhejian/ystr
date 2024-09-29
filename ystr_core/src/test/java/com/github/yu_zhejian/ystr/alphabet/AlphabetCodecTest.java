package com.github.yu_zhejian.ystr.alphabet;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AlphabetCodecTest {

    @Test
    void test() {
        var abCodec = new AlphabetCodec(AlphabetConstants.DNA5_ALPHABET, 4);
        assertEquals(0, abCodec.encode((byte) 'A'));
        assertEquals(1, abCodec.encode((byte) 'C'));
        assertEquals(3, abCodec.encode((byte) 'T'));
        assertEquals(4, abCodec.encode((byte) 'N'));
        assertEquals(4, abCodec.encode((byte) 'X'));
        assertEquals(4, abCodec.encode((byte) -1));
        assertEquals((byte) 'C', abCodec.decode(1));
        assertEquals((byte) 'N', abCodec.decode(4));
    }
}
