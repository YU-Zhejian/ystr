package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.rolling.NtHash;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/** String hashing algorithms that hash a string to some {@link Long}. */
public final class StrHash {
    private StrHash() {}

    /**
     * @param string As described.
     * @param start Start site of hashing (Inclusive).
     * @param end End site of hashing (exclusive).
     * @param p The Recommended value would be 37.
     * @param m The recommended value would be 1000000007.
     * @return As described.
     */
    public static long polynomialRollingHash(byte[] string, int start, int end, int p, int m) {
        long sum = 0;
        for (int i = 0; i < end - start; i++) {
            sum += string[start + i] * StrUtils.pow((long) p, i);
        }
        return sum % m;
    }

    /**
     * See {@link #polynomialRollingHash(byte[], int, int, int, int)}
     *
     * @param string As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    public static long polynomialRollingHash(byte[] string, int start, int end) {
        return polynomialRollingHash(string, start, end, 37, 1000000007);
    }

    /**
     * See {@link #polynomialRollingHash(byte[], int, int, int, int)}
     *
     * @param string As described.
     * @return As described.
     */
    public static long polynomialRollingHash(byte[] string) {
        return polynomialRollingHash(string, 0, string.length);
    }

    /**
     * From JDK 17.
     *
     * @param string As described.
     * @return As described.
     */
    @Contract(pure = true)
    public static long javaLatin1Hash(byte @NotNull [] string) {
        long h = 0;
        for (byte v : string) {
            h = 31 * h + (v & 0xff);
        }
        return h;
    }

    /**
     * ntHash for the entire DNA string. See {@link NtHash} for more details.
     *
     * @param string As described.
     * @return As described.
     */
    public static long ntHash(byte[] string) {
        return new NtHash(string, string.length).next();
    }

    /**
     * See {@link StrEncoder#simpleNucleotideEncoder(byte)}
     *
     * @param string As described.
     * @return As described.
     */
    @Contract(pure = true)
    public static long simpleKmerHashing(byte @NotNull [] string) {
        long retv = 0;
        for (var nt : string) {
            retv = retv << 4;
            retv += StrEncoder.simpleNucleotideEncoder(nt);
        }
        return retv;
    }
}
