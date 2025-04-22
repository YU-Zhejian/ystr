package com.github.yu_zhejian.ystr.rolling;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.yu_zhejian.ystr.hash.HashConstants;
import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RollingHashBaseTest {

    static long at(@NotNull RollingHashInterface rollingHash, byte[] string, int k, int skipFirst) {
        rollingHash.attach(string, k, skipFirst);
        var retv = rollingHash.nextLong();
        rollingHash.detach();
        return retv;
    }

    static long at2(
            @NotNull RollingHashInterface rollingHash, byte[] string, int k, int skipFirst) {
        rollingHash.attach(string, k, 0);
        var nLeft = skipFirst;
        while (nLeft > 0) {
            rollingHash.nextLong();
            nLeft--;
        }
        var retv = rollingHash.nextLong();
        rollingHash.detach();
        return retv;
    }

    @Test
    void testEqualHashAtDifferentPosition() {
        var str1 = "NNNAGCTNNN".getBytes(StandardCharsets.UTF_8);
        var str2 = "AGCTNN".getBytes(StandardCharsets.UTF_8);
        final var hashConstants = new HashConstants();
        for (var hasher : List.of(
                new PrecomputedNtHash(),
                new NtHash(),
                new PolynomialRollingHash(),
                new PolynomialRollingHash(
                        PolynomialRollingHash.longRandomPrime(), StrUtils.ALPHABET_SIZE),
                new RollingHashAdaptor(hashConstants.CRC32_HASH))) {
            System.out.println(hasher);
            assertEquals(
                    RollingHashBaseTest.at(hasher, str1, 4, 3),
                    RollingHashBaseTest.at(hasher, str2, 4, 0));
            assertEquals(
                    RollingHashBaseTest.at(hasher, str1, 4, 4),
                    RollingHashBaseTest.at(hasher, str2, 4, 1));
            assertEquals(
                    RollingHashBaseTest.at(hasher, str1, 3, 4),
                    RollingHashBaseTest.at(hasher, str2, 3, 1));
            assertEquals(
                    RollingHashBaseTest.at(hasher, str1, 4, 3),
                    RollingHashBaseTest.at2(hasher, str2, 4, 0));
            assertEquals(
                    RollingHashBaseTest.at(hasher, str1, 4, 4),
                    RollingHashBaseTest.at2(hasher, str2, 4, 1));
            assertEquals(
                    RollingHashBaseTest.at(hasher, str1, 3, 4),
                    RollingHashBaseTest.at2(hasher, str2, 3, 1));
        }
    }
}
