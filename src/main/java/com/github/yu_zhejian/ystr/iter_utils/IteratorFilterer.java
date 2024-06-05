package com.github.yu_zhejian.ystr.iter_utils;

import java.util.Iterator;
import java.util.function.Predicate;

public final class IteratorFilterer<T> implements Iterator<T> {
    private final Iterator<T> sourceIterator;
    private final Predicate<T> predicate;
    private boolean currentIsValid;
    private T current;

    public void tryPopulateCurrent() {
        while (!currentIsValid && sourceIterator.hasNext()) {
            current = sourceIterator.next();
            if (predicate.test(current)) {
                currentIsValid = true;
                break;
            }
        }
    }

    public IteratorFilterer(Iterator<T> sourceIterator, Predicate<T> predicate) {
        this.sourceIterator = sourceIterator;
        this.predicate = predicate;
        currentIsValid = false;
        tryPopulateCurrent();
    }

    @Override
    public boolean hasNext() {
        return currentIsValid;
    }

    @Override
    public T next() {
        var retv = current;
        tryPopulateCurrent();
        return retv;
    }
}
