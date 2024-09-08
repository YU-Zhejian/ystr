package com.github.yu_zhejian.ystr.unsorted;

import com.github.yu_zhejian.ystr.utils.AlphabetCodec;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * A simple data structure that stores a set of strings. This method requires an encoder to reduce
 * space consumed by nodes.
 */
public final class Trie extends BaseTrie {
    private final AlphabetCodec abCodec;
    private final TrieNode root;

    public Trie(final AlphabetCodec abCodec) {
        this.abCodec = abCodec;
        root = new TrieNode();
    }

    public Trie() {
        this(AlphabetCodec.DUMB_CODEC);
    }

    @Override
    public void clear() {
        root.value = null;
        Arrays.fill(root.mapping, null);
        root.isWordEnd = false;
    }

    @Override
    public void add(final byte @NotNull [] s) {
        var node = root;
        for (byte b : s) {
            int encodedB = abCodec.encode(b);
            if (node.mapping[encodedB] == null) {
                node.mapping[encodedB] = new TrieNode();
            }
            node = node.mapping[encodedB];
        }
        if (!node.isWordEnd) {
            node.isWordEnd = true;
            numWords++;
        }
        treeHeight = Integer.max(treeHeight, s.length);
    }

    /**
     * Search for one node.
     *
     * @param s String to search.
     * @return Identified node. {@code null} if the node was not found.
     */
    @Contract(pure = true)
    private @Nullable TrieNode getNode(final byte @NotNull [] s) {
        var node = root;
        for (byte b : s) {
            int encodedB = abCodec.encode(b);
            if (node.mapping[encodedB] == null) {
                return null;
            }
            node = node.mapping[encodedB];
        }
        return node;
    }

    @Override
    public @Nullable Object get(byte @NotNull [] s) {
        var node = getNode(s);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public void set(byte @NotNull [] s, @Nullable Object o) {
        var node = getNode(s);
        if (node == null) {
            return;
        }
        node.value = o;
    }

    @Override
    public boolean contains(final byte @NotNull [] s) {
        var node = getNode(s);
        return node != null && node.isWordEnd;
    }

    @Override
    public void traverse(
            final byte[] prefix, final @NotNull List<byte[]> words, final List<Object> values) {
        var node = getNode(prefix);
        if (node == null) {
            return;
        }
        var ba = new ByteArrayList(prefix);
        ba.ensureCapacity(treeHeight);
        node.traverse(ba, words, values);
    }

    /**
     * A node contains mapping to {@link #abCodec} child nodes. Child node will be set to
     * {@code null} if not exist.
     */
    private class TrieNode extends BaseTrieNode {
        private final TrieNode[] mapping;

        /** Default constructor. */
        private TrieNode() {
            mapping = new TrieNode[abCodec.getAlphabet().length()];
            Arrays.fill(mapping, null);
        }

        @Override
        public int numNodes() {
            int reti = 1;
            for (final var node : mapping) {
                if (node != null) {
                    reti += node.numNodes();
                }
            }
            return reti;
        }

        @Override
        public void traverse(ByteArrayList prefix, List<byte[]> words, List<Object> values) {
            if (isWordEnd) {
                words.add(prefix.toByteArray());
                values.add(value);
            }
            for (int i = 0; i < mapping.length; i++) {
                if (mapping[i] != null) {
                    prefix.add(abCodec.decode(i));
                    mapping[i].traverse(prefix, words, values);
                    prefix.popByte();
                }
            }
        }
    }
}
