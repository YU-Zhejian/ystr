package com.github.yu_zhejian.ystr;

import org.jetbrains.annotations.NotNull;

import com.github.yu_zhejian.ystr.utils.StrUtils;

/** LibC-like String manipulation. */
public final class StrLibc {

    private StrLibc() {}

    /**
     * Similar function as is implemented in C standard libraries.
     *
     * <p>Note, the string is compared unsigned!
     *
     * <p>Implemented with the help of TONGYI Lingma.
     *
     * @return As described.
     * @param array1 As described.
     * @param array2 As described.
     * @see <a href="https://en.cppreference.com/w/c/string/byte/strcmp">cppreference</a>
     * @see <a href="https://www.man7.org/linux/man-pages/man3/strcmp.3.html">Linux Manual Pages</a>
     * @see <a
     *     href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strcmp.html">POSIX</a>
     */
    public static int strcmp(final byte @NotNull [] array1, final byte @NotNull [] array2) {
        final int minLength = Math.min(array1.length, array2.length);
        final int strCmpMinLen = strncmp(array1, array2, minLength);
        return strCmpMinLen == 0 ? Integer.compare(array1.length, array2.length) : strCmpMinLen;
    }

    /**
     * Similar function as is implemented in C standard libraries.
     *
     * <p>Note, the string is compared unsigned!
     *
     * @return As described.
     * @param array1 As described.
     * @param array2 As described.
     * @param n As described.
     * @see <a href="https://en.cppreference.com/w/c/string/byte/strncmp">cppreference</a>
     * @see <a href="https://www.man7.org/linux/man-pages/man3/strncmp.3.html">Linux Manual
     *     Pages</a>
     * @see <a
     *     href="https://pubs.opengroup.org/onlinepubs/9699919799/functions/strncmp.html">POSIX</a>
     * @throws IndexOutOfBoundsException If {@code n} exceeds array boundaries.
     */
    public static int strncmp(
            final byte @NotNull [] array1, final byte @NotNull [] array2, final int n) {
        return strncmp(array1, array2, 0, 0, n);
    }

    /**
     * {@link #strncmp(byte[], byte[], int)} allowing arbitrary starts.
     *
     * <p>Note, the string is compared unsigned!
     *
     * @return As described.
     * @param array1 As described.
     * @param array2 As described.
     * @param start1 As described.
     * @param start2 As described.
     * @param n As described.
     * @throws IndexOutOfBoundsException If {@code n} exceeds array boundaries.
     */
    public static int strncmp(
            final byte @NotNull [] array1,
            final byte @NotNull [] array2,
            final int start1,
            final int start2,
            final int n) {
        byte b1;
        byte b2;
        for (int i = 0; i < n; i++) {
            b1 = array1[start1 + i];
            b2 = array2[start2 + i];
            if (b1 != b2) {
                return Integer.compare(b1 & StrUtils.BYTE_TO_UNSIGNED_MASK, b2 & StrUtils.BYTE_TO_UNSIGNED_MASK);
            }
        }
        return 0;
    }
}
