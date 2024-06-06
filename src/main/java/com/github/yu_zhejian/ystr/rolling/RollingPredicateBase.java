package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

/** A base implementation for all rolling predicate algorithms. */
abstract class RollingPredicateBase extends RollingBase<Boolean>
        implements RollingPredicateInterface {
    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param start As described.
     */
    protected RollingPredicateBase(byte @NotNull [] string, int k, int start) {
        super(string, k, start);
    }
}
