package com.github.yu_zhejian.ystr;

import com.github.yu_zhejian.ystr.rolling.NtHash;
import com.github.yu_zhejian.ystr.rolling.PrecomputedNtHash;

/**
 * String hashing algorithms that hash a string. Also includes checksum algorithms like CRC.
 *
 * <p>Notice, all functions in this implementation are one-shot hashing algorithms that takes a
 * string represented in {@code byte[]} and return some {@link Long}.
 */
public final class StrHash {

    private StrHash() {}

    /**
     * Pre-computed ntHash version 1 for the entire DNA string.
     *
     * @see NtHash
     * @see PrecomputedNtHash
     * @param string As described.
     * @return As described.
     */
    public static long ntHash(final byte[] string) {
        var hash = new PrecomputedNtHash();
        hash.attach(string, string.length);
        var retv = hash.nextLong();
        hash.detach();
        return retv;
    }
}
