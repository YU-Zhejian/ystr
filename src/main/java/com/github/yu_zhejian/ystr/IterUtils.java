package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.iter_utils.IteratorDuplicationRemover;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Various utility functions concerning {@link Iterator}, {@link Iterable} and
 * {@link java.util.Collections} to support other classes.
 */
public final class IterUtils {

    private IterUtils() {}

    /**
     * Remove adjacent duplications. See {@link IteratorDuplicationRemover} for implementation.
     *
     * @param sourceIterator As described.
     * @return As described.
     * @param <T> As described.
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Iterator<T> dedup(final Iterator<T> sourceIterator) {
        return new IteratorDuplicationRemover<>(sourceIterator);
    }

    /**
     * {@link Long} version of {@link #arrayToIterator(Object[])}.
     *
     * @param array As described.
     * @return As described.
     */
    public static @NotNull Iterator<Long> arrayToIterator(final long @NotNull [] array) {
        final var retl = new ArrayList<Long>(array.length);
        for (final var i : array) {
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
    public static @NotNull Iterator<Integer> arrayToIterator(final int @NotNull [] array) {
        final var retl = new ArrayList<Integer>(array.length);
        for (final var i : array) {
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
    public static <T> @NotNull Iterator<T> arrayToIterator(final T @NotNull [] array) {
        final var retl = new ArrayList<T>(array.length);
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
    public static <T> @NotNull Iterable<T> iterable(final @NotNull Iterator<T> iterator) {
        return new Iterable<>() {
            @NotNull
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }
}
