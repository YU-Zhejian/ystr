package com.github.yu_zhejian.ystr_demo.tinymap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The index configuration.
 *
 * @param kmerSize Length of each k-mer.
 * @param numKmerPerMinimizer Number of k-mers to consider when calculating minimizers.
 * @param ntShannonEntropyCutoff k-mer with Shannon entropy lower than this value will be ignored.
 */
public record GenomeIndexerConfig(
        int kmerSize, int numKmerPerMinimizer, double ntShannonEntropyCutoff) {
    @Contract(" -> new")
    @NotNull
    public static GenomeIndexerConfig minimap2() {
        return new GenomeIndexerConfig(14, 10, 0.7);
    }

    /**
     * The canonical NCBI Blast index which uses 7-mers without minimizer optimization.
     *
     * @return As described.
     */
    @Contract(" -> new")
    @NotNull
    public static GenomeIndexerConfig blast() {
        return new GenomeIndexerConfig(7, 1, 0.7);
    }
}
