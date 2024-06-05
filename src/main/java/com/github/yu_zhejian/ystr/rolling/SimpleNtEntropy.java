package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

/** The simplest entropy used to exclude k-mers with bases except {@code AGCTUagctu}. */
public final class SimpleNtEntropy extends RollingEntropyBase {
    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param start As described.
     */
    private SimpleNtEntropy(byte @NotNull [] string, int k, int start) {
        super(string, k, start);
    }

    @Override
    protected void initCurrentValue() {}

    @Override
    protected void updateCurrentValueToNextState() {}
}
