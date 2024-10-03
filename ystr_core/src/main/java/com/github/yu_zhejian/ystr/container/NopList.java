package com.github.yu_zhejian.ystr.container;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A list that discards all input elements.
 *
 * @param <V> As described.
 */
public final class NopList<V> extends NopCollection<V> implements List<V> {
    private static final int size = 0;

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends V> c) {
        return false;
    }

    @Contract(pure = true)
    @Override
    public @Nullable V get(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Contract(pure = true)
    @Override
    public @Nullable V set(int index, V element) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void add(int index, V element) {
        if (index == size) {
            return;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public @Nullable V remove(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int indexOf(Object o) {
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return -1;
    }

    private static class NopListIterator<T> extends NopIterator<T> implements ListIterator<T> {

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public T previous() {
            throw new NoSuchElementException();
        }

        @Override
        public int nextIndex() {
            return 0;
        }

        @Override
        public int previousIndex() {
            return -1;
        }

        @Override
        public void remove() {
            // Do nothing!
        }

        @Override
        public void set(T v) {
            // Do nothing!
        }

        @Override
        public void add(T v) {
            // Do nothing!
        }
    }

    @Override
    public @NotNull ListIterator<V> listIterator() {
        return new NopListIterator<>();
    }

    @Override
    public @NotNull ListIterator<V> listIterator(int index) {
        if (index == size) {
            return listIterator();
        }
        throw new IndexOutOfBoundsException();
    }

    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable List<V> subList(int fromIndex, int toIndex) {
        if (fromIndex == size && toIndex == size) {
            return List.of();
        }
        throw new IndexOutOfBoundsException();
    }
}
