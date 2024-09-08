package com.github.yu_zhejian.ystr.bloom;

import it.unimi.dsi.fastutil.objects.Object2LongFunction;

import java.util.BitSet;
import java.util.List;

public final class BitSetBloomFilter<T> implements BloomFilterInterface<T> {
    private final List<Object2LongFunction<T>> hashers;
    private final BitSet bs;
    private final int mask;

    public BitSetBloomFilter(final List<Object2LongFunction<T>> hashers, final int nBits) {
        this.hashers = hashers;
        bs = new BitSet(1 << nBits);
        mask = (1 << nBits) - 1;
    }

    @Override
    public void add(final T element) {
        for (final var hasher : hashers) {
            bs.set(mask & (int) hasher.applyAsLong(element));
        }
    }

    @Override
    public boolean mightContains(final T element) {
        for (final var hasher : hashers) {
            if (!bs.get(mask & (int) hasher.applyAsLong(element))) {
                return false;
            }
        }
        return true;
    }
}
