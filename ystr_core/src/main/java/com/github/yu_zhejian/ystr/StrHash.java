package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.checksum.CRC32;
import com.github.yu_zhejian.ystr.checksum.ChecksumInterface;
import com.github.yu_zhejian.ystr.rolling.NtHash;
import com.github.yu_zhejian.ystr.rolling.PolynomialRollingHash;
import com.github.yu_zhejian.ystr.rolling.PrecomputedNtHash;

import org.jetbrains.annotations.NotNull;

/**
 * String hashing algorithms that hash a string. Also includes checksum algorithms like CRC.
 *
 * <p>Notice, all functions in this implementation are one-shot hashing algorithms that takes a
 * string represented in {@code byte[]} and return some {@link Long}.
 */
public final class StrHash {

    private StrHash() {}

    /**
     * Rabin-Karp Polynomial Rolling Hash algorithm.
     *
     * @see PolynomialRollingHash
     * @param string As described.
     * @return As described.
     */
    public static long polynomialRollingHash(final byte @NotNull [] string) {
        return new PolynomialRollingHash(string, string.length, 0).nextLong();
    }

    /**
     * Pre-computed ntHash version 1 for the entire DNA string.
     *
     * @see NtHash
     * @see PrecomputedNtHash
     * @param string As described.
     * @return As described.
     */
    public static long ntHash(final byte[] string) {
        return new PrecomputedNtHash(string, string.length, 0).nextLong();
    }

    /**
     * Canonical CRC32 checksum.
     *
     * @param string As described.
     * @return As described.
     * @see CRC32
     */
    public static long crc32(final byte @NotNull [] string) {
        return ChecksumInterface.convenientChecksum(new CRC32(), string, 0, string.length);
    }
}
