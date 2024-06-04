package com.github.yu_zhejian.ystr.rolling_hash;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/** Constants for {@link NtHash} and {@link PrecomputedNtHash}. */
public abstract class NtHashBase extends RollingHashBase {
    protected static final long SEED_A = 0x3c8bfbb395c60474L;
    protected static final long SEED_C = 0x3193c18562a02b4cL;
    protected static final long SEED_G = 0x20323ed082572324L;
    protected static final long SEED_T = 0x295549f54be24456L;
    protected static final long SEED_N = 0x0000000000000000L;
    protected static final int MULTI_SHIFT = 27;
    protected static final long MULTI_SEED = 0x90b45d39fb6da1faL;
    protected long fwdHash;
    protected long revHash;

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param start As described.
     */
    public NtHashBase(byte @NotNull [] string, int k, int start) {
        super(string, k, start);
        initCurrentHash();
    }

    /**
     * As described.
     *
     * @return As described.
     */
    public long getFwdHash() {
        return fwdHash;
    }

    /**
     * As described.
     *
     * @return As described.
     */
    public long getRevHash() {
        return revHash;
    }

    /**
     * Generate multiple hashes on one k-mer allowing utilization of Bloom filters. Copied from the
     * original implementation.
     *
     * @param m Number of hashes to generate.
     * @param k K-mer size.
     * @param currentHash Hash of the current K-mer.
     * @return As described.
     */
    @Contract(pure = true)
    public static long @NotNull [] multiHash(int m, int k, long currentHash) {
        var tVal = 0L;
        var retl = new long[m];
        retl[0] = currentHash;
        for (int i = 1; i < m; i++) {
            tVal = currentHash * (i ^ k * MULTI_SEED);
            tVal ^= tVal >>> MULTI_SHIFT;
            retl[i] = tVal;
        }
        return retl;
    }

    /**
     * Generate multiple hashes on one k-mer allowing utilization of Bloom filters.
     *
     * @param m Number of hashes to generate.
     * @return As described.
     */
    @Contract(pure = true)
    public long @NotNull [] multiHash(int m) {
        return multiHash(m, k, currentHash);
    }
}
