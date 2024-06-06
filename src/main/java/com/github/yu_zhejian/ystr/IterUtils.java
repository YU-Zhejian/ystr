package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.iter_utils.IteratorCombinator;
import com.github.yu_zhejian.ystr.iter_utils.IteratorDuplicationRemover;
import com.github.yu_zhejian.ystr.iter_utils.IteratorDuplicator;
import com.github.yu_zhejian.ystr.iter_utils.IteratorFilterer;
import com.github.yu_zhejian.ystr.iter_utils.IteratorFiltererByAnother;
import com.github.yu_zhejian.ystr.iter_utils.IteratorMapper;
import com.github.yu_zhejian.ystr.iter_utils.IteratorRanker;
import com.github.yu_zhejian.ystr.iter_utils.IteratorReducer;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

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
     * @param extractor As described.
     * @return As described.
     * @param <T> As described.
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static <T, U> @NotNull Iterator<T> dedup(
            final Iterator<T> sourceIterator, Function<T, U> extractor) {
        return new IteratorDuplicationRemover<>(sourceIterator, extractor);
    }

    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Iterator<T> dedup(final Iterator<T> sourceIterator) {
        return new IteratorDuplicationRemover<>(sourceIterator, i -> i);
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

    @SafeVarargs
    public static <T> @NotNull Iterator<T> of(T... objs) {
        return arrayToIterator(objs);
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
     * {@link Boolean} version of {@link #arrayToIterator(Object[])}.
     *
     * @param array As described.
     * @return As described.
     */
    public static @NotNull Iterator<Boolean> arrayToIterator(final boolean @NotNull [] array) {
        final var retl = new ArrayList<Boolean>(array.length);
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

    /**
     * Reduce the source iterator to some value.
     *
     * <p>Reduction will be done from left to right. That is,
     *
     * <pre>{@code
     *   red(1, 2, 3, 4)
     * = red(red(1, 2), 3, 4)
     * = red(red(red(1, 2), 3), 4)
     *
     * }</pre>
     *
     * @param sourceIterator As described.
     * @param reducer The reducing function.
     * @param initialValue The initial value for reduction.
     * @return Reduced value.
     * @param <T> Type of input iterable.
     * @param <V> Type of reduced value.
     * @see <a href="https://docs.python.org/3/library/functools.html#functools.reduce">Python
     *     interface.</a>
     */
    public static <T, V> V reduce(
            Iterator<T> sourceIterator, BiFunction<T, V, V> reducer, V initialValue) {
        return new IteratorReducer<>(sourceIterator, reducer, initialValue).get();
    }

    /**
     * Convert one iterator to another.
     *
     * @param sourceIterator As described.
     * @param mapper As described.
     * @return As described.
     * @param <T> As described.
     * @param <V> As described.
     * @see <a href="https://docs.python.org/3/library/functions.html#map">Python interface</a>
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static <T, V> @NotNull Iterator<V> map(
            Iterator<T> sourceIterator, Function<T, V> mapper) {
        return new IteratorMapper<>(sourceIterator, mapper);
    }

    @Contract("_, _ -> new")
    public static <T> @NotNull Iterator<T> filter(
            Iterator<T> sourceIterator, Predicate<T> predicat) {
        return new IteratorFilterer<>(sourceIterator, predicat);
    }

    @Contract("_, _ -> new")
    public static <T> @NotNull Iterator<T> filterByAnother(
            Iterator<T> sourceIterator, Iterator<Boolean> predicat) {
        return new IteratorFiltererByAnother<>(sourceIterator, predicat);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static <V, T, U> @NotNull Iterator<V> combine(
            BiFunction<T, U, V> combinator, Iterator<T> tIterator, Iterator<U> uIterator) {
        return new IteratorCombinator<>(combinator, tIterator, uIterator);
    }

    public static <T> @NotNull Tuple2<Iterator<T>, Iterator<T>> duplicate(
            Iterator<T> sourceIterator) {
        var di = new IteratorDuplicator<>(sourceIterator);
        return Tuple.of(di.iterator1, di.iterator2);
    }

    public static <T> @NotNull Iterator<Tuple2<T, Integer>> rank(
            Iterator<T> sourceIterator, int start) {
        return new IteratorRanker<>(sourceIterator, start);
    }
}
