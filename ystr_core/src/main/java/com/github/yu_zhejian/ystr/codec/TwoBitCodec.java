package com.github.yu_zhejian.ystr.codec;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Encode data into UCSC 2bit format.
 *
 * <p><b>Note</b>
 *
 * <ol>
 *   <li>Note, this implementation deals only with sequences. It would not generate 2bit headers or
 *       masks. This implementation will only deal with {@code AGCTUagctu} nucleotides.
 *   <li>Note, all unknown bases will be converted to {@code T}.
 *   <li>Note, {@code T} will be padded if the src is not long enough. Always remember how long the
 *       src is before encoding!
 * </ol>
 *
 * <p><b>References</b>
 *
 * <ol>
 *   <li><a href="https://genome.ucsc.edu/FAQ/FAQformat#format7">Offial description.</a>
 *   <li><a href="http://jcomeau.freeshell.org/www/genome/2bitformat.html">Some external
 *       introduction to the 2bit format.</a>
 *   <li><a href="https://github.com/weng-lab/TwoBit">An alternate implementation of 2bit
 *       reader/writer.</a>
 *   <li><a
 *       href="http://genome-source.soe.ucsc.edu/gitlist/kent.git/raw/master/src/inc/twoBit.h">Official
 *       C header file.</a>
 * </ol>
 */
public final class TwoBitCodec implements CodecInterface {
    /**
     * the DNA packed to two bits per base, represented as so: T - 00, C - 01, A - 10, G - 11. The
     * first base is in the most significant 2-bit byte; the last base is in the least significant 2
     * bits. For example, the sequence TCAG is represented as 00011011.
     */
    private static final byte[] BYTE_TO_BASE = {'T', 'C', 'A', 'G'};

    /** Precomputed decode table, which should be mapping between a byte to 4 bases. */
    private static final byte[][] BYTE_TO_BASE_PRE_COMPUTED;

    /** Precomputed encode table, which should be mapping between a base to a byte. */
    private static final int[] BASE_TO_BYTE_PRE_COMPUTED;

    /** Default constructor. */
    public TwoBitCodec() {
        // Does nothing!
    }

    /**
     * Decode one byte to four bases.
     *
     * @param encodedByte As described.
     * @return As described.
     */
    @Contract(value = "_ -> new", pure = true)
    public static byte @NotNull [] decodeToFourBases(int encodedByte) {
        return new byte[] {
            BYTE_TO_BASE[encodedByte >> 6 & 0b11],
            BYTE_TO_BASE[encodedByte >> 4 & 0b11],
            BYTE_TO_BASE[encodedByte >> 2 & 0b11],
            BYTE_TO_BASE[encodedByte & 0b11]
        };
    }

    static {
        BYTE_TO_BASE_PRE_COMPUTED = new byte[StrUtils.ALPHABET_SIZE][4];
        BASE_TO_BYTE_PRE_COMPUTED = new int[StrUtils.ALPHABET_SIZE];
        for (int i = 0; i <= 0b11_11_11_11; i++) {
            BYTE_TO_BASE_PRE_COMPUTED[i] = decodeToFourBases(i);
            BASE_TO_BYTE_PRE_COMPUTED[i] = switch (i) {
                case 'A', 'a' -> 0b10;
                case 'C', 'c' -> 0b01;
                case 'G', 'g' -> 0b11;
                default -> 0b00;
            };
        }
    }

    @Override
    public byte @NotNull [] encode(byte @NotNull [] src, int srcStart, int numBytesToRead) {
        StrUtils.ensureStartLengthValid(srcStart, numBytesToRead, src.length);
        final var numFullBytes = (numBytesToRead >> 2);
        // Number of bytes left. Should be [0, 4)
        final var numBytesRemaining = numBytesToRead - (numFullBytes << 2);
        final var outLen = numFullBytes + (numBytesRemaining == 0 ? 0 : 1);
        final var dst = new byte[outLen];
        encodeImpl(src, dst, srcStart, 0, numFullBytes, numBytesRemaining);
        return dst;
    }

