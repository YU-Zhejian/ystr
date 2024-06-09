package com.github.yu_zhejian.ystr.iter_utils;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

/**
 * Remove adjacent duplications. Duplications could be defined using extracted values.
 *
 * @param <T> The type of iterable items before extraction.
 * @param <U> The type of iterable items after extraction.
 */
public final class IteratorDuplicationRemover<T, U> implements Iterator<T> {
    private final Iterator<T> sourceIterator;

    /** Whether {@link #currentValue} is reliable. */
    private boolean currentIsValid;

    /** Whether {@link #nextValue} is reliable. */
    private boolean nextIsValid;

    /** What to return when {@link #next()} is called. */
    private T currentValue;

    /** Extraction result of {@link #currentValue}. */
    private U currentExtracted;
    /**
     * Next value that is different from {@link #currentValue}. I.e., next value to return when
     * {@link #next()} is called.
     */
    private T nextValue;

    /** Extraction result of {@link #nextValue}. */
    private U nextExtracted;

    /** Function that performs extraction. */
    private final Function<T, U> extractor;

    /**
     * Default constructor.
     *
     * @param sourceIterator As described.
     * @param extractor As described.
     */
    public IteratorDuplicationRemover(
            @NotNull Iterator<T> sourceIterator, Function<T, U> extractor) {
        this.sourceIterator = sourceIterator;
        this.extractor = extractor;

        if (!sourceIterator.hasNext()) {
            currentIsValid = false;
            nextIsValid = false;
            currentValue = null;
            nextValue = null;
        } else {
            currentValue = sourceIterator.next();
            currentExtracted = extractor.apply(currentValue);
            currentIsValid = true;
            nextIsValid = false;
            nextExtracted = null;
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
            nextExtracted = extractor.apply(nextValue);
            if (!Objects.equals(currentExtracted, nextExtracted)) {
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
                currentExtracted = nextExtracted;
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
