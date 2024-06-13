package com.github.yu_zhejian.ystr.rolling;

import java.util.Iterator;

/** Rolling Predicate for Exclusion of Repetitive Regions. */
public interface RollingPredicateInterface extends Iterator<Boolean> {
    boolean nextUnboxed();
}
