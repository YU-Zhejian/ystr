package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.checksum.CRC32;
import com.github.yu_zhejian.ystr.checksum.ChecksumInterface;
import com.github.yu_zhejian.ystr.rolling_hash.NtHash;
import com.github.yu_zhejian.ystr.rolling_hash.PolynomialRollingHash;
import com.github.yu_zhejian.ystr.rolling_hash.PrecomputedNtHash;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * String hashing algorithms that hash a string. Also includes checksum algorithms like CRC.
 *
 * <p>Notice, all functions in this implementation are one-shot hashing algorithms that takes a
 * string represented in {@code byte[]} and return some {@link Long}.
 */
public final class StrHash {
    private static final int JAVA_LATIN1_HASH_P = 31;

    private StrHash() {}

    /**
     * Rabin-Karp Polynomial Rolling Hash algorithm. See {@link PolynomialRollingHash} for
     * implementation.
     *
     * @param string As described.
     * @return As described.
     */
    public static long polynomialRollingHash(final byte @NotNull [] string) {
        return new PolynomialRollingHash(string, string.length, 0).next();
    }

    /**
     * The hashing function from {@link String#hashCode()} in JDK17.
     *
     * @param string As described.
     * @return As described.
     */
    @Contract(pure = true)
    public static long javaLatin1Hash(final byte @NotNull [] string) {
        long h = 0;
        for (final byte v : string) {
            h = JAVA_LATIN1_HASH_P * h + (v & 0xff);
        }
        return h;
    }

    /**
     * ntHash for the entire DNA string. See {@link NtHash} for more details.
     *
     * @param string As described.
     * @return As described.
     */
    public static long ntHash(final byte[] string) {
        return new PrecomputedNtHash(string, string.length, 0).next();
    }

    /**
     * See {@link StrEncoder#simpleNucleotideEncoder(byte)}
     *
     * @param string As described.
     * @return As described.
     */
    @Contract(pure = true)
    public static long simpleKmerHashing(final byte @NotNull [] string) {
        long retv = 0;
        for (final var nt : string) {
            retv = retv << 4;
            retv += StrEncoder.simpleNucleotideEncoder(nt);
        }
        return retv;
    }

    public static long crc32(final byte @NotNull [] string) {
        return ChecksumInterface.fastChecksum(CRC32::new, string);
    }
}
