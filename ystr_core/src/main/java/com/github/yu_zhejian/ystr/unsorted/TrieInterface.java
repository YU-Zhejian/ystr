package com.github.yu_zhejian.ystr.unsorted;

import com.github.yu_zhejian.ystr.container.NopList;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

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

    /** Remove all items inside a trie. */
    void clear();

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
    default List<byte[]> traverse(byte[] prefix) {
        var retl = new ObjectArrayList<byte[]>();
        retl.ensureCapacity(numWords());
        traverse(prefix, retl, new NopList<>());
        return retl;
    }

    /**
     * {@link #traverse(byte[])} for both words and values stored in nodes.
     *
     * @param prefix As described.
     * @param words As described.
     * @param values As described.
     */
    void traverse(byte[] prefix, List<byte[]> words, List<Object> values);

    /**
     * {@link #traverse(byte[], List, List)} with empty prefix.
     *
     * @param words As described.
     * @param values As described.
     */
    default void traverse(List<byte[]> words, List<Object> values) {
        traverse(new byte[] {}, words, values);
    }

    /**
     * Tree height is the length of the longest word.
     *
     * @return As described.
     */
    int treeHeight();
}
