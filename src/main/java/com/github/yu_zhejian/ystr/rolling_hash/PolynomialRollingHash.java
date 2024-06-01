package com.github.yu_zhejian.ystr.rolling_hash;

import com.github.yu_zhejian.ystr.StrUtils;

import org.jetbrains.annotations.NotNull;

/** Canonical rolling hash algorithm for Rabin-Karp string matching algorithm. */
public final class PolynomialRollingHash extends RollingHashBase<Long> {

    /** Default {@code p}. */
    public static final long POLYNOMIAL_ROLLING_HASH_P = 37;
    /** Default {@code m}. */
    public static final long POLYNOMIAL_ROLLING_HASH_M = 1000000007;

    private final long m;
    private final long p;

    private long pow;

    public PolynomialRollingHash(byte @NotNull [] string, int k, int start) {
        this(string, k, start, POLYNOMIAL_ROLLING_HASH_M, POLYNOMIAL_ROLLING_HASH_P);
    }

    public PolynomialRollingHash(byte @NotNull [] string, int k) {
        this(string, k, 0, POLYNOMIAL_ROLLING_HASH_M, POLYNOMIAL_ROLLING_HASH_P);
    }

    public PolynomialRollingHash(byte @NotNull [] string, int k, int start, long m, long p) {
        super(string, k, start);
        if (m <= 0 || p <= 0) {
            throw new IllegalArgumentException("m and p must be positive. Actual: %d, %d".formatted(m, p));
        }
        this.m = m;
        this.p = p;
        initCurrentHash();
    }

    @Override
    protected void initCurrentHash() {
        currentHash = 0L;
        for (int i = 0; i < k; i++) {
            currentHash = Long.remainderUnsigned(currentHash * p + string[i + start], m);
        }
        for (int i = 0; i < k-1; i++) {
            pow = Long.remainderUnsigned(p * pow, m);
        }
    }

    @Override
    protected void updateCurrentHashToNextState() {
        var i = curPos - 1;
        var seqi = string[i];
        var seqk = string[i + k];
        currentHash = Long.remainderUnsigned(currentHash -  Long.remainderUnsigned((pow * seqi), m), m);
        currentHash = Long.remainderUnsigned(Long.remainderUnsigned(currentHash * p, m) + seqk, m);
    }
}
