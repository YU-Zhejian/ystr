package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class IterUtilsTest {

    @Test
    void dedup() {
        assertIteratorEquals(IterUtils.of(), IterUtils.dedup(IterUtils.of()));
        assertIteratorEquals(IterUtils.of(1), IterUtils.dedup(IterUtils.of(1)));
        assertIteratorEquals(IterUtils.of(1), IterUtils.dedup(IterUtils.of(1, 1)));
        assertIteratorEquals(IterUtils.of(1, 2), IterUtils.dedup(IterUtils.of(1, 1, 2)));
        assertIteratorEquals(IterUtils.of(1, 2), IterUtils.dedup(IterUtils.of(1, 1, 2, 2, 2)));
        assertIteratorEquals(
                IterUtils.of(1, 2, 3), IterUtils.dedup(IterUtils.of(1, 1, 2, 2, 2, 3)));
        assertIteratorEquals(
                IterUtils.of(1, 2, 3), IterUtils.dedup(IterUtils.of(1, 1, 2, 2, 2, 3)));
        assertIteratorEquals(
                IterUtils.of(Tuple.of(1, 2), Tuple.of(2, 3), Tuple.of(3, 6), Tuple.of(4, 7)),
                IterUtils.dedup(
                        IterUtils.of(
                                Tuple.of(1, 2),
                                Tuple.of(1, 3),
                                Tuple.of(2, 3),
                                Tuple.of(2, 4),
                                Tuple.of(2, 5),
                                Tuple.of(3, 6),
                                Tuple.of(4, 7)),
                        Tuple2::_1));
    }

    @Test
    void reduce() {
        assertEquals(10, IterUtils.reduce(IterUtils.of(1, 2, 3, 4), Integer::sum, 0));
        assertEquals(
                false,
                IterUtils.reduce(IterUtils.of(true, true, true, false), Boolean::logicalAnd, true));
        assertEquals(
                -24,
                IterUtils.reduce(IterUtils.of(1, 2, 3, 4), (Integer i, Integer j) -> i * j, -1));
        assertIterableEquals(
                List.of(1, 2, 3, 4),
                IterUtils.reduce(
                        IterUtils.of(1, 2, 3, 4),
                        (Integer o, List<Integer> l) -> {
                            l.add(o);
                            return l;
                        },
                        new ArrayList<>()));
    }

    void assertIteratorEquals(Iterator<?> expected, Iterator<?> actual) {
        assertIterableEquals(IterUtils.iterable(expected), IterUtils.iterable(actual));
    }

    @Test
    void map() {

        assertIteratorEquals(IterUtils.of(), IterUtils.map(IterUtils.of(), (Integer i) -> i * 2));
        assertIteratorEquals(
                IterUtils.of(2, 4, 6, 8),
                IterUtils.map(IterUtils.of(1, 2, 3, 4), (Integer i) -> i * 2));
        assertIteratorEquals(
                IterUtils.arrayToIterator(new boolean[] {false, false, true, true}),
                IterUtils.map(
                        IterUtils.arrayToIterator(new int[] {1, 2, 3, 4}), (Integer i) -> i > 2));
    }

    @Test
    void filter() {
        assertIteratorEquals(
                IterUtils.of(), IterUtils.filter(IterUtils.of(), (Integer i) -> i > 2));
        assertIteratorEquals(
                IterUtils.of(3, 4),
                IterUtils.filter(IterUtils.of(1, 2, 3, 4, 1), (Integer i) -> i > 2));
        assertIteratorEquals(
                IterUtils.of(true, true),
                IterUtils.filter(IterUtils.of(false, false, true, true), (Boolean b) -> b));
    }

}
