package com.github.yu_zhejian.ystr.translate;

import com.github.yu_zhejian.ystr.alphabet.Alphabet;
import com.github.yu_zhejian.ystr.match.ShiftOrMatch;
import com.github.yu_zhejian.ystr.utils.StrUtils;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Simple translator that will translate exactly as input.
 *
 * <p>Use {@link Codons#getTranslator(int, byte[][])} to construct a translator using NCBI codon.
 *
 * @see Codons
 * @see <a href="https://web.expasy.org/translate/">Expasy Translate Server</a>
 */
public final class SimpleTranslator {
    /**
     * Codons in format of NCBI codon table.
     *
     * @see Codons#NCBI_CODON_TABLE
     */
    private final Alphabet codonTable;

    /** Start codons. */
    private final byte[][] startCodons;
    /**
     * Helper for fast calculation of positions in {@link #codonTable}. All bases except
     * {@code AGCTagct} will be considered {@code T}.
     */
    private static final byte[] ENCODE_NCBI;

    static {
        ENCODE_NCBI = new byte[StrUtils.ALPHABET_SIZE];
        for (int i = 0; i < StrUtils.ALPHABET_SIZE; i++) {
            ENCODE_NCBI[i] = switch ((byte) i) {
                case 'C', 'c' -> 1;
                case 'A', 'a' -> 2;
                case 'G', 'g' -> 3;
                default -> 0;
            };
        }
    }

    /**
     * Default constructor.
     *
     * @param codonTable As described.
     * @param startCodons As described.
     * @see Codons#getTranslator(int, byte[][])
     */
    public SimpleTranslator(final Alphabet codonTable, final byte[][] startCodons) {
        this.codonTable = codonTable;
        this.startCodons = startCodons;
    }

    /**
     * Find start codon.
     *
     * @param cdna As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    public @NotNull IntList startPos(final byte[] cdna, final int start, final int end) {
        final IntList pos = new IntArrayList();
        final var som = new ShiftOrMatch();
        for (var startCodon : startCodons) {
            pos.addAll(som.applyUnchecked(cdna, startCodon, start, end));
        }
        return pos;
    }

    /**
     * Translate cDNA to protein. Bases outside {@code AGCTagct} will be considered {@code T}.
     * Trailing bases will be ignored.
     *
     * @param cdna As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @throws IllegalArgumentException See {@link StrUtils#ensureStartEndValid(int, int, int)}.
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public byte @NotNull [] translate(final byte @NotNull [] cdna, final int start, final int end) {
        StrUtils.ensureStartEndValid(start, end, cdna.length);
        var cdnaLen = end - start;
        var numFullLength = cdnaLen / 3;
        final var retv = new byte[numFullLength];
        translateUnchecked(cdna, retv, start, end, 0);
        return retv;
    }

    /**
     * Translate to destination buffer.
     *
     * @param cdna As described.
     * @param dst As described.
     * @param start As described.
     * @param end As described.
     * @param dstStart As described.
     * @throws IllegalArgumentException See {@link StrUtils#ensureStartEndValid(int, int, int)}.
     * @see #translate(byte[], int, int)
     */
    public void translate(
            final byte @NotNull [] cdna,
            final byte @NotNull [] dst,
            final int start,
            final int end,
            final int dstStart) {
        StrUtils.ensureStartEndValid(start, end, cdna.length);
        var cdnaLen = end - start;
        var numFullLength = cdnaLen / 3;
        StrUtils.ensureStartLengthValid(dstStart, numFullLength, dst.length);
        translateUnchecked(cdna, dst, start, end, dstStart);
    }

    /**
     * Translation without boundary check.
     *
     * @param cdna As described.
     * @param dst As described.
     * @param start As described.
     * @param end As described.
     * @param dstStart As described.
     */
    public void translateUnchecked(
            final byte @NotNull [] cdna,
            final byte[] dst,
            final int start,
            final int end,
            final int dstStart) {
        var cdnaPos = start;
        var dstPos = dstStart;
        while (cdnaPos <= end - 3) {
            dst[dstPos] = getUnchecked(cdna, cdnaPos);
            cdnaPos += 3;
            dstPos++;
        }
    }

    /**
     * Get amino acid at current position.
     *
     * @param cdna As described.
     * @param start As described.
     * @return As described.
     * @throws IllegalArgumentException See {@link StrUtils#ensureStartEndValid(int, int, int)}.
     */
    public byte get(final byte @NotNull [] cdna, final int start) {
        StrUtils.ensureStartLengthValid(start, 3, cdna.length);
        return getUnchecked(cdna, start);
    }

    /**
     * Get amino acid at current position without boundary checks.
     *
     * @param cdna As described.
     * @param start As described.
     * @return As described.
     * @see #get(byte[], int)
     */
    @Contract(pure = true)
    public byte getUnchecked(final byte @NotNull [] cdna, final int start) {
        final int pos = (ENCODE_NCBI[cdna[start] & StrUtils.BYTE_TO_UNSIGNED_MASK] << 4)
                | (ENCODE_NCBI[cdna[start + 1] & StrUtils.BYTE_TO_UNSIGNED_MASK] << 2)
                | (ENCODE_NCBI[cdna[start + 2] & StrUtils.BYTE_TO_UNSIGNED_MASK]);
        return codonTable.at(pos);
    }
}
