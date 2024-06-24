package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/** A base implementation for all rolling entropy algorithms. */
abstract class RollingEntropyBase extends RollingBase<Double> implements RollingEntropyInterface {

    /** By setting type into unboxed form could increase speed. */
    protected double currentValueUnboxed;
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

    @Override
    public double nextDouble() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        if (curPos != skipFirst) {
            updateCurrentValueToNextState();
        }
        curPos++;
        return currentValueUnboxed;
    }
}
