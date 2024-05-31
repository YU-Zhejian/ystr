package com.github.yu_zhejian.ystr.rolling_hash;

import com.github.yu_zhejian.ystr.StrUtils;

import org.jetbrains.annotations.NotNull;

/** Canonical rolling hash algorithm for Rabin-Karp string matching algorithm. */
public final class PolynomialRollingHash extends RollingHashBase<Long> {

    /** Default {@code p}. */
    public static final int POLYNOMIAL_ROLLING_HASH_P = 37;
    /** Default {@code m}. */
    public static final int POLYNOMIAL_ROLLING_HASH_M = 1000000007;

    private long pow;

    public PolynomialRollingHash(byte @NotNull [] string, int k, int start) {
        super(string, k, start);
    }

    public PolynomialRollingHash(byte @NotNull [] string, int k) {
        super(string, k);
    }

    @Override
    protected void initCurrentHash() {
        currentHash = 0L;
        pow = StrUtils.pow((long) POLYNOMIAL_ROLLING_HASH_P, k - 1) % POLYNOMIAL_ROLLING_HASH_M;
        ;
        for (int i = 0; i < k; i++) {
            currentHash = (currentHash * POLYNOMIAL_ROLLING_HASH_P + string[i + start])
                    % POLYNOMIAL_ROLLING_HASH_M;
        }
    }

    @Override
    protected void updateCurrentHashToNextState() {
        var i = curPos - 1;
        var seqi = string[i];
        var seqk = string[i + k];
        currentHash = ((currentHash - pow * seqi) * POLYNOMIAL_ROLLING_HASH_P + seqk)
                % POLYNOMIAL_ROLLING_HASH_M;
    }
}
