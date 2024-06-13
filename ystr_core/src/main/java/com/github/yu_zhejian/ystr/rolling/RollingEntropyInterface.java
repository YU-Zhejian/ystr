package com.github.yu_zhejian.ystr.rolling;

import java.util.Iterator;

/** Double-Precision Rolling Entropy for Exclusion of Repetitive Regions. */
public interface RollingEntropyInterface extends Iterator<Double> {
    double nextUnboxed();
}
