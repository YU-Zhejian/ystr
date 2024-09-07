package com.github.yu_zhejian.ystr.unsorted;

import com.github.yu_zhejian.ystr.utils.AlphabetCodec;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * A simple data structure that stores a set of strings. This method requires an encoder to reduce
 * space consumed by nodes.
 */
public final class Trie implements TrieInterface {
    private final AlphabetCodec abCodec;
    private final TrieNode root;
    private int treeHeight;
    private int numWords;

    public Trie(final AlphabetCodec abCodec) {
        this.abCodec = abCodec;
        root = new TrieNode();
    }

    public Trie() {
        this(AlphabetCodec.DUMB_CODEC);
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
    public int numNodes() {
        return root.numNodes();
    }

    @Override
    public int numWords() {
        return numWords;
    }

    @Override
    public @NotNull List<byte[]> traverse(final byte[] prefix) {
        var retl = new ObjectArrayList<byte[]>();
        retl.ensureCapacity(numWords);
        var node = getNode(prefix);
        if (node == null) {
            return retl;
        }
        var ba = new ByteArrayList(prefix);
        ba.ensureCapacity(treeHeight);
        node.traverse(ba, retl);
        return retl;
    }

    @Override
    public int treeHeight() {
        return treeHeight;
    }

    /**
     * A node contains mapping to {@link #abCodec} child nodes. Child node will be set to
     * {@code null} if not exist.
     */
    private class TrieNode implements TrieNodeInterface {
        private final TrieNode[] mapping;
        /** Whether a word ends here. This separates real word ends with intermediate nodes. */
        private boolean isWordEnd = false;

        private Object value;

        /** Default constructor. */
        private TrieNode() {
            mapping = new TrieNode[abCodec.getAlphabet().length()];
            Arrays.fill(mapping, null);
        }

        /**
         * Get number of children nodes, including myself.
         *
         * @return As described.
         */
        private int numNodes() {
            int reti = 1;
            for (final var node : mapping) {
                if (node != null) {
                    reti += node.numNodes();
                }
            }
            return reti;
        }

        /**
         * Recursive DFS implementation.
         *
         * @param prefix Current prefix, including the character that is represented by the current
         *     node.
         * @param retl Output list.
         */
        private void traverse(final ByteArrayList prefix, final List<byte[]> retl) {
            if (isWordEnd) {
                retl.add(prefix.toByteArray());
            }
            for (int i = 0; i < mapping.length; i++) {
                if (mapping[i] != null) {
                    prefix.add(abCodec.decode(i));
                    mapping[i].traverse(prefix, retl);
                    prefix.popByte();
                }
            }
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public void setValue(Object value) {
            this.value = value;
        }
    }
}
