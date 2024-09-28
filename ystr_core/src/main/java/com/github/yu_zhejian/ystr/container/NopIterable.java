package com.github.yu_zhejian.ystr.container;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public abstract class NopIterable<T> implements Iterable<T> {
    @Override
    public @NotNull Iterator<T> iterator() {
        return new NopIterator<>();
    }
}
