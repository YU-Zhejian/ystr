package com.github.yu_zhejian.ystr.io;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @param version BUS format version
 * @param bcLen Barcode length [1-32]
 * @param umiLen UMI length [1-32]
 * @param text Plain text header
 * @see <a href="https://github.com/BUStools/BUS-format">Specification</a>
 */
public record BUSHeader(int version, int bcLen, int umiLen, String text) {
    /** Fixed magic string. */
    static byte[] MAGIC = {'B', 'U', 'S', 0};
    /** Pre-computed fixed magic string. */
    static int MAGIC_INT;

    static {
        MAGIC_INT = (MAGIC[0]) | (MAGIC[1] << 8) | (MAGIC[2] << 16) | (MAGIC[3] << 24);
    }

    /**
     * Length of plain text header
     *
     * @return As described.
     */
    public int tlen() {
        return text.length();
    }

    @Contract("_ -> new")
    public static @NotNull BUSHeader parse(@NotNull ReadableByteChannel in) throws IOException {
        final var intBuffer = ByteBuffer.allocateDirect(Integer.BYTES);

        intBuffer.order(ByteOrder.LITTLE_ENDIAN);
        intBuffer.clear();
        in.read(intBuffer);
        intBuffer.rewind();

        var magicInt = intBuffer.getInt();
        if (MAGIC_INT != magicInt) {
            intBuffer.rewind();
            var magic = new byte[4];
            intBuffer.get(magic);
            throw new IllegalArgumentException(
                    "Invalid MAGIC. Should be 'BUS\\0' (%s). Actual: '%s‘ (%s)"
                            .formatted(
                                    Integer.toHexString(MAGIC_INT),
                                    Arrays.toString(magic),
                                    Integer.toHexString(magicInt)));
        }

        intBuffer.clear();
        in.read(intBuffer);
        intBuffer.rewind();
        var version = intBuffer.getInt();

        intBuffer.clear();
        in.read(intBuffer);
        intBuffer.rewind();
        var bcLen = intBuffer.getInt();

        intBuffer.clear();
        in.read(intBuffer);
        intBuffer.rewind();
        var umiLen = intBuffer.getInt();
        if (umiLen > 32 || umiLen < 1) {
            throw new IllegalArgumentException(
                    "Illegal UMI length; Range: [1, 32], Actual: %d".formatted(umiLen));
        }

        intBuffer.clear();
        in.read(intBuffer);
        intBuffer.rewind();
        var tlen = intBuffer.getInt();
        if (tlen > 32 || tlen < 1) {
            throw new IllegalArgumentException(
                    "Illegal Barcode length; Range: [1, 32], Actual: %d".formatted(tlen));
        }

        var textBuffer = ByteBuffer.allocate(tlen);
        in.read(textBuffer);
        textBuffer.rewind();
        var textBytes = textBuffer.array();
        return new BUSHeader(
                version, bcLen, umiLen, new String(textBytes, StandardCharsets.US_ASCII));
    }
}
