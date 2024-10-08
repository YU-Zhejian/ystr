package com.github.yu_zhejian.ystr.rolling;

import com.github.yu_zhejian.ystr.utils.StrUtils;

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
 * <p>This implementation mimics Robert Sedgewick's Las Vegas Implementation. It is slower but does
 * not report false-positives due to hash collision.
 *
 * <p><b>References</b>
 *
 * <ol>
 *   <li>Karp, Richard M. and Michael O. Rabin. "Efficient Randomized Pattern-Matching Algorithms."
 *       <i>IBM J. Res. Dev.</i> 31 (1987): 249-260. <a
 *       href="https://doi.org/10.1147/rd.312.0249">DOI</a>
 *   <li><a href="https://algs4.cs.princeton.edu/53substring/RabinKarp.java">Robert Sedgewick's
 *       Implementation</a>
 * </ol>
 */
public final class PolynomialRollingHash extends RollingHashBase {

    /** Default {@link #m}. Same as Robert Sedgewick's Implementation. */
    public static final long DEFAULT_POLYNOMIAL_ROLLING_HASH_M = 997;
    /** Some large prime number to prevent overflow. */
    private final long m;
    /** Some small prime number. */
    private final long p;
    /** Is {@code Math.pow(p, k - 1)}. */
    private long pow;

    /** Default constructor that is identical to Robert Sedgewick's implementation. */
    public PolynomialRollingHash() {
        this(DEFAULT_POLYNOMIAL_ROLLING_HASH_M, StrUtils.ALPHABET_SIZE);
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
     * @param m Some prime number.
     * @param p Usually alphabet size.
     * @throws IllegalArgumentException On negative {@code m} or {@code p}.
     */
    public PolynomialRollingHash(final long m, final long p) {
        if (m <= 0 || p <= 0) {
            throw new IllegalArgumentException(
                    "m and p must be positive. Actual: %d, %d".formatted(m, p));
        }
        this.m = m;
        this.p = p;
    }

    @Override
    protected void initCurrentValue() {
        ensureAttached();
        currentValueUnboxed = 0L;
        for (int i = 0; i < k; i++) {
            currentValueUnboxed = (currentValueUnboxed * p + string[i + skipFirst]) % m;
        }
        pow = 1L;
        for (int i = 0; i < k - 1; i++) {
            pow = (p * pow) % m;
        }
    }

    @Override
    protected void updateCurrentValueToNextState() {
        ensureAttached();
        final var i = curPos - 1;
        final var seqi = string[i];
        final var seqk = string[i + k];
        // + m in the following line prevents the genesis of negative values.
        currentValueUnboxed = (currentValueUnboxed + m - (pow * seqi) % m) % m;
        currentValueUnboxed = ((currentValueUnboxed * p) % m + seqk) % m;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "PolynomialRollingHash{m=0x%s, p=%d, curPos = %d}"
                .formatted(Long.toHexString(m), p, curPos);
    }
}
