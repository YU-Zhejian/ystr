package com.github.yu_zhejian.ystr.codec;

import com.github.yu_zhejian.ystr.StrUtils;

public interface CodecInterface {

    /**
     * Encode one array. The destination array will be allocated inside the function.
     *
     * @param src As described.
     * @param srcStart As described.
     * @param numBytesToRead As described.
     * @throws IllegalArgumentException See {@link StrUtils#ensureStartLengthValid(int, int, int)}.
     */
    byte[] encode(final byte[] src, final int srcStart, final int numBytesToRead);

    /**
     * Decode one array
     *
     * @param src As described.
     * @param srcStart As described.
     * @param numBytesToRead As described.
     * @return As described.
     * @throws IllegalArgumentException As described.
     * @see #encode(byte[], int, int)
     */
    byte[] decode(final byte[] src, final int srcStart, final int numBytesToRead);

    /**
     * Encode from one array to another.
     *
     * @param src As described.
     * @param dst As described. This array is assumed to be long enough, and no boundary check will
     *     be performed by default.
     * @param srcStart As described.
     * @param dstStart As described.
     * @param numBytesToRead As described.
     * @return Number of bytes written.
     * @throws IndexOutOfBoundsException If the {@code dest} array is not large enough.
     * @throws IllegalArgumentException See {@link StrUtils#ensureStartLengthValid(int, int, int)}.
     */
    int encode(
            final byte[] src,
            final byte[] dst,
            final int srcStart,
            final int dstStart,
            final int numBytesToRead);

    /**
     * Decode from one array to another.
     *
     * @param src As described.
     * @param dst As described.
     * @param srcStart As described.
     * @param dstStart As described.
     * @param numBytesToRead As described.
     * @return Number of bytes written.
     * @throws IndexOutOfBoundsException As described.
     * @throws IllegalArgumentException As described.
     * @see #encode(byte[], byte[], int, int, int)
     */
    int decode(
            final byte[] src,
            final byte[] dst,
            final int srcStart,
            final int dstStart,
            final int numBytesToRead);
}
