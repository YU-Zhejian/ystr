package com.github.yu_zhejian.ystr.trie;

import com.github.yu_zhejian.ystr.container.NopList;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A string-to-whatever Map implemented using Trie.
 *
 * @param <V> Type of the values.
 */
public final class TrieMap<V> implements Map<byte[], V> {

    private final TrieInterface trie;

    /**
     * Default constructor that may convert an established trie to a map.
     *
     * @param trie The internal trie instance.
     */
    public TrieMap(final TrieInterface trie) {
        this.trie = trie;
    }

    /**
     * Default constructor.
     *
     * @param trieSupplier Supplier of the internal trie.
     */
    public TrieMap(final @NotNull Supplier<TrieInterface> trieSupplier) {
        this(trieSupplier.get());
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
        trie.clear();
    }

    @Override
    public @NotNull Set<byte[]> keySet() {
        return new ObjectArraySet<>(trie.traverse().toArray());
    }

    @Override
    public @NotNull Collection<V> values() {
        var retl = new ObjectArrayList<>();
        retl.ensureCapacity(trie.numWords());
        trie.traverse(new NopList<>(), retl);
        var typedRetl = new ObjectArrayList<V>();
        typedRetl.ensureCapacity(trie.numWords());
        for (final var entry : retl) {
            typedRetl.add((V) entry);
        }
        return typedRetl;
    }

    @Override
    public @NotNull Set<Entry<byte[], V>> entrySet() {
        var values = new ObjectArrayList<>();
        var keys = new ObjectArrayList<byte[]>();
        values.ensureCapacity(trie.numWords());
        keys.ensureCapacity(trie.numWords());
        trie.traverse(keys, values);
        var valuesIterator = values.iterator();
        var retl = new ObjectArrayList<Entry<byte[], V>>();
        for (var key : keys) {
            retl.add(Map.entry(key, (V) valuesIterator.next()));
        }
        return new ObjectArraySet<>(retl.toArray());
    }
}
