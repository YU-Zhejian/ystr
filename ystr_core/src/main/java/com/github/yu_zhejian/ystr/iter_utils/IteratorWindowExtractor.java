package com.github.yu_zhejian.ystr.iter_utils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Implemented with the help of TONGYI Lingma.
 *
 * @param <T>
 */
public class IteratorWindowExtractor<T> implements Iterator<List<T>> {
    private final Iterator<T> sourceIterator;
    private final int windowSize;
    private final List<T> currentWindow;
    private boolean hasNextBatch;

    public IteratorWindowExtractor(@NotNull Iterator<T> sourceIterator, int windowSize) {
        this.sourceIterator = sourceIterator;
        this.windowSize = windowSize;
        this.hasNextBatch = sourceIterator.hasNext();
        this.currentWindow = new ObjectArrayList<>(windowSize);
        populateCurrentWindow();
    }

    private void populateCurrentWindow() {
        while (currentWindow.size() < windowSize && sourceIterator.hasNext()) {
            currentWindow.add(sourceIterator.next());
        }
        if (currentWindow.isEmpty()) {
            hasNextBatch = false;
        }
    }

    @Override
    public boolean hasNext() {
        return hasNextBatch;
    }

    @Override
    public List<T> next() {
        if (!hasNextBatch) {
            throw new NoSuchElementException();
        }
        List<T> windowCopy = new ArrayList<>(currentWindow);
        currentWindow.clear();
        populateCurrentWindow();
        return windowCopy;
    }
}
