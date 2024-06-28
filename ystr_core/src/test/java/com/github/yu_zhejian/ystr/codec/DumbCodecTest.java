package com.github.yu_zhejian.ystr.codec;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.yu_zhejian.ystr.utils.RngUtils;

import org.junit.jupiter.api.Test;

import java.util.Random;

class DumbCodecTest {
    static byte[] randArr;
    static int RAND_ARR_SIZE = 1025;
    static int RAND_COORD_SIZE = 100;

    static {
        var rng = new Random();
        randArr = new byte[RAND_ARR_SIZE];
        rng.nextBytes(randArr);
    }

    @Test
    void testDecode0() {
        var decoder = new DumbCodec();
        assertArrayEquals(randArr, decoder.decode(randArr, 0, randArr.length));
        for (var coordinate :
                RngUtils.generateRandomCoordinates(RAND_COORD_SIZE, 0, randArr.length)) {
            var arrLen = coordinate.secondInt() - coordinate.firstInt();
            var trueArray = new byte[arrLen];
            System.arraycopy(randArr, coordinate.firstInt(), trueArray, 0, arrLen);
            assertArrayEquals(trueArray, decoder.decode(randArr, coordinate.firstInt(), arrLen));
        }
    }

    @Test
    void testDecode1() {
        var decoder = new DumbCodec();
        var buffer = new byte[RAND_ARR_SIZE];

        decoder.decode(randArr, buffer, 0, 0, randArr.length);
        assertArrayEquals(randArr, buffer);

        for (var coordinate :
                RngUtils.generateRandomCoordinates(RAND_COORD_SIZE, 0, randArr.length)) {
            var arrLen = coordinate.secondInt() - coordinate.firstInt();
            var trueArray = new byte[arrLen];
            System.arraycopy(randArr, coordinate.firstInt(), trueArray, 0, arrLen);

            buffer = new byte[arrLen];
            var decodedLen = decoder.decode(randArr, buffer, coordinate.firstInt(), 0, arrLen);
            assertArrayEquals(trueArray, buffer);
            assertEquals(decodedLen, arrLen);
        }
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> decoder.decode(randArr, new byte[0], 0, 0, randArr.length));
        assertDoesNotThrow(() -> decoder.decode(randArr, new byte[0], 0, 0, 0));
    }
}
