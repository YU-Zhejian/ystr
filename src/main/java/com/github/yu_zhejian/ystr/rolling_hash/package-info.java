/**
 * Rabin-Karp Rolling Hash Algorithm.
 *
 * <p><b>Introduction</b>
 *
 * <p>A rolling hash algorithm would firstly compute the hash for the initial window. When
 * {@link java.util.Iterator#next()} is called, the window will slide towards the end of the string
 * and the hash would be updated.
 *
 * <p><b>Properties of the Algorithm</b>
 *
 * <p>An important property of this algorithm is that no matter where rolling hash was started, for
 * same window the hashing result would be the same. That is, start point would <b>NOT</b> affect
 * hashing results for the same k-mer at different positions. For example,
 *
 * <pre>
 *     long getNthNtHash1(@NotNull String input, int n, int k) {
 *         return new NtHash(input.getBytes(StandardCharsets.UTF_8), k, n).next();
 *     }
 *
 *     var str1 = "NNNAGCTNNN";
 *     var str2 = "AGCTNN";
 *
 *     assertEquals(getNthNtHash1(str1, 3, 4), getNthNtHash1(str2, 0, 4));
 *     // Which should both be "AGCT"
 * </pre>
 *
 * <b>TODO</b>
 *
 * <ol>
 *   <li>Migrate the aaHash algorithm.
 *   <li>Check whether the current ntHash implementation matches its C++ versions.
 * </ol>
 */
package com.github.yu_zhejian.ystr.rolling_hash;
