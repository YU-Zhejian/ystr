package com.github.yu_zhejian.ystr_demo.tinymap;

public enum IndexType {
    /** Used for organisms with limited number of long chromosomes. */
    CHR_SPLIT_IDX(0),
    /**
     * Used for NCBI NT databases, Roche 454/PacBio/nanopore reads, assembled transcripts, ESTs, and
     * others.
     */
    UNIFIED_SPLIT_IDX(1);
    public final int type;

    IndexType(int type) {
        this.type = type;
    }
}
