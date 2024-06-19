package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/** A base implementation for all rolling hash algorithms. */
abstract class RollingHashBase extends RollingBase<Long> implements RollingHashInterface {
    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     */
    protected RollingHashBase(byte @NotNull [] string, int k, int skipFirst) {
        super(string, k, skipFirst);
    }

    /** By setting type into unboxed form could increase speed. */
    protected long currentValueUnboxed;

    @Override
    public long nextLong() {
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
