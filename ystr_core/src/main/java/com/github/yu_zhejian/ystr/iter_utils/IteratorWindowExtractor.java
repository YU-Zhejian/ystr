package com.github.yu_zhejian.ystr.iter_utils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Implemented with the help of TONGYI Lingma.
 *
 * @param <T> As described.
 */
public final class IteratorWindowExtractor<T> implements Iterator<List<T>> {
    /** As described. */
    private final Iterator<T> sourceIterator;
    /** As described. */
    private final int windowSize;
    /** As described. */
    private final List<T> currentWindow;
    /** As described. */
    private boolean hasNextBatch;

    /**
     * Default constructor.
     *
     * @param sourceIterator As described.
     * @param windowSize As described.
     */
    public IteratorWindowExtractor(@NotNull Iterator<T> sourceIterator, int windowSize) {
        this.sourceIterator = sourceIterator;
        this.windowSize = windowSize;
        hasNextBatch = sourceIterator.hasNext();
        currentWindow = new ObjectArrayList<>(windowSize);
        populateCurrentWindow();
    }

    /** As described. */
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
    public @NotNull List<T> next() {
        if (!hasNextBatch) {
            throw new NoSuchElementException();
        }
        List<T> windowCopy = new ArrayList<>(currentWindow);
        currentWindow.clear();
        populateCurrentWindow();
        return windowCopy;
    }
}
