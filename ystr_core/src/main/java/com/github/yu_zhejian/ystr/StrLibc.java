package com.github.yu_zhejian.ystr;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * LibC-like String manipulation routines. Implemented by transforming LibC-like interface to Java
 * {@link Arrays} routines.
 *
 * <p>Note:
 *
 * <ul>
 *   <li>None of the method performs boundary check.
 * </ul>
 */
public final class StrLibc {

    /** Defunct Constructor * */
    private StrLibc() {}

    /**
     * Similar function as is implemented in C standard libraries.
     *
     * <p>Note, the string is compared unsigned!
     *
     * <p>Implemented with the help of TONGYI Lingma.
     *
     * @param array1 As described.
     * @param array2 As described.
     * @return As described.
     * @see <a href="https://en.cppreference.com/w/c/string/byte/strcmp">cppreference</a>
     * @see <a href="https://www.man7.org/linux/man-pages/man3/strcmp.3.html">Linux Manual Pages</a>
     * @see <a
     *     href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strcmp.html">POSIX</a>
     */
    public static int memcmp(final byte @NotNull [] array1, final byte @NotNull [] array2) {
        return Arrays.compareUnsigned(array1, array2);
    }

    /**
     * Compare two characters as unsigned int.
     *
     * @param char1 As described.
     * @param char2 As described.
     * @return As described.
     */
    public static int memcmp(final byte char1, final byte char2) {
        return Byte.compareUnsigned(char1, char2);
    }

    /**
     * Similar function as is implemented in C standard libraries.
     *
     * <p>Note, the string is compared unsigned!
     *
     * @param array1 As described.
     * @param array2 As described.
     * @param n As described.
     * @return As described.
     * @throws IndexOutOfBoundsException If {@code n} exceeds array boundaries.
     * @see <a href="https://en.cppreference.com/w/c/string/byte/strncmp">cppreference</a>
     * @see <a href="https://www.man7.org/linux/man-pages/man3/strncmp.3.html">Linux Manual
     *     Pages</a>
     * @see <a
     *     href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strncmp.html">POSIX</a>
     */
    public static int memcmp(
            final byte @NotNull [] array1, final byte @NotNull [] array2, final int n) {
        return memcmp(array1, array2, 0, 0, n);
    }

    /**
     * {@link #memcmp(byte[], byte[], int)} allowing arbitrary starts.
     *
     * <p>Note, the string is compared unsigned!
     *
     * @param array1 As described.
     * @param array2 As described.
     * @param start1 As described.
     * @param start2 As described.
     * @param n As described.
     * @return As described.
     * @throws IndexOutOfBoundsException If {@code n} exceeds array boundaries.
     */
    public static int memcmp(
            final byte @NotNull [] array1,
            final byte @NotNull [] array2,
            final int start1,
            final int start2,
            final int n) {
        return Arrays.compareUnsigned(array1, start1, start1 + n, array2, start2, start2 + n);
    }

    /**
     * Fill the first {@code count} bytes of an array with desired characters.
     *
     * @param dest As described.
     * @param ch As described.
     * @param n As described.
     * @throws IndexOutOfBoundsException If {@code n} exceeds array boundaries.
     */
    public static void memset(final byte @NotNull [] dest, final byte ch, final int n) {
        memset(dest, ch, 0, n);
    }

    /**
     * {@link #memset(byte[], byte, int)} for the entire string.
     *
     * @param dest As described.
     * @param ch As described.
     */
    public static void memset(final byte @NotNull [] dest, final byte ch) {
        memset(dest, ch, 0, dest.length);
    }

    /**
     * Fill the desired position of an array with arbitrary bytes.
     *
     * @param dest As described.
     * @param ch As described.
     * @param start As described.
     * @param n As described.
     * @see Arrays#fill
     * @see <a href="https://en.cppreference.com/w/c/string/byte/memset">cppreference</a>
     * @throws IndexOutOfBoundsException If {@code n} exceeds array boundaries.
     */
    public static void memset(
            final byte @NotNull [] dest, final byte ch, final int start, final int n) {
        Arrays.fill(dest, start, start + n, ch);
    }
}
