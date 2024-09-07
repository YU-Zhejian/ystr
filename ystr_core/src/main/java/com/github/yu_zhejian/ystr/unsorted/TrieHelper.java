package com.github.yu_zhejian.ystr.unsorted;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class TrieHelper<V> implements Map<byte[], V> {

    private final TrieInterface trie;

    public TrieHelper(final TrieInterface trie) {
        this.trie = trie;
    }

    @Override
    public int size() {
        return trie.numWords();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof byte[] keyBytes) {
            return trie.contains(keyBytes);
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(Object key) {
        if (key instanceof byte[] keyBytes) {
            return (V) trie.get(keyBytes);
        }
        return null;
    }

    @Override
    public @Nullable V put(byte[] key, V value) {
        if (!trie.contains(key)) {
            trie.add(key);
        }
        var get = (V) trie.get(key);
        trie.set(key, value);
        return get;
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void putAll(@NotNull Map<? extends byte[], ? extends V> m) {
        for (var entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public @NotNull Set<byte[]> keySet() {
        return new ObjectArraySet<>(trie.traverse().toArray());
    }

    @Override
    public @NotNull Collection<V> values() {
        var retl = new ObjectArrayList<V>();
        retl.ensureCapacity(trie.numWords());
        for (final var node : trie.traverseNodes()) {
            retl.add((V) node.getValue());
        }
        return retl;
    }

    @Override
    public @NotNull Set<Entry<byte[], V>> entrySet() {
        var keys = keySet();
        var values = values().iterator();
        var retl = new ObjectArrayList<Entry<byte[], V>>();
        retl.ensureCapacity(trie.numWords());
        for (var key : keys) {
            retl.add(Map.entry(key, values.next()));
        }
        return new ObjectArraySet<>(retl.toArray());
    }
}
