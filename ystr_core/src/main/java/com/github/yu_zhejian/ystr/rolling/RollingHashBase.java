package com.github.yu_zhejian.ystr.rolling;

import java.util.NoSuchElementException;

/** A base implementation for all rolling hash algorithms. */
abstract class RollingHashBase extends RollingBase<Long> implements RollingHashInterface {

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
