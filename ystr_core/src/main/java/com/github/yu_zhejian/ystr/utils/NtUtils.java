package com.github.yu_zhejian.ystr.utils;

import it.unimi.dsi.fastutil.bytes.Byte2ByteMap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/** Utilities for nucleotides. */
public final class NtUtils {
    private static final byte[] TO_UPPER_TRANSL_TABLE;
    private static final byte[] TO_LOWER_TRANSL_TABLE;
    private static final byte[] COMPLEMENTARY_TRANSL_TABLE;

    /** Defunct Constructor * */
    private NtUtils() {}

    static {
        TO_UPPER_TRANSL_TABLE = new byte[StrUtils.ALPHABET_SIZE];
        TO_LOWER_TRANSL_TABLE = new byte[StrUtils.ALPHABET_SIZE];
        for (int i = 0; i < StrUtils.ALPHABET_SIZE; i++) {
            if ('A' <= i && i <= 'Z') {
                TO_LOWER_TRANSL_TABLE[i] = (byte) (i + 'a' - 'A');
            } else {
                TO_LOWER_TRANSL_TABLE[i] = (byte) i;
            }
            if ('a' <= i && i <= 'z') {
                TO_UPPER_TRANSL_TABLE[i] = (byte) (i - 'a' + 'A');
            } else {
                TO_UPPER_TRANSL_TABLE[i] = (byte) i;
            }
        }
        COMPLEMENTARY_TRANSL_TABLE = makeTrans(
                "ATCGatcg".getBytes(StandardCharsets.US_ASCII),
                "TAGCtagc".getBytes(StandardCharsets.US_ASCII));
    }

    @Contract(pure = true)
    public static byte @NotNull [] makeTrans(
            final byte @NotNull [] from, final byte @NotNull [] to) {
        if (from.length != to.length) {
            throw new IllegalArgumentException();
        }
        var translTable = new byte[StrUtils.ALPHABET_SIZE];
        for (int i = 0; i < StrUtils.ALPHABET_SIZE; i++) {
            translTable[i] = (byte) (i);
        }
        for (int i = 0; i < from.length; i++) {
            translTable[from[i] & StrUtils.BYTE_TO_UNSIGNED_MASK] = to[i];
        }
        return translTable;
    }

    @Contract(pure = true)
    public static byte @NotNull [] makeTrans(final Map<Byte, Byte> fromToMap) {
        var translTable = new byte[StrUtils.ALPHABET_SIZE];
        for (int i = 0; i < StrUtils.ALPHABET_SIZE; i++) {
            translTable[i] = (byte) (i);
        }
        for (final var entry : fromToMap.entrySet()) {
            translTable[entry.getKey() & StrUtils.BYTE_TO_UNSIGNED_MASK] = entry.getValue();
        }
        return translTable;
    }

    @Contract(pure = true)
    public static byte @NotNull [] makeTrans(final Byte2ByteMap fromToMap) {
        var translTable = new byte[StrUtils.ALPHABET_SIZE];
        for (int i = 0; i < StrUtils.ALPHABET_SIZE; i++) {
            translTable[i] = (byte) (i);
        }
        for (final var entry : fromToMap.byte2ByteEntrySet()) {
            translTable[entry.getByteKey() & StrUtils.BYTE_TO_UNSIGNED_MASK] = entry.getByteValue();
        }
        return translTable;
    }

    public static void mapBasedTranslateInPlaceUnchecked(
            final byte[] seq, final int start, final int end, final byte[] translTable) {
        for (int i = start; i < end; i++) {
            seq[i] = translTable[seq[i] & StrUtils.BYTE_TO_UNSIGNED_MASK];
        }
    }

    public static void mapBasedTranslateInPlace(
            final byte @NotNull [] seq, final int start, final int end, final byte[] translTable) {
        StrUtils.ensureStartEndValid(start, end, seq.length);
        mapBasedTranslateInPlaceUnchecked(seq, start, end, translTable);
    }

    public static void toUpperInPlaceUnchecked(final byte[] seq, final int start, final int end) {
        mapBasedTranslateInPlace(seq, start, end, TO_UPPER_TRANSL_TABLE);
    }

    public static void toLowerInPlaceUnchecked(final byte[] seq, final int start, final int end) {
        mapBasedTranslateInPlace(seq, start, end, TO_LOWER_TRANSL_TABLE);
    }

    public static void toUpperInPlace(final byte @NotNull [] seq, final int start, final int end) {
        StrUtils.ensureStartEndValid(start, end, seq.length);
        toUpperInPlaceUnchecked(seq, start, end);
    }

    public static void toLowerInPlace(final byte @NotNull [] seq, final int start, final int end) {
        StrUtils.ensureStartEndValid(start, end, seq.length);
        toLowerInPlaceUnchecked(seq, start, end);
    }

    public static void maskLowerInPlaceUnchecked(
            final byte[] seq, final int start, final int end, final byte mask) {
        final var translTable = new byte[StrUtils.ALPHABET_SIZE];
        for (int i = 0; i < StrUtils.ALPHABET_SIZE; i++) {
            if ('a' <= i && i <= 'z') {
                translTable[i] = mask;
            } else {
                translTable[i] = (byte) i;
            }
        }
        mapBasedTranslateInPlace(seq, start, end, translTable);
    }

    public static void maskLowerInPlace(
            final byte @NotNull [] seq, final int start, final int end, final byte mask) {
        StrUtils.ensureStartEndValid(start, end, seq.length);
        maskLowerInPlaceUnchecked(seq, start, end, mask);
    }

    public static void reverseInPlace(final byte @NotNull [] seq, final int start, final int end) {
        StrUtils.ensureStartEndValid(start, end, seq.length);
        reverseInPlaceUnchecked(seq, start, end);
    }

    public static void reverseInPlaceUnchecked(final byte[] seq, final int start, final int end) {
        int left = start;
        int right = end - 1;
        byte temp;
        while (left < right) {
            temp = seq[left];
            seq[left] = seq[right];
            seq[right] = temp;
            left++;
            right--;
        }
    }

    public static void complementaryInPlace(
            final byte @NotNull [] seq, final int start, final int end) {
        StrUtils.ensureStartEndValid(start, end, seq.length);
        complementaryInPlaceUnchecked(seq, start, end);
    }

    public static void complementaryInPlaceUnchecked(
            final byte[] seq, final int start, final int end) {
        mapBasedTranslateInPlaceUnchecked(seq, start, end, COMPLEMENTARY_TRANSL_TABLE);
    }

    public static void reverseComplementaryInPlace(
            final byte @NotNull [] seq, final int start, final int end) {
        StrUtils.ensureStartEndValid(start, end, seq.length);
        reverseComplementaryInPlaceUnchecked(seq, start, end);
    }

    public static void reverseComplementaryInPlaceUnchecked(
            final byte[] seq, final int start, final int end) {
        complementaryInPlaceUnchecked(seq, start, end);
        reverseInPlaceUnchecked(seq, start, end);
    }
}
