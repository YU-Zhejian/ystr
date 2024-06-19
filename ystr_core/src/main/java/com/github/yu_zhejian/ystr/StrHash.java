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
     * Rabin-Karp Polynomial Rolling Hash algorithm. See {@link PolynomialRollingHash} for
     * implementation.
     *
     * @param string As described.
     * @return As described.
     */
    public static long polynomialRollingHash(final byte @NotNull [] string) {
        return new PolynomialRollingHash(string, string.length, 0).nextLong();
    }

    /**
     * ntHash for the entire DNA string. See {@link NtHash} for more details.
     *
     * @param string As described.
     * @return As described.
     */
    public static long ntHash(final byte[] string) {
        return new PrecomputedNtHash(string, string.length, 0).nextLong();
    }

    /**
     * Generate the CRC32 checksum for a string.
     *
     * <p><b>Implementation</b> Traditional CRC32 algorithm. This implementation was modified from
     * <a href="https://wiki.osdev.org/CRC32">here</a> with a pre-computed lookup table.
     *
     * <p>Note, this algorithm may give different results than {@link java.util.zip.CRC32}.
     *
     * <p>Note, this algorithm is a <b>CHECKSUM</b> algorithm instead of a <b>HASHING</b> algorithm.
     *
     * @param string As described.
     * @return As described.
     * @see <a href="https://introcs.cs.princeton.edu/java/61data/CRC32.java.html">Princeton
     *     University Implementyation of the CRC32 algorithm.</a>
     */
    public static long crc32(final byte @NotNull [] string) {
        return ChecksumInterface.fastChecksum(CRC32::new, string);
    }
}
