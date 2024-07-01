package com.github.yu_zhejian.ystr.rolling;

/** Pre-computed {@link NtHash} implementation. */
public final class PrecomputedNtHash extends PrecomputedBidirectionalNtHash {

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
