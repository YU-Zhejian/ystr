package com.github.yu_zhejian.ystr_demo.tinymap.ds;

/** The current implementation would keep all IndexedPositions in memory. */
public class HashToIndexedPositionsMap {

    public IndexedPositions load(final long hash) {
        return new IndexedPositions(hash);
    }
}
