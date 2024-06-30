package com.github.yu_zhejian.ystr.hash;

/**
 * 32-bit Fowler-Noll-Vo 1 hash, alternate implementation, using bitwise operations.
 *
 * @see FNV1Base
 */
public final class BitwiseFNV1a32 extends FNV1Base {
    private int hval;
    /** Default constructor. */
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
