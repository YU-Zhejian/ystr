package com.github.yu_zhejian.ystr.iter_utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class IteratorDuplicator<T> {
    private static final int NUM_INITIAL_ELEMENTS = 4;
    private final Iterator<T> sourceIterator;
    private final LinkedList<T> stage1;
    private final LinkedList<T> stage2;

    public IteratorDuplicator(Iterator<T> sourceIterator) {
        this.sourceIterator = sourceIterator;
        stage1 = new LinkedList<>();
        stage2 = new LinkedList<>();
        populateStage();
    }

    private void populateStage() {
        int i = 0;
        while (i < NUM_INITIAL_ELEMENTS && sourceIterator.hasNext()) {
            var next = sourceIterator.next();
            stage1.addLast(next);
            stage2.addLast(next);
            i++;
        }
    }

    private T next1() {
        if (!hasNext1()) {
            throw new NoSuchElementException();
        }
        return stage1.pollFirst();
    }

    private T next2() {
        if (!hasNext2()) {
            throw new NoSuchElementException();
        }
        return stage2.pollFirst();
    }

    private boolean hasNext1() {
        if (stage1.isEmpty()) {
            populateStage();
        }
        return !stage1.isEmpty();
    }

    private boolean hasNext2() {
        if (stage2.isEmpty()) {
            populateStage();
        }
        return !stage2.isEmpty();
    }

    public final Iterator<T> iterator1 = new Iterator<>() {
        @Override
        public boolean hasNext() {
            return hasNext1();
        }

        @Override
        public T next() {
            return next1();
        }
    };

    public final Iterator<T> iterator2 = new Iterator<>() {
        @Override
        public boolean hasNext() {
            return hasNext2();
        }

        @Override
        public T next() {
            return next2();
        }
    };
}
