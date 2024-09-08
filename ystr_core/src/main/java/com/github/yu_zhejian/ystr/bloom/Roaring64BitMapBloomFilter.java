package com.github.yu_zhejian.ystr.bloom;

import it.unimi.dsi.fastutil.objects.Object2LongFunction;

import org.roaringbitmap.longlong.Roaring64Bitmap;

import java.util.List;

public final class Roaring64BitMapBloomFilter<T> implements BloomFilterInterface<T> {
    private final List<Object2LongFunction<T>> hashers;
    private final Roaring64Bitmap rbm;

    public Roaring64BitMapBloomFilter(final List<Object2LongFunction<T>> hashers) {
        this.hashers = hashers;
        rbm = new Roaring64Bitmap();
    }

    @Override
    public void add(final T element) {
        for (final var hasher : hashers) {
            rbm.addLong(hasher.applyAsLong(element));
        }
    }

    @Override
    public boolean mightContains(final T element) {
        for (final var hasher : hashers) {
            if (!rbm.contains(hasher.applyAsLong(element))) {
                return false;
            }
        }
        return true;
    }
}
