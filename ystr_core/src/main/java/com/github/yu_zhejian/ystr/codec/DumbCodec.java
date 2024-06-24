package com.github.yu_zhejian.ystr.codec;

import com.github.yu_zhejian.ystr.StrUtils;

import org.jetbrains.annotations.NotNull;

/**
 * The codec that does nothing. Bith encoder and decoder will use {@link System#arraycopy(Object,
 * int, Object, int, int)} to copy desired number of bytes from {@code src} to {@code dst}.
 */
public class DumbCodec implements CodecInterface {

    /**
     * Default constructor.
     */
    public DumbCodec(){
        // Does nothing!
    }

    @Override
    public byte[] encode(byte[] src, int srcStart, int numBytesToRead) {
        final var retb = new byte[numBytesToRead];
        System.arraycopy(src, srcStart, retb, 0, numBytesToRead);
        return retb;
    }

    @Override
    public byte[] decode(byte @NotNull [] src, int srcStart, int numBytesToRead) {
        StrUtils.ensureStartLengthValid(srcStart, numBytesToRead, src.length);
        return encode(src, srcStart, numBytesToRead);
    }

    @Override
    public int encode(
            byte @NotNull [] src, byte[] dst, int srcStart, int dstStart, int numBytesToRead) {
        StrUtils.ensureStartLengthValid(srcStart, numBytesToRead, src.length);
        System.arraycopy(src, srcStart, dst, dstStart, numBytesToRead);
        return numBytesToRead;
    }

    @Override
    public int decode(
            byte @NotNull [] src, byte[] dst, int srcStart, int dstStart, int numBytesToRead) {
        StrUtils.ensureStartLengthValid(srcStart, numBytesToRead, src.length);
        return encode(src, dst, srcStart, dstStart, numBytesToRead);
    }
}
