package com.github.yu_zhejian.ystr.trie;

import com.github.yu_zhejian.ystr.alphabet.AlphabetCodec;

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
public final class CodecTrie extends BaseTrie {
    private final AlphabetCodec abCodec;
    private final TrieNode root;

    /**
     * Default constructor.
     *
     * @param abCodec As described.
     */
    public CodecTrie(final AlphabetCodec abCodec) {
        this.abCodec = abCodec;
        root = new TrieNode();
        numNodes++;
    }

    /**
     * Constructor using dumb encoder.
     *
     * <p><b>WARNING</b> This trie will be of tremendous size since each node holds an array of 256
     * elements.
     */
    public CodecTrie() {
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
                numNodes++;
            }
            node = node.mapping[encodedB];
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
            int encodedB = abCodec.encode(b);
            if (node.mapping[encodedB] == null) {
                return null;
            }
            node = node.mapping[encodedB];
        }
        return node;
    }

    /**
     * A node contains mapping to {@link #abCodec} child nodes. Child node will be set to
     * {@code null} if not exist.
     */
    private final class TrieNode extends BaseTrieNode {
        private final TrieNode[] mapping;

        /** Default constructor. */
        private TrieNode() {
            mapping = new TrieNode[abCodec.getAlphabet().length()];
            Arrays.fill(mapping, null);
        }

        @Override
        public void traverse(
                final ByteArrayList prefix, final List<byte[]> words, final List<Object> values) {
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
