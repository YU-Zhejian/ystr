package com.github.yu_zhejian.ystr.hash;

public final class BitwiseFNV1a32 extends FNV1Base {
    private int hval;

    public BitwiseFNV1a32() {
        reset();
    }

    @Override
    public long getValue() {
        return hval;
    }

    @Override
    public void reset() {
        hval = FNV_INIT_32;
    }

    @Override
    public void update(int b) {
        hval ^= b;
        hval += (hval << 1) + (hval << 4) + (hval << 7) + (hval << 8) + (hval << 24);
    }
}
