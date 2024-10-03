package com.github.yu_zhejian.ystr.alphabet;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Class that maps {@link Byte} to a smaller space within unsigned {@link Integer}. Used for data
 * structures like Tries to reduce space.
 */
public final class AlphabetCodec {
    private final int[] preComputedEncodingTable;
    private final byte[] preComputedDecodingTable;
    private final Alphabet alphabet;

    /**
     * Default constructor.
     *
     * @param alphabet As described.
     * @param defaultValue Defaults for bytes that are not presented in the alphabet.
     * @throws IllegalArgumentException If the default value is not within the range of the
     *     alphabet.
     */
    public AlphabetCodec(final @NotNull Alphabet alphabet, final int defaultValue) {
        this.alphabet = alphabet;
        if (defaultValue < 0 || defaultValue > alphabet.length()) {
            throw new IllegalArgumentException("Default value " + defaultValue
                    + " is not in range [0, " + alphabet.length() + ")");
        }
        preComputedEncodingTable = new int[StrUtils.ALPHABET_SIZE];
        preComputedDecodingTable = new byte[alphabet.length()];
        Arrays.fill(preComputedEncodingTable, defaultValue);
        for (int i = 0; i < alphabet.length(); i++) {
            preComputedEncodingTable[alphabet.at(i) & StrUtils.BYTE_TO_UNSIGNED_MASK] = i;
            preComputedDecodingTable[i] = alphabet.at(i);
        }
    }

    public int encode(final byte b) {
        return preComputedEncodingTable[b & StrUtils.BYTE_TO_UNSIGNED_MASK];
    }

    public byte decode(final int i) {
        return preComputedDecodingTable[i];
    }

    public Alphabet getAlphabet() {
        return alphabet;
    }

    public static final AlphabetCodec DUMB_CODEC =
            new AlphabetCodec(AlphabetConstants.FULL_ALPHABET, 0);
}
