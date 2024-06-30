package com.github.yu_zhejian.ystr.codec;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Encode data into UCSC nib format.
 *
 * <p>Note, this implementation deals only with sequences. It would not generate 2bit headers or
 * masks. This implementation will only deal with {@code AGCTNagctn} nucleotides.
 *
 * <p>Note, all unknown bases will be converted to {@code N}.
 *
 * <p><b>References</b>
 *
 * <ol>
 *   <li><a href="https://genome.ucsc.edu/FAQ/FAQformat#format8">Official description</a>
 * </ol>
 */
public final class NibCodec implements CodecInterface {
    /** Numerical representation of the bases. */
    private static final byte[] BYTE_TO_BASE = {'T', 'C', 'A', 'G', 'N', 'N', 'N', 'N'};
    /** Mask version of {@link #BYTE_TO_BASE} */
    private static final byte[] BYTE_TO_BASE_MASKED = {'t', 'c', 'a', 'g', 'n', 'n', 'n', 'n'};

    /** Precomputed decode table, which should be mapping between a byte to 4 bases. */
    private static final byte[][] BYTE_TO_BASE_PRE_COMPUTED;

    /** Precomputed encode table, which should be mapping between a base to a byte. */
    private static final int[] BASE_TO_BYTE_PRE_COMPUTED;

    /** Default constructor. */
    public NibCodec() {
        // Does nothing!
    }

    @Contract(value = "_ -> new", pure = true)
    public static byte @NotNull [] decodeToTwoBases(int encodedByte) {
        final var base1 = (encodedByte >> 4) & 0b1111;
        final var base2 = encodedByte & 0b1111;
        return new byte[] {
            ((base1 & 0b1000) == 0b1000 ? BYTE_TO_BASE_MASKED : BYTE_TO_BASE)[base1 & 0b0111],
            ((base2 & 0b1000) == 0b1000 ? BYTE_TO_BASE_MASKED : BYTE_TO_BASE)[base2 & 0b0111]
        };
    }

    static {
        BYTE_TO_BASE_PRE_COMPUTED = new byte[StrUtils.ALPHABET_SIZE][2];
        BASE_TO_BYTE_PRE_COMPUTED = new int[StrUtils.ALPHABET_SIZE];
        for (int i = 0; i <= 0b11_11_11_11; i++) {
            BYTE_TO_BASE_PRE_COMPUTED[i] = decodeToTwoBases(i);
            BASE_TO_BYTE_PRE_COMPUTED[i] = switch (i) {
                case 'T' -> 0x0000;
                case 'C' -> 0x0001;
                case 'A' -> 0x0010;
                case 'G' -> 0x0011;
                    // N becomes default
                case 't' -> 0x1000;
                case 'c' -> 0x1001;
                case 'a' -> 0x1010;
                case 'g' -> 0x1011;
                case 'n' -> 0x1100;
                default -> 0x0100;
            };
        }
    }

    @Override
    public byte @NotNull [] encode(byte @NotNull [] src, int srcStart, int numBytesToRead) {
        StrUtils.ensureStartLengthValid(srcStart, numBytesToRead, src.length);
        final var numFullBytes = (numBytesToRead >> 1);
        // Number of bytes left. Should be 0 or 1
        final var numBytesRemaining = numBytesToRead - (numFullBytes << 1);
        final var outLen = numFullBytes + numBytesRemaining;
        final var dst = new byte[outLen];
        encodeImpl(src, dst, srcStart, 0, numFullBytes, numBytesRemaining);
        return dst;
    }

    @Override
    public byte @NotNull [] decode(byte @NotNull [] src, int srcStart, int numBytesToRead) {
        StrUtils.ensureStartLengthValid(srcStart, numBytesToRead, src.length);
        final var outLen = numBytesToRead << 1;
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
        final var numFullBytes = (numBytesToRead >> 1);
        // Number of bytes left. Should be [0, 4)
        final var numBytesRemaining = numBytesToRead - (numFullBytes << 1);
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
        var retl = numBytesRemaining;
        var srcPos = srcStart;
        var dstPos = dstStart;
        for (var i = 0; i < numFullBytes; i++) {
            dst[dstPos++] = (byte) ((BASE_TO_BYTE_PRE_COMPUTED[src[srcPos++] & 0xFF] << 4)
                    | (BASE_TO_BYTE_PRE_COMPUTED[src[srcPos++] & 0xFF]));
        }
        if (numBytesRemaining != 0) {
            retl += 1;
            dst[dstPos] = (byte) (BASE_TO_BYTE_PRE_COMPUTED[src[srcPos] & 0xFF] << 4);
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
        final var retl = numBytesToRead << 1;
        byte[] decoded;
        for (var i = 0; i < numBytesToRead; i++) {
            decoded = BYTE_TO_BASE_PRE_COMPUTED[src[srcPos++] & 0xFF];
            // This is the fastest way of setting all bits.
            System.arraycopy(decoded, 0, dst, dstPos, 2);
            dstPos += 2;
        }
        return retl;
    }
}
