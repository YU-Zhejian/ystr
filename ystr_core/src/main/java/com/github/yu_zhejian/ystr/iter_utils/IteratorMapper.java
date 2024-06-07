package com.github.yu_zhejian.ystr.iter_utils;

import java.util.Iterator;
import java.util.function.Function;

public final class IteratorMapper<T, V> implements Iterator<V> {
    private final Iterator<T> sourceIterator;
    private final Function<T, V> mapper;

    public IteratorMapper(final Iterator<T> sourceIterator, final Function<T, V> mapper) {
        this.sourceIterator = sourceIterator;
        this.mapper = mapper;
    }

    @Override
    public V next() {
        return mapper.apply(sourceIterator.next());
    }

    @Override
    public boolean hasNext() {
        return sourceIterator.hasNext();
    }
}
