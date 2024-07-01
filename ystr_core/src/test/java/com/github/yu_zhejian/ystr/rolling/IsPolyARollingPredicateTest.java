package com.github.yu_zhejian.ystr.rolling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.yu_zhejian.ystr.utils.IterUtils;
import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

class IsPolyARollingPredicateTest {
    @Test
    void testPredicate() {
        assertEquals(256, IsPolyARollingPredicate.PREDICATE.length);
        for (int i = 0; i < StrUtils.ALPHABET_SIZE; i++) {
            if (i == 'A' || i == 'a' || i == 'T' || i == 't' || i == 'u' || i == 'U') {
                assertTrue(IsPolyARollingPredicate.PREDICATE[i]);
            } else {
                assertFalse(IsPolyARollingPredicate.PREDICATE[i]);
            }
        }
    }

    @Test
    void testStrangeEncoding() {
        byte[] string = new byte[] {78, 0, 78, 78, 65, 'T', 'U', 'u', -2, 78, -17, 78};
        var predicate = new IsPolyARollingPredicate(3);
        predicate.attach(string, 4);
        assertIterableEquals(
                List.of(false, false, false, true, true, true, false, false, false),
                IterUtils.collect(predicate));
        predicate.detach();
    }

    @Test
    void testNormal() {
        byte[] string = "NNNNAAAANNNN".getBytes(StandardCharsets.US_ASCII);
        var predicate = new IsPolyARollingPredicate(3);
        predicate.attach(string, 4);
        assertIterableEquals(
                List.of(false, false, false, true, true, true, false, false, false),
                IterUtils.collect(predicate));
        predicate.detach();
    }
}
