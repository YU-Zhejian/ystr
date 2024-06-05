package com.github.yu_zhejian.ystr.iter_utils;

import java.util.Iterator;
import java.util.function.BiFunction;

public final class IteratorCombinator<V, T, U> implements Iterator<V> {
    private final BiFunction<T, U, V> combinator;
    private final Iterator<T> tIterator;
    private final Iterator<U> uIterator;

    public IteratorCombinator(
            BiFunction<T, U, V> combinator, Iterator<T> tIterator, Iterator<U> uIterator) {
        this.combinator = combinator;
        this.tIterator = tIterator;
        this.uIterator = uIterator;
    }

    @Override
    public boolean hasNext() {
        return tIterator.hasNext() && uIterator.hasNext();
    }

    @Override
    public V next() {
        return combinator.apply(tIterator.next(), uIterator.next());
    }
}
