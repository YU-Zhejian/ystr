package com.github.yu_zhejian.ystr.trie;

import com.github.yu_zhejian.ystr.StrLibc;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * A simple data structure that stores a set of strings. This method requires an encoder to reduce
 * space consumed by nodes.
 */
public final class MapBasedTrie extends BaseTrie {
    private final MapBasedTrieNode root;
    private final Supplier<Byte2ObjectMap<MapBasedTrieNode>> mapSupplier;

    /**
     * Default Constructor that supports customizable internal map implementation.
     *
     * @param mapSupplier The internal map implementation.
     */
    public MapBasedTrie(final Supplier<Byte2ObjectMap<MapBasedTrieNode>> mapSupplier) {
        this.mapSupplier = mapSupplier;
        root = new MapBasedTrieNode(mapSupplier);
        numNodes++;
    }

    /**
     * Default constructor that uses {@link Byte2ObjectAVLTreeMap} for internal map implementation.
     */
    public MapBasedTrie() {
        this(() -> new Byte2ObjectAVLTreeMap<>(StrLibc::strcmp));
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
            node.mapping.computeIfAbsent(b, i -> {
                numNodes++;
                return new MapBasedTrieNode(mapSupplier);
            });
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

    /** A node contains mapping to child nodes. */
    public static class MapBasedTrieNode extends BaseTrieNode {
        private final Byte2ObjectMap<MapBasedTrieNode> mapping;

        /** Default constructor that supports passing of customized maps. */
        private MapBasedTrieNode(
                final @NotNull Supplier<Byte2ObjectMap<MapBasedTrieNode>> mapSupplier) {
            mapping = mapSupplier.get();
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
