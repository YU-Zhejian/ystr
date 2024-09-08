package com.github.yu_zhejian.ystr.trie;

import com.github.yu_zhejian.ystr.StrLibc;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A simple data structure that stores a set of strings. This method requires an encoder to reduce
 * space consumed by nodes.
 */
public final class MapBasedTrie extends BaseTrie {
    private final MapBasedTrieNode root;

    public MapBasedTrie() {
        root = new MapBasedTrieNode();
    }

    @Override
    public void clear() {
        root.value = null;
        root.mapping.clear();
        root.isWordEnd = false;
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

    @Override
    @Contract(pure = true)
    protected @Nullable TrieNodeInterface getNode(final byte @NotNull [] s) {
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
    protected TrieNodeInterface getRoot() {
        return root;
    }

    /** A node contains mapping to child nodes. */
    private static class MapBasedTrieNode extends BaseTrieNode {
        private final Byte2ObjectMap<MapBasedTrieNode> mapping;

        /** Default constructor. */
        private MapBasedTrieNode() {
            mapping = new Byte2ObjectAVLTreeMap<>(StrLibc::strcmp);
        }

        @Override
        public int numNodes() {
            int reti = 1;
            for (final var node : mapping.values()) {
                if (node != null) {
                    reti += node.numNodes();
                }
            }
            return reti;
        }

        @Override
        public void traverse(
                final ByteArrayList prefix, final List<byte[]> words, final List<Object> values) {
            if (isWordEnd) {
                words.add(prefix.toByteArray());
                values.add(value);
            }
            for (final var entry : mapping.byte2ObjectEntrySet()) {
                if (entry.getValue() != null) {
                    prefix.add(entry.getByteKey());
                    entry.getValue().traverse(prefix, words, values);
                    prefix.popByte();
                }
            }
        }
    }
}
