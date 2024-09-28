package com.github.yu_zhejian.ystr.pw;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/** A simple substitution matrix wrapper. */
public final class ScoreMtx {
    /** Substitution matrix. */
    private final int[][] mtx;

    /**
     * Default constructor.
     *
     * @param mtx Desired substitution matrix.
     */
    private ScoreMtx(final int[][] mtx) {
        this.mtx = mtx;
    }

    public ScoreMtx(final InputStreamReader isr) throws IOException {
        final var bis = new BufferedReader(isr);
        String line;
        while ((line = bis.readLine()) != null) {
            if (line.charAt(0) == '#') {
                continue;
            }
            // TODO
        }
        this.mtx = new int[StrUtils.ALPHABET_SIZE][StrUtils.ALPHABET_SIZE];
    }

    /**
     * Get score for 2 nucleotides/animo acids.
     *
     * @param b1 As described.
     * @param b2 As described.
     * @return As described.
     */
    public int get(final byte b1, final byte b2) {
        return mtx[b1 & StrUtils.BYTE_TO_UNSIGNED_MASK][b2 & StrUtils.BYTE_TO_UNSIGNED_MASK];
    }

    /**
     * Generate a simple matrix with match and mismatch only.
     *
     * @param matchScore Positive score for match.
     * @param mismatchPenalty Negative score for mismatch.
     * @return As described.
     */
    @Contract("_, _ -> new")
    public @NotNull ScoreMtx simpleSubstMtx(final int matchScore, final int mismatchPenalty) {
        final int[][] substMtx = new int[StrUtils.ALPHABET_SIZE][StrUtils.ALPHABET_SIZE];
        for (int i = 0; i < mtx.length; i++) {
            Arrays.fill(substMtx[i], mismatchPenalty);
            substMtx[i][i] = matchScore;
        }
        return new ScoreMtx(substMtx);
    }
}
