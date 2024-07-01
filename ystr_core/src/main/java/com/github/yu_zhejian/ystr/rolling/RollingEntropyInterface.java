package com.github.yu_zhejian.ystr.rolling;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;

/** Double-Precision Rolling Entropy for Exclusion of Repetitive Regions. */
public interface RollingEntropyInterface extends DoubleIterator, RollingInterface<Double> {}
