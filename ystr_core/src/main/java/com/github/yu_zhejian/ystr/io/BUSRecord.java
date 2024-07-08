package com.github.yu_zhejian.ystr.io;

import com.github.yu_zhejian.ystr.codec.TwoBitCodec;
import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/** @see <a href="https://github.com/BUStools/BUS-format">Specification</a> */
public final class BUSRecord {
    private final long barcode;
    private final long umi;
    private final int ec;
    private final long count;
    private final int flags;
    private final BUSHeader header;

    /**
     * @param barcode 2-bit encoded barcode.
     * @param umi 2-bit encoded UMI
     * @param ec equivalence class
     * @param count fragment count
     * @param flags flags
     * @param header Associated read header.
     */
    public BUSRecord(
            final long barcode,
            final long umi,
            final int ec,
            final long count,
            final int flags,
            final BUSHeader header) {
        this.barcode = barcode;
        this.umi = umi;
        this.ec = ec;
        this.count = count;
        this.flags = flags;
        this.header = header;
    }

    /** Number of bytes to read. The BUS record is rounded up to 32 bytes. */
    public static final int BIN_SIZE = 32;

    /** Convert UCSC 2bit encoding to BUS 2bit encoding. {@code TCAG -> ACGT}. */
    private static final byte[] UCSC_TWOBIT_TO_BUS_TWOBIT_TRANSLATOR;

    static {
        UCSC_TWOBIT_TO_BUS_TWOBIT_TRANSLATOR = new byte[StrUtils.ALPHABET_SIZE];
        for (int i = 0; i < StrUtils.ALPHABET_SIZE; i++) {
            UCSC_TWOBIT_TO_BUS_TWOBIT_TRANSLATOR[i] = switch (i) {
                case 'T' -> 'A';
                case 'A' -> 'G';
                case 'G' -> 'T';
                case 'C' -> 'C';
                default -> 0;
            };
        }
    }

    public static @NotNull BUSRecord parse(
            final @NotNull ByteBuffer buffer, final BUSHeader header) {
        var barcode = buffer.getLong();
        var umi = buffer.getLong();
        var ec = buffer.getInt();
        var count = Integer.toUnsignedLong(buffer.getInt());
        var flags = buffer.getInt();
        return new BUSRecord(barcode, umi, ec, count, flags, header);
    }

    private void decode(final byte[] dst, final long val) {
        var arr = new byte[Long.BYTES];
        var buff = ByteBuffer.wrap(arr);
        buff.order(ByteOrder.BIG_ENDIAN);
        buff.clear();
        buff.putLong(val);
        var tbc = new TwoBitCodec();
        var decoded = tbc.decode(arr);
        for (var i = 0; i < decoded.length; i++) {
            decoded[i] = UCSC_TWOBIT_TO_BUS_TWOBIT_TRANSLATOR[decoded[i]];
        }
        System.arraycopy(decoded, decoded.length - dst.length, dst, 0, dst.length);
    }

    @Contract(pure = true)
    public byte @NotNull [] getDecodedUMI() {
        var umiLen = header.umiLen();
        var retl = new byte[umiLen];
        decode(retl, umi);
        return retl;
    }

    @Contract(pure = true)
    public byte @NotNull [] getDecodedBarcode() {
        var bcLen = header.bcLen();
        var retl = new byte[bcLen];
        decode(retl, barcode);
        return retl;
    }

    @Override
    public @NotNull String toString() {
        return new String(getDecodedBarcode(), StandardCharsets.US_ASCII) + "\t"
                + new String(getDecodedUMI(), StandardCharsets.US_ASCII) + "\t" + ec + "\t" + count
                + "\t" + flags + "\n";
    }
}
