package com.github.yu_zhejian.ystr.utils;

import com.github.yu_zhejian.ystr.StrLibc;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/** Various utility functions concerning strings to support other classes. */
public final class StrUtils {
    /** Size of {@link Byte}. */
    public static final int ALPHABET_SIZE = 256;
    /** Size of {@link Long}. */
    public static final int LONG_SIZE = 64;

    public static final int BYTE_TO_UNSIGNED_MASK = 0xFF;

    /** Defunct constructor */
    private StrUtils() {}

    /**
     * Convert a signed byte to unsigned int.
     *
     * @param b As described.
     * @return As described.
     */
    public static int byteToUnsigned(final byte b) {
        return b & BYTE_TO_UNSIGNED_MASK;
    }

    /**
     * Convert an array of signed bytes to unsigned ints.
     *
     * @param b As described.
     * @return As described.
     */
    @Contract(pure = true)
    public static int @NotNull [] byteToUnsigned(final byte @NotNull [] b) {
        var reti = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            reti[i] = byteToUnsigned(b[i]);
        }
        return reti;
    }

    /**
     * Integer version of {@link #byteToUnsigned(byte)}.
     *
     * @param b As described.
     * @return As described.
     */
    public static int byteToUnsigned(final int b) {
        return b & BYTE_TO_UNSIGNED_MASK;
    }

    /**
     * Ensure start and end are valid for some open-close interval.
     *
     * @param start As described.
     * @param end As described.
     * @throws IllegalArgumentException otherwise.
     */
    public static void ensureStartEndValid(final int start, final int end) {
        if (start > end) {
            throw new IllegalArgumentException(
                    "start must be less than end. Actual: %d vs %d".formatted(start, end));
        }
        if (start < 0) {
            throw new IllegalArgumentException(
                    "start must be greater than or equal to zero. Actual: %d".formatted(start));
        }
    }

    /**
     * Ensure start and end are valid for some open-close interval, {@link Long} variant.
     *
     * @param start As described.
     * @param end As described.
     * @throws IllegalArgumentException otherwise.
     * @see #ensureStartEndValid(int, int)
     */
    public static void ensureStartEndValid(final long start, final long end) {
        if (start > end) {
            throw new IllegalArgumentException(
                    "start must be less than end. Actual: %d vs %d".formatted(start, end));
        }
        if (start < 0) {
            throw new IllegalArgumentException(
                    "start must be greater than or equal to zero. Actual: %d".formatted(start));
        }
    }

    /**
     * Ensure start and end is valid for some open-close interval within a string.
     *
     * @param start As described.
     * @param end As described.
     * @param strLen As described.
     * @see #ensureStartEndValid(int, int)
     * @throws IllegalArgumentException otherwise.
     */
    public static void ensureStartEndValid(final int start, final int end, final int strLen) {
        ensureStartEndValid(start, end);
        if (end > strLen) {
            throw new IllegalArgumentException(
                    "end must be less than strLen. Actual: %d vs %d".formatted(end, strLen));
        }
    }

    /**
     * Ensure start and length are valid for some open-close interval within a string.
     *
     * @param start As described.
     * @param numBytesToRead As described.
     * @param strLen As described.
     * @throws IllegalArgumentException otherwise.
     */
    public static void ensureStartLengthValid(
            final int start, final int numBytesToRead, final int strLen) {
        ensureStartEndValid(start, start + numBytesToRead, strLen);
    }

    /**
     * Assert whether a list of string is sorted.
     *
     * @param strings As described.
     */
    public static void requiresSorted(final @NotNull List<byte[]> strings) {
        if (strings.isEmpty()) {
            return;
        }
        for (int i = 0; i < strings.size() - 1; i++) {
            if (StrLibc.strcmp(strings.get(i), strings.get(i + 1)) > 0) {
                throw new IllegalArgumentException(
                        "The array is not sorted between %d and %d, whish is %s and %s".formatted(
                            i, i + 1,
                            Arrays.toString(strings.get(i)),
                            Arrays.toString(strings.get(i + 1))
                        ));
            }
        }
    }

    /**
     * Assert whether a string is sorted.
     *
     * @param string As described.
     */
    public static void requiresSorted(final byte @NotNull [] string) {
        if (string.length == 0) {
            return;
        }
        for (int i = 0; i < string.length - 1; i++) {
            if (StrLibc.strcmp(string[i], string[i + 1]) > 0) {
                throw new IllegalArgumentException(
                        "The array is not sorted between %d and %d!".formatted(i, i + 1));
            }
        }
    }

    /**
     * In-place sorting of small arrays. Modified from Robert Sedgewick et al. This sorting
     * algorithm is stable.
     *
     * @param array As described.
     */
    public static void countingSort(final byte @NotNull [] array) {
        var aux = new byte[array.length];
        var count = new int[ALPHABET_SIZE + 1];
        for (final byte j : array) {
            count[(j & BYTE_TO_UNSIGNED_MASK) + 1]++;
        }
        for (int r = 0; r < ALPHABET_SIZE; r++) {
            count[r + 1] += count[r];
        }
        for (final byte j : array) {
            aux[count[(j & BYTE_TO_UNSIGNED_MASK)]++] = (j);
        }
        System.arraycopy(aux, 0, array, 0, array.length);
    }
}
