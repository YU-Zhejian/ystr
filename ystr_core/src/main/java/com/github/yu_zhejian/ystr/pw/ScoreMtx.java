package com.github.yu_zhejian.ystr.pw;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import java.util.Arrays;

/** A simple substitution matrix wrapper. */
public class ScoreMtx {
    /** Substitution matrix. */
    private final int[][] mtx;

    /**
     * Default constructor.
     *
     * @param mtx Desired substitution matrix.
     */
    public ScoreMtx(final int[][] mtx) {
        this.mtx = mtx;
    }

    /**
     * Get score for 2 nucleotides/animo acids.
     *
     * @param b1 As described.
     * @param b2 As described.
     * @return As described.
     */
    public int get(final byte b1, final byte b2) {
        return mtx[b1 & 0xFF][b2 & 0xFF];
    }

    /**
     * Generate a simple matrix with match and mismatch only.
     *
     * @param matchScore Positive score for match.
     * @param mismatchPenalty Negative score for mismatch.
     * @return As described.
     */
    public ScoreMtx simpleSubstMtx(final int matchScore, final int mismatchPenalty) {
        final int[][] substMtx = new int[StrUtils.ALPHABET_SIZE][StrUtils.ALPHABET_SIZE];
        for (int i = 0; i < mtx.length; i++) {
            Arrays.fill(substMtx[i], mismatchPenalty);
            substMtx[i][i] = matchScore;
        }
        return new ScoreMtx(substMtx);
    }
}
