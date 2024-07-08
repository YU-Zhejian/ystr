package com.github.yu_zhejian.ystr.bloom;

import it.unimi.dsi.fastutil.objects.Object2LongFunction;

import org.roaringbitmap.longlong.Roaring64Bitmap;

import java.util.List;

public final class BloomFilter<T> {
    private final List<Object2LongFunction<T>> hashers;
    private final Roaring64Bitmap rbm;

    public BloomFilter(List<Object2LongFunction<T>> hashers) {
        this.hashers = hashers;
        rbm = new Roaring64Bitmap();
    }

    public void add(T element) {
        for (var hasher : hashers) {
            rbm.add(hasher.applyAsLong(element));
        }
    }
}
