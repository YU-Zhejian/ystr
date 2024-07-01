package com.github.yu_zhejian.ystr.rolling;

import java.util.NoSuchElementException;

/** A base implementation for all rolling predicate algorithms. */
abstract class RollingPredicateBase extends RollingBase<Boolean>
        implements RollingPredicateInterface {
    /** By setting type into unboxed form could increase speed. */
    protected boolean currentValueUnboxed;

    @Override
    public boolean nextBoolean() {
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