    @Override
    public byte @NotNull [] decode(byte @NotNull [] src, int srcStart, int numBytesToRead) {
        StrUtils.ensureStartLengthValid(srcStart, numBytesToRead, src.length);
        final var outLen = numBytesToRead << 2;
        final var dst = new byte[outLen];
        decodeImpl(src, dst, srcStart, 0, numBytesToRead);
        return dst;
    }

    @Override
    public int encode(
            byte @NotNull [] src,
            byte @NotNull [] dst,
            int srcStart,
            int dstStart,
            int numBytesToRead) {
        StrUtils.ensureStartLengthValid(srcStart, numBytesToRead, src.length);
        final var numFullBytes = (numBytesToRead >> 2);
        // Number of bytes left. Should be [0, 4)
        final var numBytesRemaining = numBytesToRead - (numFullBytes << 2);
        return encodeImpl(src, dst, srcStart, dstStart, numFullBytes, numBytesRemaining);
    }

    @Override
    public int decode(
            byte @NotNull [] src,
            byte @NotNull [] dst,
            int srcStart,
            int dstStart,
            int numBytesToRead) {
        StrUtils.ensureStartLengthValid(srcStart, numBytesToRead, src.length);
        return decodeImpl(src, dst, srcStart, dstStart, numBytesToRead);
    }

    /**
     * Unchhecked {@link #encode(byte[], byte[], int, int, int)}
     *
     * @param src As described.
     * @param dst As described.
     * @param srcStart As described.
     * @param dstStart As described.
     * @param numFullBytes As described.
     * @param numBytesRemaining As described.
     * @return As described.
     */
    private int encodeImpl(
            final byte @NotNull [] src,
            final byte[] dst,
            final int srcStart,
            final int dstStart,
            final int numFullBytes,
            final int numBytesRemaining) {
        var retl = numFullBytes;
        var srcPos = srcStart;
        var dstPos = dstStart;
        for (var i = 0; i < numFullBytes; i++) {
            dst[dstPos++] = (byte) ((BASE_TO_BYTE_PRE_COMPUTED[src[srcPos++] & StrUtils.BYTE_TO_UNSIGNED_MASK] << 6)
                    | (BASE_TO_BYTE_PRE_COMPUTED[src[srcPos++] & StrUtils.BYTE_TO_UNSIGNED_MASK] << 4)
                    | (BASE_TO_BYTE_PRE_COMPUTED[src[srcPos++] & StrUtils.BYTE_TO_UNSIGNED_MASK] << 2)
                    | BASE_TO_BYTE_PRE_COMPUTED[src[srcPos++] & StrUtils.BYTE_TO_UNSIGNED_MASK]);
        }
        if (numBytesRemaining != 0) {
            retl += 1;
            final var numBlanksRemaining = 4 - numBytesRemaining;
            byte lastb = 0;
            for (var i = 0; i < numBytesRemaining; i++) {
                lastb = (byte) (lastb << 2 | BASE_TO_BYTE_PRE_COMPUTED[src[srcPos++] & StrUtils.BYTE_TO_UNSIGNED_MASK]);
            }
            for (var i = 0; i < numBlanksRemaining; i++) {
                lastb = (byte) (lastb << 2);
            }
            dst[dstPos] = lastb;
        }
        return retl;
    }
    /**
     * Unchhecked {@link #decode(byte[], byte[], int, int, int)}
     *
     * @param src As described.
     * @param dst As described.
     * @param srcStart As described.
     * @param dstStart As described.
     * @param numBytesToRead As described.
     * @return As described.
     */
    private int decodeImpl(
            final byte @NotNull [] src,
            final byte[] dst,
            final int srcStart,
            final int dstStart,
            final int numBytesToRead) {
        var srcPos = srcStart;
        var dstPos = dstStart;
        final var retl = numBytesToRead << 2;
        byte[] decoded;
        for (var i = 0; i < numBytesToRead; i++) {
            decoded = BYTE_TO_BASE_PRE_COMPUTED[src[srcPos++] & StrUtils.BYTE_TO_UNSIGNED_MASK];
            // This is the fastest way of setting all bits.
            System.arraycopy(decoded, 0, dst, dstPos, 4);
            dstPos += 4;
        }
        return retl;
    }
}
