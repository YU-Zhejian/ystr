package com.github.yu_zhejian.ystr.rolling;

/** Pre-computed {@link NtHash} implementation. */
public final class PrecomputedNtHash extends PrecomputedBidirectionalNtHash {

    /**
     * Pre-computed ntHash version 1 for the entire DNA string.
     *
     * @param string As described.
     * @return As described.
     * @see NtHash
     * @see PrecomputedNtHash
     */
    public static long convenient(final byte[] string) {
        var hash = new PrecomputedNtHash();
        hash.attach(string, string.length);
        var retv = hash.nextLong();
        hash.detach();
        return retv;
    }

    @Override
    protected void initCurrentValue() {
        super.initCurrentValue();
        ensureAttached();
        currentValueUnboxed = Long.compareUnsigned(fwdHash, revHash) < 0 ? fwdHash : revHash;
    }

    @Override
    protected void updateCurrentValueToNextState() {
        super.updateCurrentValueToNextState();
        ensureAttached();
        // This line redone Long.compareUnsigned
        currentValueUnboxed =
                (fwdHash + Long.MIN_VALUE < revHash + Long.MIN_VALUE) ? fwdHash : revHash;
    }
}
