package com.github.yu_zhejian.ystr.bloom;

public interface BloomFilterInterface<T> {
    void add(T element);

    boolean mightContains(T element);
}
