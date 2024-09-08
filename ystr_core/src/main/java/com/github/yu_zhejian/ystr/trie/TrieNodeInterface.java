package com.github.yu_zhejian.ystr.trie;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface TrieNodeInterface {
    @Nullable
    Object getValue();

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

    boolean isWordEnd();
}
