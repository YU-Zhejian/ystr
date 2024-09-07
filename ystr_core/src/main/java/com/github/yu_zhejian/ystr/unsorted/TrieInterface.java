package com.github.yu_zhejian.ystr.unsorted;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface TrieInterface {
    /**
     * Insert a new string into the trie and set the associating value to {@code null}. If the
     * string exists, will do nothing.
     *
     * @param s The inserting string.
     */
    void add(byte @NotNull [] s);

    /**
     * Determine whether a string exists inside the trie.
     *
     * @param s As described.
     * @return As described.
     */
    boolean contains(byte @NotNull [] s);

    /**
     * Get the value associated with the word. Will return {@code null} if the word do not exist.
     *
     * @param s As described.
     * @return As described.
     */
    @Nullable
    Object get(byte @NotNull [] s);

    /**
     * Set value to a trie word. Will do nothing if the word does not exist.
     *
     * @param s As described.
     * @param o As described.
     */
    void set(byte @NotNull [] s, @Nullable Object o);

    /**
     * Get number of nodes inside the trie. Useful for calculating memory consumption.
     *
     * @return As described.
     */
    int numNodes();

    /**
     * Get number of words inside the trie.
     *
     * @return As described.
     */
    int numWords();

    /**
     * {@link #traverse(byte[])} with empty prefix. I.e., get all words inside.
     *
     * @return As described.
     */
    @NotNull
    default List<byte[]> traverse() {
        return traverse(new byte[] {});
    }

    /**
     * Pre-order traversal of all stored words that share some prefix. If no word starts with the
     * prefix, will return an empty list; If the prefix is itself a word, it will also be returned
     * as well.
     *
     * @param prefix As described.
     * @return As described.
     */
    @NotNull
    List<byte[]> traverse(byte[] prefix);

    /**
     * Tree height is the length of the longest word.
     *
     * @return As described.
     */
    int treeHeight();

    @NotNull
    default List<TrieNodeInterface> traverseNodes() {
        return traverseNodes(new byte[] {});
    }

    @NotNull
    List<TrieNodeInterface> traverseNodes(byte[] prefix);
}
