package com.github.yu_zhejian.ystr_demo.tinymap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
