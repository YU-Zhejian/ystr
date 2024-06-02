package com.github.yu_zhejian.ystr;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/** Various utility functions to support other classes. */
public final class StrUtils {
    private StrUtils() {}

    /**
     * Integer power mimicking {@link Math#pow(double, double)}.
     *
     * <p><b>Implementation Limitations</b>
     *
     * <ul>
     *   <li>This method does not perform overflow detection.
     *   <li>This supports non-negative {@code p} and {@code q} only.
     * </ul>
     *
     * @param p The base.
     * @param q The exponent.
     * @return As described.
     */
    public static int pow(int p, int q) {
        if (p < 0 || q < 0) {
            throw new IllegalArgumentException(
                    "p and q should be non-negative. Actual: %d, %d".formatted(p, q));
        }
        int retv = 1;
        for (int i = q; i > 0; i--) {
            retv = retv * p;
        }
        return retv;
    }

    /**
     * Long version of {@link #pow(int, int)}
     *
     * @param p As described.
     * @param q As described.
     * @return As described.
     */
    public static long pow(long p, int q) {
        if (p < 0 || q < 0) {
            throw new IllegalArgumentException(
                    "p and q should be non-negative. Actual: %d, %d".formatted(p, q));
        }
        long retv = 1;
        for (int i = q; i > 0; i--) {
            retv = retv * p;
        }
        return retv;
    }

    /**
     * Ensure start and end is valid for some open-close interval.
     *
     * @param start As described.
     * @param end As described.
     */
    public static void ensureStartEndValid(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException(
                    "start must be less than end. Actual: %d vs %d".formatted(start, end));
        }
        if (start < 0) {
            throw new IllegalArgumentException(
                    "start must be greater than or equal to zero. Actual: %d".formatted(start));
        }
    }

    /**
     * Ensure start and end is valid for some open-close interval within a string.
     *
     * @param start As described.
     * @param end As described.
     * @param strLen As described.
     */
    public static void ensureStartEndValid(int start, int end, int strLen) {
        ensureStartEndValid(start, end);
        if (end > strLen) {
            throw new IllegalArgumentException(
                    "end must be less than strLen. Actual: %d vs %d".formatted(end, strLen));
        }
    }

    /**
     * Remove adjacent duplications. See {@link UniqueAdjacentIterator} for implementation.
     *
     * @param sourceIterator As described.
     * @return As described.
     * @param <T> As described.
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Iterator<T> dedup(Iterator<T> sourceIterator) {
        return new UniqueAdjacentIterator<>(sourceIterator);
    }

    /**
     * {@link Long} version of {@link #arrayToIterator(Object[])}.
     *
     * @param array As described.
     * @return As described.
     */
    public static @NotNull Iterator<Long> arrayToIterator(long @NotNull [] array) {
        var retl = new ArrayList<Long>(array.length);
        for (var i : array) {
            retl.add(i);
        }
        return retl.iterator();
    }

    /**
     * {@link Integer} version of {@link #arrayToIterator(Object[])}.
     *
     * @param array As described.
     * @return As described.
     */
    public static @NotNull Iterator<Integer> arrayToIterator(int @NotNull [] array) {
        var retl = new ArrayList<Integer>(array.length);
        for (var i : array) {
            retl.add(i);
        }
        return retl.iterator();
    }

    /**
     * Convert some array to iterator. This method uses an {@link ArrayList} for storing the values.
     *
     * @param array As described.
     * @param <T> As described.
     * @return As described.
     */
    public static <T> @NotNull Iterator<T> arrayToIterator(T @NotNull [] array) {
        var retl = new ArrayList<T>(array.length);
        retl.addAll(Arrays.asList(array));
        return retl.iterator();
    }

    /**
     * Convert some iterable to some iterator.
     *
     * @param iterator As described.
     * @return As described.
     * @param <T> As described.
     */
    public static <T> @NotNull Iterable<T> iterable(Iterator<T> iterator) {
        return new Iterable<>() {
            @NotNull
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }
}

/**
 * Remove adjacent duplications.
 *
 * @param <T> As described.
 */
class UniqueAdjacentIterator<T> implements Iterator<T> {
    private final Iterator<T> sourceIterator;

    public UniqueAdjacentIterator(@NotNull Iterator<T> sourceIterator) {
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
            var retv = currentValue;
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
