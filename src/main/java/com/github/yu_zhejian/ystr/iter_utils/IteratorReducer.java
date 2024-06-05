package com.github.yu_zhejian.ystr.iter_utils;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class IteratorReducer <T, V> implements Supplier<V> {
    private final Iterator<T> sourceIterator;
    private final BiFunction<T,V, V> reducer;
    private final V initialValue;

    public IteratorReducer(final Iterator<T> sourceIterator, final BiFunction<T,V, V> reducer, V initialValue){
        this.sourceIterator = sourceIterator;
        this.reducer = reducer;
        this.initialValue = initialValue;
    }

    @Override
    public V get() {
        var currentResult = initialValue;
        while (sourceIterator.hasNext()){
            currentResult = reducer.apply(sourceIterator.next(), currentResult);
        }
        return currentResult;
    }
}
