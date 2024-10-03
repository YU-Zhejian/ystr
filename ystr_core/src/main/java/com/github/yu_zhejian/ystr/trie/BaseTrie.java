package com.github.yu_zhejian.ystr.trie;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/** A basic implementation of trie. */
public abstract class BaseTrie implements TrieInterface {
    /** @see #treeHeight() */
    protected int treeHeight;
    /** @see #numWords() */
    protected int numWords;
    /** @see #numNodes() */
    protected int numNodes;

    @Override
    public int numNodes() {
        return numNodes;
    }

    @Override
    public final int numWords() {
        return numWords;
    }

    @Override
    public final int treeHeight() {
        return treeHeight;
    }

    /**
     * Search for one node.
     *
     * @param s String to search.
     * @return Identified node. {@code null} if the node was not found.
     */
    @Contract(pure = true)
    protected abstract @Nullable TrieNodeInterface getNode(final byte @NotNull [] s);

    @Override
    public boolean contains(final byte @NotNull [] s) {
        var node = getNode(s);
        return node != null && node.isWordEnd();
    }

    @Override
    public @Nullable Object get(final byte @NotNull [] s) {
        var node = getNode(s);
        if (node == null) {
            return null;
        }
        return node.getValue();
    }

    @Override
    public void set(final byte @NotNull [] s, final @Nullable Object o) {
        var node = getNode(s);
        if (node == null) {
            add(s);
            node = Objects.requireNonNull(getNode(s));
        }
        node.setValue(o);
    }

    @Override
    public void traverse(
            final byte[] prefix,
            final @NotNull List<byte[]> words,
            final @NotNull List<Object> values) {
        var node = getNode(prefix);
        if (node == null) {
            return;
        }
        var ba = new ByteArrayList(prefix);
        ba.ensureCapacity(treeHeight);
        node.traverse(ba, words, values);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + "treeHeight="
                + treeHeight + ", numWords="
                + numWords + ", numNodes="
                + numNodes() + '}';
    }
}
