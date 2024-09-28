package com.github.yu_zhejian.ystr.container;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NopIterator<T> implements Iterator<T> {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        throw new NoSuchElementException();
    }
}
