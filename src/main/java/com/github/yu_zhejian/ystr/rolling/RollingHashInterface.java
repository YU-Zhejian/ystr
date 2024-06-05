package com.github.yu_zhejian.ystr.rolling;

import java.util.Iterator;

/**
 * Rabin-Karp-Compatible Rolling Hash Algorithm.
 *
 * <p><b>Introduction</b>
 *
 * <p>Represented by {@link com.github.yu_zhejian.ystr.rolling.RollingHashInterface}, a rolling hash
 * algorithm (aka., recursive hash algorithm) should be some {@link java.util.Iterator} of
 * {@link java.lang.Long} that would firstly compute the hash for the initial window. When
 * {@link java.util.Iterator#next()} is called, the window will slide towards the end of the string
 * and the hash would be updated.
 *
 * <p>Most algorithms generates 64-bit hashes. There are also algorithms that generates shorter
 * hashes.
 *
 * <p><b>Properties of the Algorithm</b>
 *
 * <p>An important property of this algorithm is that no matter where rolling hash was started, for
 * same window the hashing result would be the same. That is, start point would <b>NOT</b> affect
 * hashing results for the same k-mer at different positions. For example,
 *
 * <pre>{@code
 * long getNthNtHash1(@NotNull String input, int n, int k) {
 *     return new NtHash(input.getBytes(StandardCharsets.UTF_8), k, n).next();
 * }
 *
 * var str1 = "NNNAGCTNNN";
 * var str2 = "AGCTNN";
 *
 * assertEquals(getNthNtHash1(str1, 3, 4), getNthNtHash1(str2, 0, 4));
 * // Which should both be "AGCT"
 * }</pre>
 */
public interface RollingHashInterface extends Iterator<Long> {}
