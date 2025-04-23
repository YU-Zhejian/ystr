package org.labw.libinterval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.List;

class IntervalAlgorithmsTest {

    @Test
    void overlaps() {
        var si1 = new SimpleInterval(10, 20);
        var si2 = new SimpleInterval(20, 30);
        assertFalse(IntervalAlgorithms.overlaps(si1, si2));
        assertFalse(IntervalAlgorithms.overlaps(si2, si1));

        si2 = new SimpleInterval(15, 30);
        assertTrue(IntervalAlgorithms.overlaps(si1, si2));
        assertTrue(IntervalAlgorithms.overlaps(si2, si1));

        si2 = new SimpleInterval(21, 30);
        assertFalse(IntervalAlgorithms.overlaps(si1, si2));
        assertFalse(IntervalAlgorithms.overlaps(si2, si1));

        si2 = new SimpleInterval(10, 20);
        assertTrue(IntervalAlgorithms.overlaps(si1, si2));
        assertTrue(IntervalAlgorithms.overlaps(si2, si1));

        si2 = new SimpleInterval(10, 15);
        assertTrue(IntervalAlgorithms.overlaps(si1, si2));
        assertTrue(IntervalAlgorithms.overlaps(si2, si1));

        si2 = new SimpleInterval(15, 20);
        assertTrue(IntervalAlgorithms.overlaps(si1, si2));
        assertTrue(IntervalAlgorithms.overlaps(si2, si1));

        si2 = new SimpleInterval(12, 15);
        assertTrue(IntervalAlgorithms.overlaps(si1, si2));
        assertTrue(IntervalAlgorithms.overlaps(si2, si1));
    }

    @Test
    void contains() {
        var si1 = new SimpleInterval(10, 20);
        assertTrue(IntervalAlgorithms.contains(si1, si1));
        var si2 = new SimpleInterval(5, 8);
        assertFalse(IntervalAlgorithms.contains(si1, si2));
        assertFalse(IntervalAlgorithms.contains(si2, si1));

        si2 = new SimpleInterval(10, 15);
        assertTrue(IntervalAlgorithms.contains(si1, si2));
        assertFalse(IntervalAlgorithms.contains(si2, si1));

        si2 = new SimpleInterval(15, 20);
        assertTrue(IntervalAlgorithms.contains(si1, si2));
        assertFalse(IntervalAlgorithms.contains(si2, si1));

        si2 = new SimpleInterval(12, 15);
        assertTrue(IntervalAlgorithms.contains(si1, si2));
        assertFalse(IntervalAlgorithms.contains(si2, si1));

        si2 = new SimpleInterval(21, 25);
        assertFalse(IntervalAlgorithms.contains(si1, si2));
        assertFalse(IntervalAlgorithms.contains(si2, si1));
    }

    @Test
    void mergeIntervals() {
        assertIterableEquals(List.of(), IntervalAlgorithms.mergeIntervals(List.of()));
        assertIterableEquals(
                List.of(SimpleInterval.of(1, 4)),
                IntervalAlgorithms.mergeIntervals(List.of(SimpleInterval.of(1, 4))));
        assertIterableEquals(
                List.of(SimpleInterval.of(1, 4), SimpleInterval.of(9, 10)),
                IntervalAlgorithms.mergeIntervals(
                        List.of(SimpleInterval.of(1, 4), SimpleInterval.of(9, 10))));
        assertIterableEquals(
                List.of(SimpleInterval.of(1, 4), SimpleInterval.of(9, 10)),
                IntervalAlgorithms.mergeIntervals(List.of(
                        SimpleInterval.of(1, 3),
                        SimpleInterval.of(2, 4),
                        SimpleInterval.of(9, 10))));
        assertIterableEquals(
                List.of(SimpleInterval.of(1, 4), SimpleInterval.of(9, 10)),
                IntervalAlgorithms.mergeIntervals(List.of(
                        SimpleInterval.of(2, 4),
                        SimpleInterval.of(1, 3),
                        SimpleInterval.of(9, 10))));
        assertIterableEquals(
                List.of(SimpleInterval.of(1, 4), SimpleInterval.of(9, 10)),
                IntervalAlgorithms.mergeIntervals(List.of(
                        SimpleInterval.of(1, 4),
                        SimpleInterval.of(2, 3),
                        SimpleInterval.of(9, 10))));
        assertIterableEquals(
                List.of(SimpleInterval.of(1, 10)),
                IntervalAlgorithms.mergeIntervals(List.of(
                        SimpleInterval.of(1, 4),
                        SimpleInterval.of(2, 9),
                        SimpleInterval.of(9, 10))));
    }

