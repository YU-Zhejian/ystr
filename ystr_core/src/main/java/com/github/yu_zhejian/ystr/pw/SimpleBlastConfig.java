package com.github.yu_zhejian.ystr.pw;

/**
 * Simple alignment configuration for:
 *
 * <ul>
 *   <li>BLASTP -- Protein-to-protein BLAST
 *   <li>BLASTN -- Nucleotide-to-nucleotide BLAST
 * </ul>
 *
 * @param gapOpenPenalty Negative score when a gap is opened.
 * @param gapExtendPenalty Negative score when a gap is extended.
 * @param scoreMtx Substitution matrix.
 */
public record SimpleBlastConfig(int gapOpenPenalty, int gapExtendPenalty, ScoreMtx scoreMtx) {}
