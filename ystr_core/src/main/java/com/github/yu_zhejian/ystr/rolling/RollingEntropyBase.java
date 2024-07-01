package com.github.yu_zhejian.ystr.rolling;

import java.util.NoSuchElementException;

/** A base implementation for all rolling entropy algorithms. */
abstract class RollingEntropyBase extends RollingBase<Double> implements RollingEntropyInterface {
    /** By setting type into unboxed form could increase speed. */
    protected double currentValueUnboxed;

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
