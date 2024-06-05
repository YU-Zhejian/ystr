package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.iter_utils.IteratorDuplicationRemover;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
    public static int pow(final int p, final int q) {
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
    public static long pow(final long p, final int q) {
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
    public static void ensureStartEndValid(final int start, final int end) {
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
    public static void ensureStartEndValid(final int start, final int end, final int strLen) {
        ensureStartEndValid(start, end);
        if (end > strLen) {
            throw new IllegalArgumentException(
                    "end must be less than strLen. Actual: %d vs %d".formatted(end, strLen));
        }
    }

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

    /**
     * Similar function as is implemented in C standard libraries.
     *
     * <p>Implemented with the help of TONGYI Lingma.
     *
     * @return As described.
     */
    public static int strcmp(final byte @NotNull [] array1, final byte @NotNull [] array2) {
        final int minLength = Math.min(array1.length, array2.length);
        for (int i = 0; i < minLength; i++) {
            if (!Objects.equals(array1[i], array2[i])) {
                // If bytes are not equal, return the difference (this mimics the behavior of
                // strcmp)
                return Byte.compare(array1[i], array2[i]);
            }
        }
        // If we didn't find any differences in the common prefix, the shorter array is
        // lexicographically less
        return Integer.compare(array1.length, array2.length);
    }
}
