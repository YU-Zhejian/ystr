package com.github.yu_zhejian.ystr.iter_utils;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Remove adjacent duplications.
 *
 * @param <T> As described.
 */
public final class IteratorDuplicationRemover<T> implements Iterator<T> {
    private final Iterator<T> sourceIterator;

    /** Whether {@link #currentValue} is reliable. */
    private boolean currentIsValid;

    /** Whether {@link #nextValue} is reliable. */
    private boolean nextIsValid;

    /** What to return when {@link #next()} is called. */
    private T currentValue;
    /**
     * Next value that is different from {@link #currentValue}. I.e., next value to return when
     * {@link #next()} is called.
     */
    private T nextValue;

    public IteratorDuplicationRemover(@NotNull Iterator<T> sourceIterator) {
        this.sourceIterator = sourceIterator;

        if (!sourceIterator.hasNext()) {
            currentIsValid = false;
            nextIsValid = false;
            currentValue = null;
            nextValue = null;
        } else {
            currentValue = sourceIterator.next();
            currentIsValid = true;
            nextIsValid = false;
            tryPopulateNext();
        }
    }

    @Override
    public boolean hasNext() {
        return currentIsValid;
    }

    /**
     * Assumes that {@link #nextIsValid} is false. Try to populate {@link #nextValue} for a value
     * that is different of {@link #currentValue}.
     */
    private void tryPopulateNext() {
        while (sourceIterator.hasNext() && !nextIsValid) {
            nextValue = sourceIterator.next();
            if (!Objects.equals(currentValue, nextValue)) {
                nextIsValid = true;
            }
        }
    }

    /**
     * As described.
     *
     * <p><b>Implementation</b>
     *
     * <ol>
     *   <li>If {@link #currentValue} is valid, will return {@link #currentValue}, and:
     *       <ol>
     *         <li>If {@link #nextValue} had already been calculated, will replace
     *             {@link #currentValue} with {@link #nextValue} and try find another
     *             {@link #nextValue} using {@link #tryPopulateNext()}.
     *         <li>If {@link #nextValue} is not calculated, will mark {@link #currentIsValid} to
     *             false, meaning the last available element is about to be returned.
     *       </ol>
     *   <li>Otherwise, throw {@link NoSuchElementException}.
     * </ol>
     *
     * @return As described.
     */
    @Override
    public T next() {
        if (currentIsValid) {
            final var retv = currentValue;
            if (nextIsValid) {
                currentValue = nextValue;
                nextIsValid = false;
                tryPopulateNext();
            } else {
                currentIsValid = false;
            }
            return retv;
        }
        throw new NoSuchElementException();
    }
}