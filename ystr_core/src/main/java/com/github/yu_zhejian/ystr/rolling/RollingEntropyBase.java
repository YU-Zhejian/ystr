package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

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

    /** By setting type into unboxed form could increase speed. */
    protected double currentValueUnboxed;
    /** @deprecated Disabled ever since. Use {@link #currentValueUnboxed} instead. */
    @Deprecated
    protected final Double currentValue = null;

    /** @deprecated Unoptimized version; use {@link #nextUnboxed()} instead. */
    @Override
    @Deprecated
    public Double next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        if (curPos != skipFirst) {
            updateCurrentValueToNextState();
        }
        curPos++;
        return currentValueUnboxed;
    }

    public double nextUnboxed() {
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
