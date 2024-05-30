package com.github.yu_zhejian.ystr;

import org.jetbrains.annotations.NotNull;

/** Encoding of biological strings and friends. */
public final class StrEncoder {
    private StrEncoder() {}

    /**
     * A simple DNA/RNA encoder algorithm that encodes nucleotides to 4-bit integers while retaining
     * masking information.
     *
     * <ol>
     *   <li>All bases except {@code AGCTUagctu} would be considered {@code N}.
     *   <li>{@code T} and {@code U} will be distinguished.
     *   <li>Upper- and lower-cased bases could be distinguished by observing its leading bit. If
     *       this bit is set, will be lower-cased nucleotides.
     * </ol>
     *
     * @param nt As described.
     * @return As described.
     */
    public static short simpleNucleotideEncoder(byte nt) {
        switch (nt) {
            case 'A' -> {
                return 0b0001;
            }
            case 'C' -> {
                return 0b0010;
            }
            case 'G' -> {
                return 0b0011;
            }
            case 'T' -> {
                return 0b0100;
            }
            case 'U' -> {
                return 0b0101;
            }
            case 'a' -> {
                return 0b1001;
            }
            case 'c' -> {
                return 0b1010;
            }
            case 'g' -> {
                return 0b1011;
            }
            case 't' -> {
                return 0b1100;
            }
            case 'u' -> {
                return 0b1101;
            }
            case 'n' -> {
                return 0b1110;
            }
            default -> {
                return 0b0110;
            }
        }
    }

    /**
     * Decoder to {@link #simpleNucleotideEncoder(byte)}. Will decode to upper-case nucleotides with
     * {@code U} decoded as {@code T}.
     *
     * @param nt As described.
     * @return As described.
     */
    public static byte simpleNucleotideDecoder(short nt) {
        switch (nt) {
            case 0b0000 -> {
                return 0;
            }
            case 0b0001 -> {
                return 'A';
            }
            case 0b0010 -> {
                return 'C';
            }
            case 0b0011 -> {
                return 'G';
            }
            case 0b0100 -> {
                return 'T';
            }
            case 0b0101 -> {
                return 'U';
            }
            case 0b1001 -> {
                return 'a';
            }
            case 0b1010 -> {
                return 'c';
            }
            case 0b1011 -> {
                return 'g';
            }
            case 0b1100 -> {
                return 't';
            }
            case 0b1101 -> {
                return 'u';
            }
            case 0b1110 -> {
                return 'n';
            }
            default -> {
                return 'N';
            }
        }
    }

    /**
     * Encode DNA/RNA string using {@link #simpleNucleotideEncoder(byte)}. This implementation will
     * compact the resulting string by compacting 2 3-bit nucleotides into one byte.
     *
     * @param string As described.
     * @return As described.
     */
    public static byte @NotNull [] simpleKmerEncoder(byte @NotNull [] string) {
        if (string.length == 0) {
            return new byte[0];
        }
        int retlen = (string.length + ((string.length & 0x1) == 1 ? 1 : 0)) >> 1;
        var retb = new byte[retlen];
        int i;
        for (i = 0; i < string.length - 1; i += 2) {
            retb[i / 2] = (byte) ((simpleNucleotideEncoder(string[i]) << 4)
                    | simpleNucleotideEncoder(string[i + 1]));
        }
        if (string.length == i + 1) {
            retb[i / 2] = (byte) ((simpleNucleotideEncoder(string[i]) << 4));
        }
        return retb;
    }

    /**
     * Decode string encoded by {@link #simpleKmerEncoder(byte[])}
     *
     * @param string As described.
     * @return As described.
     */
    public static byte @NotNull [] simpleKmerDecoder(byte @NotNull [] string) {
        if (string.length == 0) {
            return new byte[0];
        }
        int retlen = string.length << 1;
        if ((string[string.length - 1] & 0b1111) == 0b0000) {
            retlen -= 1;
        }
        var retb = new byte[retlen];
        var retbPos = 0;
        int i;
        for (i = 0; i < string.length - 1; i += 1) {
            retb[retbPos] = simpleNucleotideDecoder((short) ((string[i] & 0b11110000) >> 4));
            retbPos += 1;
            retb[retbPos] = simpleNucleotideDecoder((short) (string[i] & 0b1111));
            retbPos += 1;
        }
        retb[retbPos] = simpleNucleotideDecoder((short) ((string[i] & 0b11110000) >> 4));
        retbPos += 1;
        if (retbPos != retlen) {
            retb[retbPos] = simpleNucleotideDecoder((short) (string[i] & 0b1111));
        }
        return retb;
    }

    /**
     * Case-insensitive 2bit encoder. Will treat all bases except {@code AGCagc} as {@code T}.
     *
     * @param nt As described.
     * @return As described.
     */
    public static short twoBitNucleotideEncoder(byte nt) {
        switch (nt) {
            case 'A', 'a' -> {
                return 0b10;
            }
            case 'C', 'c' -> {
                return 0b01;
            }
            case 'G', 'g' -> {
                return 0b11;
            }
            default -> {
                return 0b00;
            }
        }
    }

    /**
     * Encode data into simplified UCSC 2bit format.
     *
     * <p>Note, this implementation deals only with sequences. It would not generate 2bit headers or
     * masks. This implementation will only deal with upper-case {@code AGCT} nucleotides.
     *
     * <p>Note, {@code T} will be padded if the string is not long enough. Always remember how long
     * the string is before encoding!
     *
     * @param string As described.
     * @return As described.
     * @see <a href="http://jcomeau.freeshell.org/www/genome/2bitformat.html">Some external
     *     introduction to the 2bit format.</a>
     */
    public static byte @NotNull [] simpleTwoBitEncoder(byte @NotNull [] string) {
        if (string.length == 0) {
            return new byte[0];
        }
        var last4len = string.length - ((string.length >> 2) << 2);
        var retlen = (string.length >> 2) + (last4len == 0 ? 0 : 1);
        var retb = new byte[retlen];
        int i;
        for (i = 0; i < string.length - 4; i += 4) {
            retb[i / 4] = (byte) (((twoBitNucleotideEncoder(string[i])) << 6)
                    | (twoBitNucleotideEncoder(string[i + 1]) << 4)
                    | (twoBitNucleotideEncoder(string[i + 2]) << 2)
                    | twoBitNucleotideEncoder(string[i + 3]));
        }
        while (i < string.length) {
            retb[retlen - 1] = (byte) (retb[retlen - 1] << 2 | twoBitNucleotideEncoder(string[i]));
            i += 1;
        }
        if (last4len != 0) {
            last4len = 4 - last4len;
            while (last4len > 0) {
                retb[retlen - 1] = (byte) (retb[retlen - 1] << 2);
                last4len -= 1;
            }
        }
        return retb;
    }
}
