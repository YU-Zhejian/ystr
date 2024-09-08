package com.github.yu_zhejian.ystr.unsorted;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import java.util.List;

public interface TrieNodeInterface {
    Object getValue();

    void setValue(Object value);

    /**
     * Get number of children nodes, including myself.
     *
     * @return As described.
     */
    int numNodes();

    /**
     * Recursive DFS implementation.
     *
     * @param prefix Current prefix, including the character that is represented by the current
     *     node.
     * @param words Output list of words.
     * @param values Output list of values.
     */
    void traverse(ByteArrayList prefix, List<byte[]> words, List<Object> values);
}
