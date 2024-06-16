package com.github.yu_zhejian.ystr.rolling;

import static org.junit.jupiter.api.Assertions.*;

import com.github.yu_zhejian.ystr.IterUtils;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

class IsPolyARollingPredicateTest {
    @Test
    void testPredicate() {
        assertEquals(256, IsPolyARollingPredicate.PREDICATE.length);
        for (int i = 0; i < 256; i++) {
            if (i == 'A' || i == 'a' || i == 'T' || i == 't' || i == 'u' || i == 'U') {
                assertTrue(IsPolyARollingPredicate.PREDICATE[i]);
            } else {
                assertFalse(IsPolyARollingPredicate.PREDICATE[i]);
            }
        }
        assertEquals(
                3,
                new IsPolyARollingPredicate("AAAA".getBytes(StandardCharsets.US_ASCII), 4, 0)
                        .getNumAThreshold());
    }

    @Test
    void testStrangeEncoding() {
        byte[] string = new byte[] {78, 0, 78, 78, 65, 'T', 'U', 'u', -2, 78, -17, 78};
        var predicate = new IsPolyARollingPredicate(string, 4, 0, 3);
        assertIterableEquals(
                List.of(false, false, false, true, true, true, false, false, false),
                IterUtils.collect(predicate));
    }

    @Test
    void testNormal() {
        byte[] string = "NNNNAAAANNNN".getBytes(StandardCharsets.US_ASCII);
        var predicate = new IsPolyARollingPredicate(string, 4, 0, 3);
        assertIterableEquals(
                List.of(false, false, false, true, true, true, false, false, false),
                IterUtils.collect(predicate));
    }
}
