package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

/**
 * The ntHash rolling hash algorithm designed for genomic sequences.
 *
 * <p>This {@link NtHash} implementation should yield similar results as version 1.0.0 of ntHash
 * original C++ implementation (referred to as "original implementation" for short). It has the
 * following characteristics:
 *
 * <ol>
 *   <li>All nucleotides except {@code AGCTUagctu} would be considered {@code N}.
 *   <li>The nucleotide {@code T} and {@code U} will <b>NOT</b> be distinguished. The original
 *       implementation will render {@code U} as {@code N}.
 *   <li>Upper- and lower-cased bases will <b>NOT</b> be distinguished.
 *   <li>The {@link #next()} method will return the canonical ntHash, which is the smaller one
 *       between {@link #getFwdHash()} and {@link #getRevHash()}. This is the default in ntHash1 and
 *       ntHash2 {@code <= 2.3.0}, but is changed in ntHash2 2.3.0.
 *   <li>This implementation do not rely on a pre-computed hash table, so it amy be slower than
 *       those with one ({@link PrecomputedNtHash}).
 *   <li>This implementation would only generate 1 hash value for one k-mer. Use
 *       {@link NtHashBase#multiHash} to generate multiple hashes.
 *   <li>This implementation will <b>NOT</b> skip k-mers with {@code N} inside. However, the
 *       official {@code ntHashIterator} would skip them.
 * </ol>
 *
 * <p><b>References</b>
 *
 * <ul>
 *   <li>Hamid Mohamadi, Justin Chu, Benjamin P. Vandervalk, Inanc Birol, ntHash: recursive
 *       nucleotide hashing, <i>Bioinformatics</i>, Volume 32, Issue 22, November 2016, Pages
 *       3492–3494, <a href="https://doi.org/10.1093/bioinformatics/btw397">DOI</a>
 *   <li><a href="https://github.com/luizirber/nthash">Rust implementation</a> at commit
 *       {@code ee653d33}, which is based on <a
 *       href="https://github.com/bcgsc/ntHash/releases/tag/v1.0.4">1.0.4</a> version of the
 *       original C++ implementation.
 *   <li>ntHash <a
 *       href="https://github.com/bcgsc/ntHash/releases/download/1.0.0/ntHash-1.0.0.tar.gz">1.0.0</a>,
 *       the reference implementation.
 * </ul>
 *
 * <p><b>Compatibility Note</b>
 *
 * <p>The hash values produced by version 2 of ntHash differ from version 1.
 */
public final class NtHash extends NtHashBase {
    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param start As described.
     */
    public NtHash(final byte @NotNull [] string, final int k, final int start) {
        super(string, k, start);
    }

    @Override
    protected void initCurrentValue() {
        fwdHash = 0;
        for (var i = 0; i < k; i++) {
            fwdHash ^= Long.rotateLeft(seedTableGet(string[i + start]), k - 1 - i);
        }
        revHash = 0;
        for (var i = 0; i < k; i++) {
            revHash ^= Long.rotateLeft(complSeedTableGet(string[i + start]), i);
        }
        currentValue = Long.compareUnsigned(fwdHash, revHash) < 0 ? fwdHash : revHash;
    }

    @Override
    protected void updateCurrentValueToNextState() {
        final var i = curPos - 1;
        final var seqi = string[i];
        final var seqk = string[i + k];
        fwdHash = Long.rotateLeft(fwdHash, 1)
                ^ Long.rotateLeft(seedTableGet(seqi), k)
                ^ seedTableGet(seqk);
        revHash = Long.rotateRight(revHash, 1)
                ^ Long.rotateRight(complSeedTableGet(seqi), 1)
                ^ Long.rotateLeft(complSeedTableGet(seqk), k - 1);
        currentValue = Long.compareUnsigned(fwdHash, revHash) < 0 ? fwdHash : revHash;
    }

    /**
     * {@link #seedTableGet(byte)} for reverse-complementary base.
     *
     * @param b As described.
     * @return As described.
     */
    private long complSeedTableGet(final byte b) {
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

    /**
     * Normalize the incoming base and return its seed.
     *
     * @param b As described.
     * @return As described.
     */
    private long seedTableGet(final byte b) {
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
}
