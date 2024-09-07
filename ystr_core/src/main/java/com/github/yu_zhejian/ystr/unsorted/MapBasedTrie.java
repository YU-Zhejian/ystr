package com.github.yu_zhejian.ystr.unsorted;

import com.github.yu_zhejian.ystr.StrLibc;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A simple data structure that stores a set of strings. This method requires an encoder to reduce
 * space consumed by nodes.
 */
public final class MapBasedTrie implements TrieInterface {
    private final MapBasedTrieNode root;
    private int treeHeight;
    private int numWords;

    public MapBasedTrie() {
        root = new MapBasedTrieNode();
    }

    @Override
    public void add(final byte @NotNull [] s) {
        var node = root;
        for (byte b : s) {
            node.mapping.computeIfAbsent(b, i -> new MapBasedTrieNode());
            node = node.mapping.get(b);
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
    private @Nullable MapBasedTrie.MapBasedTrieNode getNode(final byte @NotNull [] s) {
        var node = root;
        for (byte b : s) {
            if (!node.mapping.containsKey(b)) {
                return null;
            }
            node = node.mapping.get(b);
        }
        return node;
    }

    @Override
    public boolean contains(final byte @NotNull [] s) {
        var node = getNode(s);
        return node != null && node.isWordEnd;
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

    @Override
    public @NotNull List<TrieNodeInterface> traverseNodes(byte[] prefix) {
        var retl = new ObjectArrayList<TrieNodeInterface>();
        retl.ensureCapacity(numWords);
        var node = getNode(prefix);
        if (node == null) {
            return retl;
        }
        var ba = new ByteArrayList(prefix);
        ba.ensureCapacity(treeHeight);
        node.traverseNode(ba, retl);
        return retl;
    }

    /** A node contains mapping to child nodes. */
    private static class MapBasedTrieNode implements TrieNodeInterface {
        private Object value = null;

        private final Byte2ObjectMap<MapBasedTrieNode> mapping;
        /** Whether a word ends here. This separates real word ends with intermediate nodes. */
        private boolean isWordEnd = false;

        /** Default constructor. */
        private MapBasedTrieNode() {
            mapping = new Byte2ObjectAVLTreeMap<>(StrLibc::strcmp);
        }

        /**
         * Get number of children nodes, including myself.
         *
         * @return As described.
         */
        private int numNodes() {
            int reti = 1;
            for (final var node : mapping.values()) {
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
            for (final var entry : mapping.byte2ObjectEntrySet()) {
                if (entry.getValue() != null) {
                    prefix.add(entry.getByteKey());
                    entry.getValue().traverse(prefix, retl);
                    prefix.popByte();
                }
            }
        }

        private void traverseNode(final ByteArrayList prefix, final List<TrieNodeInterface> retl) {
            if (isWordEnd) {
                retl.add(this);
            }
            for (final var entry : mapping.byte2ObjectEntrySet()) {
                if (entry.getValue() != null) {
                    prefix.add(entry.getByteKey());
                    entry.getValue().traverseNode(prefix, retl);
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
