package org.labw.libinterval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.labw.libinterval.itree.BruteForceIntervalTree;
import org.labw.libinterval.itree.IntervalHeap;
import org.labw.libinterval.itree.IntervalTree;
import org.labw.libinterval.itree.IntervalTreeInterface;
import org.labw.libinterval.itree.NullIntervalTree;

import java.util.ArrayList;
import java.util.Random;

/** TODO: Edge cases were not tested. */
class IntervalTreeInterfaceTest {
    static final int nTest = 1024;
    static final int bound = 100;

    static @NotNull SimpleInterval nextInterval() {
        var rdg = new Random();
        var possibleInt1 = Math.abs(rdg.nextInt(bound));
        var possibleInt2 = Math.abs(rdg.nextInt(bound));
        return SimpleInterval.of(
                Math.min(possibleInt1, possibleInt2), Math.max(possibleInt1, possibleInt2));
    }

    void testIntervalTreeImplementation(
            IntervalTreeInterface<SimpleInterval> intervalTree, int iSize) {
        var intervals = new ArrayList<SimpleInterval>();
        for (int i = 0; i < iSize; i++) {
            var currentInterval = nextInterval();
            intervalTree.add(currentInterval);
            intervals.add(currentInterval);
        }
        intervals.sort(null);
        intervalTree.index();
        assertEquals(intervalTree.getSize(), iSize);
        for (int i = 0; i < nTest; i++) {
            var lastInterval = nextInterval();
            var ovl = new ArrayList<>(intervalTree.overlap(lastInterval));
            ovl.sort(null);
            var ovlManual = new ArrayList<>(intervals.stream()
                    .filter((SimpleInterval interval) ->
                            IntervalAlgorithms.overlaps(interval, lastInterval))
                    .toList());
            ovlManual.sort(null);
            assertIterableEquals(ovl, ovlManual);
        }
        assertIterableEquals(intervalTree.getIntervals().stream().sorted().toList(), intervals);
    }

    @Test
    void testIntervalHeap() {
        testIntervalTreeImplementation(new IntervalHeap<>(), 1024);
        testIntervalTreeImplementation(new IntervalHeap<>(), 0);
    }

    @Test
    void testIntervalTree() {
        testIntervalTreeImplementation(new IntervalTree<>(), 1024);
        testIntervalTreeImplementation(new IntervalTree<>(), 0);
    }

    @Test
    void testBruteForceIntervalTree() {
        testIntervalTreeImplementation(new BruteForceIntervalTree<>(), 1024);
        testIntervalTreeImplementation(new BruteForceIntervalTree<>(), 0);
    }

    @Test
    void testNullIntervalTree() {
        testIntervalTreeImplementation(new NullIntervalTree<>(), 0);
    }
}
