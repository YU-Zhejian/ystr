package com.github.yu_zhejian.ystr.iter_utils;

import java.util.Iterator;
import java.util.function.BiFunction;

/**
 * Combine 2 iterators by some function.
 * If those iterators are not of equal length, will stop when the shorter one exhausts.
 * <p>
 * Warning, this method may impair performance.
 *
 * @param <V> Return data type.
 * @param <T> Data type of one iterator.
 * @param <U> Data type of another iterator.
 */
public final class IteratorCombinator<V, T, U> implements Iterator<V> {
    /**
     * Function for combining values from 2 iterators.
     */
    private final BiFunction<T, U, V> combinator;
    /**
     * One source iterator.
     */
    private final Iterator<T> tIterator;
    /**
     * Another source iterator.
     */
    private final Iterator<U> uIterator;

    /**
     * Default constructor.
     *
     * @param combinator As described.
     * @param tIterator As described.
     * @param uIterator As described.
     */
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