    @Test
    void overlappingLength() {
        assertEquals(
                IntervalAlgorithms.overlappingLength(
                        SimpleInterval.of(1, 4), SimpleInterval.of(1, 4)),
                3);
        assertEquals(
                IntervalAlgorithms.overlappingLength(
                        SimpleInterval.of(1, 4), SimpleInterval.of(1, 10)),
                3);
        assertEquals(
                IntervalAlgorithms.overlappingLength(
                        SimpleInterval.of(1, 10), SimpleInterval.of(1, 4)),
                3);
        assertEquals(
                IntervalAlgorithms.overlappingLength(
                        SimpleInterval.of(1, 4), SimpleInterval.of(4, 10)),
                0);
        assertEquals(
                IntervalAlgorithms.overlappingLength(
                        SimpleInterval.of(4, 10), SimpleInterval.of(1, 4)),
                0);

        assertEquals(
                IntervalAlgorithms.overlappingLength(
                        SimpleInterval.of(1, 5), SimpleInterval.of(4, 10)),
                1);
        assertEquals(
                IntervalAlgorithms.overlappingLength(
                        SimpleInterval.of(4, 10), SimpleInterval.of(1, 5)),
                1);

        assertEquals(
                IntervalAlgorithms.overlappingLength(
                        SimpleInterval.of(1, 3), SimpleInterval.of(4, 10)),
                -1);
        assertEquals(
                IntervalAlgorithms.overlappingLength(
                        SimpleInterval.of(4, 10), SimpleInterval.of(1, 3)),
                -1);
    }

    @Test
    void positiveOverlappingLength() {
        assertEquals(
                IntervalAlgorithms.positiveOverlappingLength(
                        SimpleInterval.of(1, 4), SimpleInterval.of(1, 4)),
                3);
        assertEquals(
                IntervalAlgorithms.positiveOverlappingLength(
                        SimpleInterval.of(1, 4), SimpleInterval.of(1, 10)),
                3);
        assertEquals(
                IntervalAlgorithms.positiveOverlappingLength(
                        SimpleInterval.of(1, 10), SimpleInterval.of(1, 4)),
                3);
        assertEquals(
                IntervalAlgorithms.positiveOverlappingLength(
                        SimpleInterval.of(1, 4), SimpleInterval.of(4, 10)),
                0);
        assertEquals(
                IntervalAlgorithms.positiveOverlappingLength(
                        SimpleInterval.of(4, 10), SimpleInterval.of(1, 4)),
                0);

        assertEquals(
                IntervalAlgorithms.positiveOverlappingLength(
                        SimpleInterval.of(1, 5), SimpleInterval.of(4, 10)),
                1);
        assertEquals(
                IntervalAlgorithms.positiveOverlappingLength(
                        SimpleInterval.of(4, 10), SimpleInterval.of(1, 5)),
                1);

        assertEquals(
                IntervalAlgorithms.positiveOverlappingLength(
                        SimpleInterval.of(1, 3), SimpleInterval.of(4, 10)),
                0);
        assertEquals(
                IntervalAlgorithms.positiveOverlappingLength(
                        SimpleInterval.of(4, 10), SimpleInterval.of(1, 3)),
                0);
    }
}
