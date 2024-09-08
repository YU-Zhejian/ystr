package com.github.yu_zhejian.ystr.trie;

import com.github.yu_zhejian.ystr.StrLibc;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class TernarySearchTrie extends BaseTrie {
    private TernaryTrieNode root;
    private boolean isEmptyPresented; // Special node to store empty string
    private Object empty;

    public TernarySearchTrie() {
        root = null;
        empty = null;
        isEmptyPresented = false;
    }

    /**
     * Adapted from <a href="https://www.geeksforgeeks.org/ternary-search-tree/">Geeks for
     * Geeks</a>.
     *
     * @param node As described.
     * @param word As described.
     * @param offset As described.
     * @return As described.
     */
    private @NotNull TernaryTrieNode add(
            TernaryTrieNode node,
            final byte[] word,
            final int offset,
            final int currentTreeHeight) {
        if (node == null) {
            node = new TernaryTrieNode();
            numNodes++;
            node.myByte = word[offset];
        }
        int strcmpResult = StrLibc.strcmp(word[offset], node.myByte);

        if (strcmpResult < 0) {
            node.smaller = add(node.smaller, word, offset, currentTreeHeight + 1);
        } else if (strcmpResult > 0) {
            node.larger = add(node.larger, word, offset, currentTreeHeight + 1);
        } else {
            if (offset < word.length - 1) {
                node.equal = add(node.equal, word, offset + 1, currentTreeHeight + 1);
            } else {
                if (!node.isWordEnd) {
                    node.isWordEnd = true;
                    numWords++;
                    treeHeight = Integer.max(treeHeight, currentTreeHeight);
                }
            }
        }
        return node;
    }

    @Override
    public void add(final byte @NotNull [] s) {
        if (s.length == 0 && !isEmptyPresented) {
            isEmptyPresented = true;
            empty = null;
            numWords++;
            return;
        }
        root = add(root, s, 0, 0);
    }

    @Override
    public @Nullable Object get(final byte @NotNull [] s) {
        if (s.length == 0) {
            return empty;
        }
        return super.get(s);
    }

    @Override
    public boolean contains(final byte @NotNull [] s) {
        if (s.length == 0) {
            return isEmptyPresented;
        }
        return super.contains(s);
    }

    @Override
    public int numNodes() {
        return numNodes + (isEmptyPresented ? 1 : 0);
    }

    @Override
    public void set(byte @NotNull [] s, @Nullable Object o) {
        if (s.length == 0) {
            isEmptyPresented = true;
            empty = o;
            return;
        }
        super.set(s, o);
    }

    @Override
    public void clear() {
        root = null;
        empty = null;
        isEmptyPresented = false;
    }

    @Override
    public void traverse(byte[] prefix, @NotNull List<byte[]> words, @NotNull List<Object> values) {
        if (isEmptyPresented && prefix.length == 0) {
            words.add(new byte[0]);
            values.add(empty);
        }
        TernaryTrieNode node;
        if (prefix.length == 0) {
            node = root;
            if (node == null) {
                return;
            }
        } else {
            node = getNode(root, prefix, 0);
            if (node == null || node.equal == null) {
                return;
            }
            node = node.equal;
        }
        var ba = new ByteArrayList(prefix);
        ba.ensureCapacity(treeHeight);
        node.traverse(ba, words, values);
    }

    /**
     * Adapted from <a href="https://www.geeksforgeeks.org/ternary-search-tree/">Geeks for
     * Geeks</a>.
     *
     * @param node As described.
     * @param word As described.
     * @param offset As described.
     * @return As described.
     */
    private @Nullable TernaryTrieNode getNode(
            final @Nullable TernaryTrieNode node, final byte @NotNull [] word, final int offset) {
        if (node == null) {
            return null;
        }
        int strcmpResult = StrLibc.strcmp(word[offset], node.myByte);

        if (strcmpResult < 0) {
            return getNode(node.smaller, word, offset);
        } else if (strcmpResult > 0) {
            return getNode(node.larger, word, offset);
        } else {
            if (offset < word.length - 1) {
                return getNode(node.equal, word, offset + 1);
            } else {
                return node;
            }
        }
    }

    @Override
    @Contract(pure = true)
    protected @Nullable TrieNodeInterface getNode(final byte @NotNull [] s) {
        return getNode(root, s, 0);
    }

    private static class TernaryTrieNode extends BaseTrieNode {
        private TernaryTrieNode smaller;
        private TernaryTrieNode larger;
        private TernaryTrieNode equal;
        private byte myByte;

        @Override
        public void traverse(
                final ByteArrayList prefix, final List<byte[]> words, final List<Object> values) {
            if (smaller != null) {
                smaller.traverse(prefix, words, values);
            }
            prefix.add(myByte);
            if (isWordEnd) {
                words.add(prefix.toByteArray());
                values.add(value);
            }
            if (equal != null) {
                equal.traverse(prefix, words, values);
            }
            prefix.popByte();
            if (larger != null) {
                larger.traverse(prefix, words, values);
            }
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "TernaryTrieNode[" + (char) myByte + (isWordEnd ? ", E" : "") + "]{" + value
                    + '}';
        }
    }
}
