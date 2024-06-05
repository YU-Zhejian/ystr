package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Random;

/**
 * Canonical rolling hash algorithm for Rabin-Karp string matching algorithm.
 *
 * <p><b>Implementation Details</b>
 *
 * <p>The basis of modular arithmetics:
 *
 * <ul>
 *   <li>{@code (a + b) % m = ((a % m) + (b % m)) % m}
 *   <li>{@code (a * b) % m = ((a % m) * (b % m)) % m}
 * </ul>
 *
 * This implementation mimics Robert Sedgewick's Las Vegas Implementation. It is slower but does not
 * report false-positives due to hash collision.
 *
 * <p><b>References</b>
 *
 * <ol>
 *   <li>Karp, Richard M. and Michael O. Rabin. “Efficient Randomized Pattern-Matching Algorithms.”
 *       <i>IBM J. Res. Dev.</i> 31 (1987): 249-260. <a
 *       href="https://doi.org/10.1147/rd.312.0249">DOI</a>
 *   <li><a href="https://algs4.cs.princeton.edu/53substring/RabinKarp.java">Robert Sedgewick's
 *       Implementation</a>
 * </ol>
 */
public final class PolynomialRollingHash extends RollingHashBase {
    /**
     * Default {@link #p}. Since we do not use negative part of {@link Byte}, the alphabet size is
     * 128.
     */
    public static final long DEFAULT_POLYNOMIAL_ROLLING_HASH_RADIX_P = 128;
    /** Default {@link #m}. Same as Robert Sedgewick's Implementation. */
    public static final long DEFAULT_POLYNOMIAL_ROLLING_HASH_M = 997;

    private final long m;
    private final long p;

    /** Is {@code Math.pow(p, k - 1)}. */
    private long pow;

    /**
     * Default constructor that is identical to Robert Sedgewick's implementation.
     *
     * @param start As described.
     * @param string As described.
     * @param k As described.
     */
    public PolynomialRollingHash(final byte @NotNull [] string, final int k, final int start) {
        this(
                string,
                k,
                start,
                DEFAULT_POLYNOMIAL_ROLLING_HASH_M,
                DEFAULT_POLYNOMIAL_ROLLING_HASH_RADIX_P);
    }

    /**
     * A random 31-bit prime.
     *
     * @return As described.
     */
    public static long longRandomPrime() {
        return BigInteger.probablePrime(31, new Random()).longValue();
    }

    /**
     * Default constructor.
     *
     * @param string As described.
     * @param k As described.
     * @param start As described.
     * @param m Some prime number.
     * @param p Usually alphabet size.
     */
    public PolynomialRollingHash(
            final byte @NotNull [] string,
            final int k,
            final int start,
            final long m,
            final long p) {
        super(string, k, start);
        if (m <= 0 || p <= 0) {
            throw new IllegalArgumentException(
                    "m and p must be positive. Actual: %d, %d".formatted(m, p));
        }
        this.m = m;
        this.p = p;
        initCurrentValue();
    }

    @Override
    protected void initCurrentValue() {
        currentValue = 0L;
        for (int i = 0; i < k; i++) {
            currentValue = (currentValue * p + string[i + start]) % m;
        }
        pow = 1L;
        for (int i = 0; i < k - 1; i++) {
            pow = (p * pow) % m;
        }
    }

    @Override
    protected void updateCurrentValueToNextState() {
        final var i = curPos - 1;
        final var seqi = string[i];
        final var seqk = string[i + k];
        // + m in the following line prevents the genesis of negative values.
        currentValue = (currentValue + m - (pow * seqi) % m) % m;
        currentValue = ((currentValue * p) % m + seqk) % m;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "PolynomialRollingHash{m=0x%s, p=%d, curPos = %d}"
                .formatted(Long.toHexString(m), p, curPos);
    }
}
