package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

/** A base implementation for all rolling entropy algorithms. */
abstract class RollingEntropyBase extends RollingBase<Double> implements RollingEntropyInterface {
    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     */
    protected RollingEntropyBase(byte @NotNull [] string, int k, int skipFirst) {
        super(string, k, skipFirst);
    }
}
