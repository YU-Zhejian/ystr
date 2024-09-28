package com.github.yu_zhejian.ystr.container;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class NopMap<K, V> implements Map<K, V> {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Contract(pure = true)
    @Override
    public @Nullable V get(Object key) {
        return null;
    }

    @Override
    public @Nullable V put(K key, V value) {
        return null;
    }

    @Contract(pure = true)
    @Override
    public @Nullable V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        // Do nothing!
    }

    @Override
    public void clear() {
        // Do nothing!
    }

    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable Set<K> keySet() {
        return Set.of();
    }

    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable Collection<V> values() {
        return List.of();
    }

    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable Set<Entry<K, V>> entrySet() {
        return Set.of();
    }
}
