package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.yu_zhejian.ystr.rolling_hash.PolynomialRollingHash;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class PolynomialRollingHashTest {
    long getPolynomialRollinghPolynomialRollingHash1(@NotNull String input, int n, int k) {
        return new PolynomialRollingHash(input.getBytes(StandardCharsets.UTF_8), k, n).next();
    }

    long getPolynomialRollinghPolynomialRollingHash2(@NotNull String input, int n, int k) {
        var nth = new PolynomialRollingHash(input.getBytes(StandardCharsets.UTF_8), k);
        while (n > 0) {
            nth.next();
            n--;
        }
        return nth.next();
    }

    @Test
    void testEqualHashAtDifferentPosition() {
        var str1 = "NNNAGCTNNN";
        var str2 = "AGCTNN";

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
