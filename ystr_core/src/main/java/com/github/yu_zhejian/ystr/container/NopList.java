package com.github.yu_zhejian.ystr.container;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A list that discards all input elements.
 *
 * @param <V> As described.
 */
public class NopList<V> implements List<V> {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public @NotNull Iterator<V> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public V next() {
                throw new NoSuchElementException();
            }
        };
    }

    @Override
    public @NotNull Object @NotNull [] toArray() {
        return new Object[0];
    }

    @Override
    public @NotNull <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return a;
    }

    @Override
    public boolean add(V v) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends V> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends V> c) {
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        // Do nothing!
    }

    @Override
    public V get(int index) {
        return null;
    }

    @Override
    public V set(int index, V element) {
        return null;
    }

    @Override
    public void add(int index, V element) {
        // Do nothing!
    }

    @Override
    public V remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return -1;
    }

    @Override
    public @NotNull ListIterator<V> listIterator() {
        return new ListIterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public V next() {
                throw new NoSuchElementException();
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public V previous() {
                throw new NoSuchElementException();
            }

            @Override
            public int nextIndex() {
                return -1;
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
            public void set(V v) {
                // Do nothing!
            }

            @Override
            public void add(V v) {
                // Do nothing!
            }
        };
    }

    @Override
    public @NotNull ListIterator<V> listIterator(int index) {
        return new ListIterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public V next() {
                throw new NoSuchElementException();
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public V previous() {
                throw new NoSuchElementException();
            }

            @Override
            public int nextIndex() {
                return -1;
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
            public void set(V v) {
                // Do nothing!
            }

            @Override
            public void add(V v) {
                // Do nothing!
            }
        };
    }

    @Override
    public @NotNull List<V> subList(int fromIndex, int toIndex) {
        return List.of();
    }
}
