package com.github.yu_zhejian.ystr.rolling;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.yu_zhejian.ystr.StrHash;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class PolynomialRollingHashTest {
    long getPolynomialRollinghPolynomialRollingHash1(@NotNull String input, int n, int k) {
        return new PolynomialRollingHash(input.getBytes(StandardCharsets.UTF_8), k, n)
                .nextLong();
    }

    long getPolynomialRollinghPolynomialRollingHash2(@NotNull String input, int n, int k) {
        var nLeft = n;
        var nth = new PolynomialRollingHash(input.getBytes(StandardCharsets.UTF_8), k, 0);
        while (nLeft > 0) {
            nth.nextLong();
            nLeft--;
        }
        return nth.nextLong();
    }

    @Test
    void testEqualHashAtDifferentPosition() {
        var str1 = "NNNAGCTNNN";
        var str2 = "AGCTNN";
        var str3 = "AGCTAGCT";
        assertEquals(
                getPolynomialRollinghPolynomialRollingHash2(str3, 0, 4),
                getPolynomialRollinghPolynomialRollingHash2(str3, 4, 4));
        assertEquals(
                getPolynomialRollinghPolynomialRollingHash1(str3, 0, 4),
                getPolynomialRollinghPolynomialRollingHash2(str3, 4, 4));

        assertEquals(
                getPolynomialRollinghPolynomialRollingHash2(str1, 3, 4),
                getPolynomialRollinghPolynomialRollingHash2(str2, 0, 4));
        assertEquals(
                getPolynomialRollinghPolynomialRollingHash2(str1, 4, 4),
                getPolynomialRollinghPolynomialRollingHash2(str2, 1, 4));
        assertEquals(
                getPolynomialRollinghPolynomialRollingHash2(str1, 4, 3),
                getPolynomialRollinghPolynomialRollingHash2(str2, 1, 3));

        assertEquals(
                StrHash.polynomialRollingHash("AGCT".getBytes(StandardCharsets.UTF_8)),
                getPolynomialRollinghPolynomialRollingHash2(str2, 0, 4));
        assertEquals(
                StrHash.polynomialRollingHash("AGCT".getBytes(StandardCharsets.UTF_8)),
                getPolynomialRollinghPolynomialRollingHash2(str3, 4, 4));
        assertEquals(
                StrHash.polynomialRollingHash("GCTN".getBytes(StandardCharsets.UTF_8)),
                getPolynomialRollinghPolynomialRollingHash2(str2, 1, 4));
        assertEquals(
                StrHash.polynomialRollingHash("GCT".getBytes(StandardCharsets.UTF_8)),
                getPolynomialRollinghPolynomialRollingHash2(str2, 1, 3));

        // Different skipping strategy
        assertEquals(
                getPolynomialRollinghPolynomialRollingHash1(str1, 3, 4),
                getPolynomialRollinghPolynomialRollingHash2(str2, 0, 4));
        assertEquals(
                getPolynomialRollinghPolynomialRollingHash1(str1, 4, 4),
                getPolynomialRollinghPolynomialRollingHash2(str2, 1, 4));
        assertEquals(
                getPolynomialRollinghPolynomialRollingHash1(str1, 4, 3),
                getPolynomialRollinghPolynomialRollingHash2(str2, 1, 3));
    }
}
