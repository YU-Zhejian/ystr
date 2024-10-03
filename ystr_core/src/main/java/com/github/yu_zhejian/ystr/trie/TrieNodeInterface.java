package com.github.yu_zhejian.ystr.trie;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/** A node in trie. */
public interface TrieNodeInterface {
    /**
     * Get the value associated with the node.
     *
     * @return As described.
     */
    @Nullable
    Object getValue();

    /**
     * Set the value associated with the node.
     *
     * @param value As described.
     */
    void setValue(@Nullable Object value);

    /**
     * Recursive DFS implementation.
     *
     * @param prefix Current prefix, including the character that is represented by the current
     *     node.
     * @param words Output list of words.
     * @param values Output list of values.
     */
    void traverse(ByteArrayList prefix, List<byte[]> words, List<Object> values);

    /**
     * Indicator of whether the current node is at word end.
     *
     * @return As described.
     */
    boolean isWordEnd();
}
