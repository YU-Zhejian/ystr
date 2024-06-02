package com.github.yu_zhejian.ystr.rolling_hash;

import java.util.Iterator;

/**
 * Interface for Rabin-Karp rolling hash algorithm. The resulting hash should be 64-bit unsigned
 * integers as {@link Long}.
 */
public interface RollingHashInterface extends Iterator<Long> {}
