package com.github.yu_zhejian.ystr.hash;

public final class MultiplyFNV1a64 extends FNV1Base {
    private long hval;

    public MultiplyFNV1a64() {
        reset();
    }

    @Override
    public long getValue() {
        return hval;
    }

    @Override
    public void reset() {
        hval = FNV_INIT_64;
    }

    @Override
    public void update(int b) {
        hval ^= b;
        hval *= FNV_PRIME_64;
    }
}
