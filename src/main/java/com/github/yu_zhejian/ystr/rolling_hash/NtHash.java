package com.github.yu_zhejian.ystr.rolling_hash;

import org.jetbrains.annotations.NotNull;

/**
 * The ntHash rolling hash algorithm designed for genomic sequences.
 *
 * <p><b>Implementation</b>
 *
 * <p>Re implemented from <a href="https://github.com/luizirber/nthash">Rust Implementation of
 * ntHash</a> at commit {@code ee653d33c485e95565d0b082082e885f6d397062}, which is based on <a
 * href="https://github.com/bcgsc/ntHash/releases/tag/v1.0.4">Original C++ implementation at 1.0.4
 * branch</a>. This implementation has the following characteristics:
 *
 * <ol>
 *   <li>All nucleotides except {@code AGCTUagctu} would be considered {@code N}.
 *   <li>The nucleotide {@code T} and {@code U} will <b>NOT</b> be distinguished.
 *   <li>Upper- and lower-cased bases will <b>NOT</b> be distinguished.
 * </ol>
 *
 * <p><b>References</b>
 *
 * <p>Original paper: Hamid Mohamadi, Justin Chu, Benjamin P. Vandervalk, Inanc Birol, ntHash:
 * recursive nucleotide hashing, <i>Bioinformatics</i>, Volume 32, Issue 22, November 2016, Pages
 * 3492–3494, <a href="https://doi.org/10.1093/bioinformatics/btw397">DOI</a>
 *
 * <p><b>API Note</b>
 *
 * <p>The {@link #next()} would by default get canonical ntHash, which is the smaller one between
 * {@link #getFwdHash()} and {@link #getRevHash()}.
 */
public final class NtHash extends RollingHashBase<Long> {
    private static final long SEED_A = 0x3c8bfbb395c60474L;
    private static final long SEED_C = 0x3193c18562a02b4cL;
    private static final long SEED_G = 0x20323ed082572324L;
    private static final long SEED_T = 0x295549f54be24456L;
    private static final long SEED_N = 0x0000000000000000L;

    private long fwdHash;
    private long revHash;

    @Override
    protected void initCurrentHash() {
        fwdHash = 0;
        for (var i = 0; i < k; i++) {
            fwdHash ^= Long.rotateLeft(seedTableGet(string[i + start]), k - 1 - i);
        }
        revHash = 0;
        for (var i = 0; i < k; i++) {
            revHash ^= Long.rotateLeft(complSeedTableGet(string[i + start]), i);
        }
        currentHash = Long.compareUnsigned(fwdHash, revHash) < 0 ? fwdHash : revHash;
    }

    public NtHash(final byte @NotNull [] string, int k, int start) {
        super(string, k, start);
        initCurrentHash();
    }

    public NtHash(final byte @NotNull [] string, int k) {
        this(string, k, 0);
    }

    private long complSeedTableGet(byte b) {
        switch (b) {
            case 'A', 'a' -> {
                return SEED_T;
            }
            case 'G', 'g' -> {
                return SEED_C;
            }
            case 'C', 'c' -> {
                return SEED_G;
            }
            case 'T', 't', 'U', 'u' -> {
                return SEED_A;
            }
            default -> {
                return SEED_N;
            }
        }
    }

    private long seedTableGet(byte b) {
        switch (b) {
            case 'A', 'a' -> {
                return SEED_A;
            }
            case 'G', 'g' -> {
                return SEED_G;
            }
            case 'C', 'c' -> {
                return SEED_C;
            }
            case 'T', 't', 'U', 'u' -> {
                return SEED_T;
            }
            default -> {
                return SEED_N;
            }
        }
    }

    @Override
    protected void updateCurrentHashToNextState() {
        var i = curPos - 1;
        var seqi = string[i];
        var seqk = string[i + k];
        fwdHash = Long.rotateLeft(fwdHash, 1)
                ^ Long.rotateLeft(seedTableGet(seqi), k)
                ^ seedTableGet(seqk);
        revHash = Long.rotateRight(revHash, 1)
                ^ Long.rotateRight(complSeedTableGet(seqi), 1)
                ^ Long.rotateLeft(complSeedTableGet(seqk), k - 1);
        currentHash = Long.compareUnsigned(fwdHash, revHash) < 0 ? fwdHash : revHash;
    }

    public long getFwdHash() {
        return fwdHash;
    }

    public long getRevHash() {
        return revHash;
    }
}
