package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

/**
 * Pre-computed {@link NtHash} implementation.
 *
 * <p>This is a direct port of C++ version of ntHash 1.0.0.
 */
public final class PrecomputedNtHash extends PrecomputedBidirectionalNtHash {
    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param start As described.
     */
    public PrecomputedNtHash(final byte @NotNull [] string, final int k, final int start) {
        super(string, k, start);
    }

    @Override
    protected void initCurrentValue() {
        super.initCurrentValue();
        currentValueUnboxed = Long.compareUnsigned(fwdHash, revHash) < 0 ? fwdHash : revHash;
    }

    @Override
    protected void updateCurrentValueToNextState() {
        super.updateCurrentValueToNextState();
        // This line redone Long.compareUnsigned
        currentValueUnboxed =
                (fwdHash + Long.MIN_VALUE < revHash + Long.MIN_VALUE) ? fwdHash : revHash;
    }
}
