package org.labw.libinterval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StrandUtilsTest {
    @Test
    void strandRepr() {
        assertEquals(".", StrandUtils.strandRepr(null));
        assertEquals("+", StrandUtils.strandRepr(true));
        assertEquals("-", StrandUtils.strandRepr(false));
        assertEquals(".", StrandUtils.strandRepr(StrandUtils.STRAND_UNKNOWN));
        assertEquals("+", StrandUtils.strandRepr(StrandUtils.STRAND_POSITIVE));
        assertEquals("-", StrandUtils.strandRepr(StrandUtils.STRAND_NEGATIVE));
    }

    @Test
    void strandStrToInt() {
        assertEquals(StrandUtils.STRAND_POSITIVE, StrandUtils.strandStrToInt("+"));
        assertEquals(StrandUtils.STRAND_NEGATIVE, StrandUtils.strandStrToInt("-"));
        assertEquals(StrandUtils.STRAND_UNKNOWN, StrandUtils.strandStrToInt("."));
        assertEquals(StrandUtils.STRAND_UNKNOWN, StrandUtils.strandStrToInt("x"));
    }

    @Test
    void strandCmp() {
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_NEGATIVE,
                StrandUtils.STRAND_NEGATIVE,
                StrandCmpPolicy.ON_SAME_STRAND_ONLY));
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_POSITIVE,
                StrandUtils.STRAND_POSITIVE,
                StrandCmpPolicy.ON_SAME_STRAND_ONLY));
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_UNKNOWN,
                StrandUtils.STRAND_UNKNOWN,
                StrandCmpPolicy.ON_SAME_STRAND_ONLY));
        assertFalse(StrandUtils.strandCmp(
                StrandUtils.STRAND_NEGATIVE,
                StrandUtils.STRAND_POSITIVE,
                StrandCmpPolicy.ON_SAME_STRAND_ONLY));
        assertFalse(StrandUtils.strandCmp(
                StrandUtils.STRAND_UNKNOWN,
                StrandUtils.STRAND_POSITIVE,
                StrandCmpPolicy.ON_SAME_STRAND_ONLY));
        assertFalse(StrandUtils.strandCmp(
                StrandUtils.STRAND_UNKNOWN,
                StrandUtils.STRAND_NEGATIVE,
                StrandCmpPolicy.ON_SAME_STRAND_ONLY));

        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_NEGATIVE,
                StrandUtils.STRAND_NEGATIVE,
                StrandCmpPolicy.NOT_ON_OPPOSITE_STRAND));
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_POSITIVE,
                StrandUtils.STRAND_POSITIVE,
                StrandCmpPolicy.NOT_ON_OPPOSITE_STRAND));
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_UNKNOWN,
                StrandUtils.STRAND_UNKNOWN,
                StrandCmpPolicy.NOT_ON_OPPOSITE_STRAND));
        assertFalse(StrandUtils.strandCmp(
                StrandUtils.STRAND_NEGATIVE,
                StrandUtils.STRAND_POSITIVE,
                StrandCmpPolicy.NOT_ON_OPPOSITE_STRAND));
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_UNKNOWN,
                StrandUtils.STRAND_POSITIVE,
                StrandCmpPolicy.NOT_ON_OPPOSITE_STRAND));
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_UNKNOWN,
                StrandUtils.STRAND_NEGATIVE,
                StrandCmpPolicy.NOT_ON_OPPOSITE_STRAND));

        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_NEGATIVE, StrandUtils.STRAND_NEGATIVE, StrandCmpPolicy.IGNORED));
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_POSITIVE, StrandUtils.STRAND_POSITIVE, StrandCmpPolicy.IGNORED));
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_UNKNOWN, StrandUtils.STRAND_UNKNOWN, StrandCmpPolicy.IGNORED));
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_NEGATIVE, StrandUtils.STRAND_POSITIVE, StrandCmpPolicy.IGNORED));
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_UNKNOWN, StrandUtils.STRAND_POSITIVE, StrandCmpPolicy.IGNORED));
        assertTrue(StrandUtils.strandCmp(
                StrandUtils.STRAND_UNKNOWN, StrandUtils.STRAND_NEGATIVE, StrandCmpPolicy.IGNORED));
    }
}
