package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.iter_utils.IteratorWindowExtractor;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Various utility functions concerning {@link Iterator}, {@link Iterable} and
 * {@link java.util.Collections} to support other classes.
 */
public final class IterUtils {

    private IterUtils() {}

    /**
     * Remove adjacent duplications.
     *
     * @param list As described.
     * @return As described.
     * @param <T> As described.
     */
    public static <T> @NotNull List<T> dedup(@NotNull List<T> list) {
        var result = new ArrayList<T>();
        if (list.isEmpty()) {
            return result;
        }
        var prev = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            var current = list.get(i);
            if (!Objects.equals(prev, current)) {
                result.add(current);
                prev = current;
            }
        }
        return result;
    }

    public static @NotNull IntArrayList dedup(@NotNull IntArrayList list) {
        var result = new IntArrayList();
        if (list.isEmpty()) {
            return result;
        }
        var prev = list.getInt(0);
        for (int i = 1; i < list.size(); i++) {
            var current = list.getInt(i);
            if (prev != current) {
                result.add(current);
                prev = current;
            }
        }
        return result;
    }

    /**
     * Collect current iterator to {@link ArrayList}.
     *
     * <p>Warning, this method impairs performance.
     *
     * @param sourceIterator As described.
     * @return As described.
     * @param <T> Type of elements inside the iterator.
     */
    public static <T> @NotNull List<T> collect(@NotNull Iterator<T> sourceIterator) {
        var retl = new ArrayList<T>();
        while (sourceIterator.hasNext()) {
            retl.add(sourceIterator.next());
        }
        return retl;
    }

    /**
     * Consume the entire iterator.
     *
     * @param sourceIterator As described.
     */
    public static void exhaust(@NotNull Iterator<?> sourceIterator) {
        while (sourceIterator.hasNext()) {
            sourceIterator.next();
        }
    }

    /**
     * Return the indices of items inside {@code sourceIterator} which were evaluated to
     * {@code true}.
     *
     * @param sourceIterator As described.
     * @param predicate As described.
     * @return As described.
     * @param <T> As described.
     */
    public static <T> @NotNull IntArrayList where(
            @NotNull Iterator<T> sourceIterator, Predicate<T> predicate) {
        var retl = new IntArrayList();
        var i = 0;
        while (sourceIterator.hasNext()) {
            if (predicate.test(sourceIterator.next())) {
                retl.add(i);
            }
            i++;
        }
        return retl;
    }

    public static @NotNull IntArrayList where(
            @NotNull DoubleArrayList list, Predicate<Double> predicate) {
        var retl = new IntArrayList();
        for (var i = 0; i < list.size(); i++) {
            if (predicate.test(list.getDouble(i))) {
                retl.add(i);
            }
            i++;
        }
        return retl;
    }

    @Contract("_, _ -> new")
    public static <T> @NotNull Iterator<List<T>> window(
            @NotNull Iterator<T> sourceIterator, int windowSize) {
        return new IteratorWindowExtractor<>(sourceIterator, windowSize);
    }
}
