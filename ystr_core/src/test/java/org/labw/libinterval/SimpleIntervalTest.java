package org.labw.libinterval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.util.List;

class SimpleIntervalTest {

    @Test
    void getOffsets() {
        var si = new SimpleInterval(10, 20);
        assertIterableEquals(List.of(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L), si.getOffsets(1));
        assertIterableEquals(List.of(0L, 2L, 4L, 6L, 8L), si.getOffsets(2));
        si = new SimpleInterval(10, 21);
        assertIterableEquals(List.of(0L, 2L, 4L, 6L, 8L, 10L), si.getOffsets(2));
    }

    @Test
    void getPositions() {
        var si = new SimpleInterval(10, 20);
        assertIterableEquals(
                List.of(10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L), si.getPositions(1));
        assertIterableEquals(List.of(10L, 12L, 14L, 16L, 18L), si.getPositions(2));
        si = new SimpleInterval(10, 21);
        assertIterableEquals(List.of(10L, 12L, 14L, 16L, 18L, 20L), si.getPositions(2));
    }

    @Test
    void equals() {
        var si = new SimpleInterval(10, 20);
        var si2 = new SimpleInterval(10, 20);
        var si3 = new SimpleInterval(10, 11);
        var si4 = new SimpleInterval(0, 10);
        assertEquals(si, si2);
        assertEquals(si2, si);
        assertNotEquals(si, si3);
        assertNotEquals(si3, si);
        assertNotEquals(si, si4);
        assertNotEquals(si4, si);
        assertNotEquals(null, si);
        assertNotEquals(new Object(), si2);
    }

    @Test
    void assertHashCode() {
        var si = new SimpleInterval(10, 20);
        var si2 = new SimpleInterval(10, 20);
        var si3 = new SimpleInterval(10, 11);
        var si4 = new SimpleInterval(0, 10);
        assertEquals(si.hashCode(), si2.hashCode());
        assertEquals(si2.hashCode(), si.hashCode());
        assertNotEquals(si.hashCode(), si3.hashCode());
        assertNotEquals(si3.hashCode(), si.hashCode());
        assertNotEquals(si.hashCode(), si4.hashCode());
        assertNotEquals(si4.hashCode(), si.hashCode());
    }

    @Test
    void testToStr() {
        var si = new SimpleInterval(10, 20);
        assertEquals(si.simplifiedToString(), "10-20");
        assertEquals("SimpleInterval:10-20(10)", si.toString());
        assertEquals(10, si.getLength());
        assertEquals(si, SimpleInterval.lex(si.simplifiedToString()));
        assertThrows(IllegalArgumentException.class, () -> SimpleInterval.of(20, 10));
        assertThrows(IllegalArgumentException.class, () -> SimpleInterval.of(-1, 10));
        assertEquals(si, SimpleInterval.ofAuto(20, 10));
        assertEquals(si, SimpleInterval.ofAuto(10, 20));
    }

    @Test
    void offsetBy() {
        var si = new SimpleInterval(10, 20);
        assertEquals(new SimpleInterval(11, 21), si.offsetBy(1));
        assertEquals(new SimpleInterval(9, 19), si.offsetBy(-1));
        assertEquals(si, si.offsetBy(0));
        assertEquals(new SimpleInterval(0, 10), si.offsetBy(-10));
        assertThrows(IllegalArgumentException.class, () -> si.offsetBy(-11));
    }
}
