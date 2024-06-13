package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/** A base implementation for all rolling predicate algorithms. */
abstract class RollingPredicateBase extends RollingBase<Boolean>
        implements RollingPredicateInterface {
    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     */
    protected RollingPredicateBase(byte @NotNull [] string, int k, int skipFirst) {
        super(string, k, skipFirst);
    }

    /** By setting type into unboxed form could increase speed. */
    protected boolean currentValueUnboxed;
    /** @deprecated Disabled ever since. Use {@link #currentValueUnboxed} instead. */
    protected final Boolean currentValue = null;

    /** @deprecated Unoptimized version; use {@link #nextUnboxed()} instead. */
    @Override
    @Deprecated
    public Boolean next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        if (curPos != skipFirst) {
            updateCurrentValueToNextState();
        }
        curPos++;
        return currentValueUnboxed;
    }

    public boolean nextUnboxed() {
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
