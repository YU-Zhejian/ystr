package com.github.yu_zhejian.ystr.pw;

import com.github.yu_zhejian.ystr.translate.SimpleTranslator;

/**
 * Translated alignment configuration.
 *
 * <ul>
 *   <li>BLASTX -- Nucleotide-to-protein BLAST
 *   <li>TBLASTN -- Protein-to-nucleotide BLAST
 *   <li>TBLASTX -- Nucleotide-to-nucleotide BLAST, translated to protein before alignment.
 * </ul>
 *
 * @param gapOpenPenalty Negative score when a gap is opened.
 * @param gapExtendPenalty Negative score when a gap is extended.
 * @param scoreMtx Substitution matrix.
 */
public record TranslatedBlastConfig(
        int gapOpenPenalty,
        int gapExtendPenalty,
        int frameshiftPenalty,
        ScoreMtx scoreMtx,
        SimpleTranslator translator) {}
