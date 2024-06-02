package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

class StrUtilsTest {

    @Test
    void pow() {
        assertEquals(2, StrUtils.pow(2, 1));
        assertEquals(1, StrUtils.pow(1, 1));
        assertEquals(4, StrUtils.pow(2, 2));
        assertEquals(8, StrUtils.pow(2, 3));
        assertEquals(9, StrUtils.pow(3, 2));
        assertEquals(1, StrUtils.pow(3, 0));
        assertEquals(0, StrUtils.pow(0, 3));
        assertEquals(1, StrUtils.pow(0, 0));

        assertEquals(2L, StrUtils.pow(2L, 1));
        assertEquals(1L, StrUtils.pow(1L, 1));
        assertEquals(4L, StrUtils.pow(2L, 2));
        assertEquals(8L, StrUtils.pow(2L, 3));
        assertEquals(9L, StrUtils.pow(3L, 2));
        assertEquals(1L, StrUtils.pow(3L, 0));
        assertEquals(0L, StrUtils.pow(0L, 3));
        assertEquals(1L, StrUtils.pow(0L, 0));
    }

    @Test
    void dedup() {
        assertIterableEquals(
                List.of(), StrUtils.iterable(StrUtils.dedup(StrUtils.arrayToIterator(new int[0]))));
        assertIterableEquals(
                List.of(1),
                StrUtils.iterable(StrUtils.dedup(StrUtils.arrayToIterator(new int[] {1}))));
        assertIterableEquals(
                List.of(1),
                StrUtils.iterable(StrUtils.dedup(StrUtils.arrayToIterator(new int[] {1, 1}))));
        assertIterableEquals(
                List.of(1, 2),
                StrUtils.iterable(StrUtils.dedup(StrUtils.arrayToIterator(new int[] {1, 1, 2}))));
        assertIterableEquals(
                List.of(1, 2),
                StrUtils.iterable(
                        StrUtils.dedup(StrUtils.arrayToIterator(new int[] {1, 1, 2, 2, 2}))));
        assertIterableEquals(
                List.of(1, 2, 3),
                StrUtils.iterable(
                        StrUtils.dedup(StrUtils.arrayToIterator(new int[] {1, 1, 2, 2, 2, 3}))));
        assertIterableEquals(
                StrUtils.iterable(StrUtils.arrayToIterator(new Integer[] {1, 2, 3, null})),
                StrUtils.iterable(StrUtils.dedup(
                        StrUtils.arrayToIterator(new Integer[] {1, 1, 2, 2, 2, 3, null}))));
    }
}
