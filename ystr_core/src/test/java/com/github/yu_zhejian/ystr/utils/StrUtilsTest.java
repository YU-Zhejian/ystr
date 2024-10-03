package com.github.yu_zhejian.ystr.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.yu_zhejian.ystr.alphabet.AlphabetConstants;
import com.github.yu_zhejian.ystr.alphabet.RandomKmerGenerator;

import org.junit.jupiter.api.Test;

import java.util.List;

class StrUtilsTest {
    @Test
    @SuppressWarnings("java:S3415")
    void testSize() {
        assertEquals(Long.SIZE, StrUtils.LONG_SIZE);
        var expectedAlphabetSize = Byte.MAX_VALUE - Byte.MIN_VALUE + 1;
        assertEquals(expectedAlphabetSize, StrUtils.ALPHABET_SIZE);
    }

    @Test
    void testByteToUnsigned() {
        var bytes = AlphabetConstants.FULL_ALPHABET.getValue();
        var unsignedInts = StrUtils.byteToUnsigned(bytes);
        assertArrayEquals(
                new int[] {
                    128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143,
                    144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159,
                    160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175,
                    176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191,
                    192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207,
                    208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223,
                    224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239,
                    240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255,
                    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                    22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41,
                    42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61,
                    62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81,
                    82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100,
                    101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116,
                    117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127
                },
                unsignedInts);
    }

    @Test
    void testKeyIndexedCountingSimple() {
        for (final byte[] bytes : List.of(
                new byte[] {'A', 'C', 'T', 'G'},
                new byte[] {-1, 0, 9, 2},
                new byte[] {},
                new byte[] {1, 1, 1})) {
            StrUtils.countingSort(bytes);
            assertDoesNotThrow(() -> StrUtils.requiresSorted(bytes));
        }
    }

    @Test
    void testKeyIndexedCountingShortKmers() {
        for (int i = 1; i < 10; i++) {
            var rkg = new RandomKmerGenerator(AlphabetConstants.FULL_ALPHABET, i);
            for (int j = 0; j < 1000; j++) {
                var bytes = rkg.next();
                StrUtils.countingSort(bytes);
                assertDoesNotThrow(() -> StrUtils.requiresSorted(bytes));
            }
        }
    }

    /** Generated with the help of TONGYI Lingma. */
    @Test
    void ensureStartEndValid() {
        StrUtils.ensureStartEndValid(0, 10);
        StrUtils.ensureStartEndValid(5, 10);
        StrUtils.ensureStartEndValid(5, 5);
        assertThrows(IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(10, 5));
        assertThrows(IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(0, -1));
        assertThrows(IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(11, 10));

        StrUtils.ensureStartEndValid(0, 10, 20);
        StrUtils.ensureStartEndValid(5, 10, 20);
        StrUtils.ensureStartEndValid(5, 5, 20);
        assertThrows(IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(10, 5, 20));
        assertThrows(
                IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(-1, 10, 20));
        assertThrows(IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(0, -1, 20));
        assertThrows(
                IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(11, 10, 20));

        assertThrows(IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(5, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(5, 10, -2));
        assertThrows(IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(-5, 10, 5));
        assertThrows(
                IllegalArgumentException.class, () -> StrUtils.ensureStartEndValid(5, -10, -2));
        StrUtils.ensureStartEndValid(5, 5, 5);
        StrUtils.ensureStartEndValid(0, 0, 0);
    }

    /** Generated with the help of TONGYI Lingma. */
    @Test
    void ensureStartLengthValid() {
        int strLen = 100;
        assertDoesNotThrow(() -> StrUtils.ensureStartLengthValid(0, 10, strLen));
        assertDoesNotThrow(() -> StrUtils.ensureStartLengthValid(5, 10, strLen));
        assertDoesNotThrow(() -> StrUtils.ensureStartLengthValid(90, 10, strLen));
        StrUtils.ensureStartLengthValid(0, 0, strLen);
        StrUtils.ensureStartLengthValid(strLen, 0, strLen);
        StrUtils.ensureStartLengthValid(strLen - 1, 1, strLen);
        assertThrows(
                IllegalArgumentException.class,
                () -> StrUtils.ensureStartLengthValid(0, -10, strLen));
        assertThrows(
                IllegalArgumentException.class,
                () -> StrUtils.ensureStartLengthValid(5, -5, strLen));
        assertThrows(
                IllegalArgumentException.class,
                () -> StrUtils.ensureStartLengthValid(100, 10, strLen));
        assertThrows(
                IllegalArgumentException.class,
                () -> StrUtils.ensureStartLengthValid(110, 10, strLen));
        assertThrows(
                IllegalArgumentException.class,
                () -> StrUtils.ensureStartLengthValid(90, 20, strLen));
        assertThrows(
                IllegalArgumentException.class,
                () -> StrUtils.ensureStartLengthValid(0, 101, strLen));
        assertThrows(
                IllegalArgumentException.class,
                () -> StrUtils.ensureStartLengthValid(-10, 10, strLen));
        assertThrows(
                IllegalArgumentException.class,
                () -> StrUtils.ensureStartLengthValid(-1, 10, strLen));
        assertThrows(
                IllegalArgumentException.class,
                () -> StrUtils.ensureStartLengthValid(strLen, 10, strLen));
    }

    /** Generated with the help of TONGYI Lingma. */
    @Test
    void requiresSorted() {
        var emptyArray = new byte[0];
        var sortedArray = new byte[] {'A', 'C', 'G', 'T'};
        var unsortedArray = new byte[] {'A', 'T', 'C', 'G'};
        var duplicateElementsArray = new byte[] {0, 0, 'A', 'A', 'C', 'C', 'G', 'G', 'T', 'T'};
        assertDoesNotThrow(() -> StrUtils.requiresSorted(emptyArray));
        assertDoesNotThrow(() -> StrUtils.requiresSorted(sortedArray));
        assertThrows(IllegalArgumentException.class, () -> StrUtils.requiresSorted(unsortedArray));
        assertDoesNotThrow(() -> StrUtils.requiresSorted(new byte[] {'a'}));
        assertDoesNotThrow(() -> StrUtils.requiresSorted(duplicateElementsArray));
    }
}
