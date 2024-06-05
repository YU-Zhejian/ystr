package com.github.yu_zhejian.ystr.iter_utils;

import java.util.Iterator;

public final class IteratorFiltererByAnother<T> implements Iterator<T> {
    private final Iterator<T> sourceIterator;
    private final Iterator<Boolean> predicate;
    private boolean currentIsValid;
    private T current;

    public void tryPopulateCurrent() {
        while (!currentIsValid && sourceIterator.hasNext() && predicate.hasNext()) {
            current = sourceIterator.next();
            if (predicate.next()) {
                currentIsValid = true;
                break;
            }
        }
    }

    public IteratorFiltererByAnother(Iterator<T> sourceIterator, Iterator<Boolean> predicate) {
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
