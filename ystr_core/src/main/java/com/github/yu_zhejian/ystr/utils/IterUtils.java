package com.github.yu_zhejian.ystr.utils;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

/**
 * Various utility functions concerning {@link Iterator}, {@link Iterable} and
 * {@link java.util.Collections} to support other classes. Classes supporting {@link IterUtils}.
 *
 * <p>Warning, almost all generic methods inside this library could result in performance damage.
 * Use with care. For performance issues, prefer/implement methods that use FastUtils instead.
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
        final var result = new ArrayList<T>();
        if (list.isEmpty()) {
            return result;
        }
        var prev = list.get(0);
        result.add(prev);
        for (int i = 1; i < list.size(); i++) {
            final var current = list.get(i);
            if (!Objects.equals(prev, current)) {
                result.add(current);
                prev = current;
            }
        }
        return result;
    }

    /**
     * Optimized {@link #dedup(List)}
     *
     * @param list As described.
     * @return As described.
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public static @NotNull IntArrayList dedup(@NotNull IntArrayList list) {
        final var result = new IntArrayList();
        if (list.isEmpty()) {
            return result;
        }
        var prev = list.getInt(0);
        result.add(prev);
        for (int i = 1; i < list.size(); i++) {
            final var current = list.getInt(i);
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
    @SuppressWarnings("PMD.LooseCoupling")
    public static <T> @NotNull ObjectArrayList<T> collect(@NotNull Iterator<T> sourceIterator) {
        final var retl = new ObjectArrayList<T>();
        while (sourceIterator.hasNext()) {
            retl.add(sourceIterator.next());
        }
        return retl;
    }

    /**
     * Optimized {@link #collect(Iterator)}
     *
     * @param sourceIterator As described.
     * @return As described.
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public static @NotNull LongArrayList collect(@NotNull LongIterator sourceIterator) {
        final var retl = new LongArrayList();
        while (sourceIterator.hasNext()) {
            retl.add(sourceIterator.nextLong());
        }
        return retl;
    }

    /**
     * Optimized {@link #collect(Iterator)}
     *
     * @param sourceIterator As described.
     * @return As described.
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public static @NotNull DoubleArrayList collect(@NotNull DoubleIterator sourceIterator) {
        final var retl = new DoubleArrayList();
        while (sourceIterator.hasNext()) {
            retl.add(sourceIterator.nextDouble());
        }
        return retl;
    }

    /**
     * Optimized {@link #collect(Iterator)}
     *
     * @param sourceIterator As described.
     * @return As described.
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public static @NotNull BooleanArrayList collect(@NotNull BooleanIterator sourceIterator) {
        final var retl = new BooleanArrayList();
        while (sourceIterator.hasNext()) {
            retl.add(sourceIterator.nextBoolean());
        }
        return retl;
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
    @SuppressWarnings("PMD.LooseCoupling")
    public static <T> @NotNull IntArrayList where(
            @NotNull Iterator<T> sourceIterator, Predicate<T> predicate) {
        final var retl = new IntArrayList();
        var i = 0;
        while (sourceIterator.hasNext()) {
            if (predicate.test(sourceIterator.next())) {
                retl.add(i);
            }
            i++;
        }
        return retl;
    }

    /**
     * Optimized {@link #where(Iterator, Predicate)}
     *
     * @param list As described.
     * @param predicate As described.
     * @return As described.
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public static @NotNull IntArrayList where(
            @NotNull DoubleArrayList list, DoublePredicate predicate) {
        final var retl = new IntArrayList();
        for (var i = 0; i < list.size(); i++) {
            if (predicate.test(list.getDouble(i))) {
                retl.add(i);
            }
        }
        return retl;
    }

    /**
     * Split contents of an iterator into fixed-sized windows of {@link ObjectArrayList} of elements
     * inside. The last window will be return unfilled if {@code sourceIterator.length % windowSize
     * != 0}.
     *
     * <p>Warning, this method will impair performance. Avoid using when {@code T} is a primitive.
     *
     * @param sourceIterator As described.
     * @param windowSize As described.
     * @return As described.
     * @param <T> As described.
     */
    @Contract("_, _ -> new")
    public static <T> @NotNull Iterator<List<T>> window(
            @NotNull Iterator<T> sourceIterator, int windowSize) {
        return new IteratorWindowExtractor<>(sourceIterator, windowSize);
    }

    /**
     * Implemented with the help of TONGYI Lingma.
     *
     * @param <T> As described.
     */
    public static final class IteratorWindowExtractor<T> implements Iterator<List<T>> {
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
        public @NotNull ObjectArrayList<T> next() {
            if (!hasNextBatch) {
                throw new NoSuchElementException();
            }
            final var windowCopy = new ObjectArrayList<>(currentWindow);
            currentWindow.clear();
            populateCurrentWindow();
            return windowCopy;
        }
    }
}
