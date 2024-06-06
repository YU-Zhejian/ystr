package com.github.yu_zhejian.ystr.iter_utils;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.Iterator;

public class IteratorRanker<T> implements Iterator<Tuple2<T, Integer>> {
    private final Iterator<T> sourceIterator;
    private int curPos;

    public IteratorRanker(final Iterator<T> sourceIterator, final int start) {
        this.sourceIterator = sourceIterator;
        this.curPos = start;
    }

    @Override
    public boolean hasNext() {
        return sourceIterator.hasNext();
    }

    @Override
    public Tuple2<T, Integer> next() {
        var retv = Tuple.of(sourceIterator.next(), curPos);
        curPos++;
        return retv;
    }
}
