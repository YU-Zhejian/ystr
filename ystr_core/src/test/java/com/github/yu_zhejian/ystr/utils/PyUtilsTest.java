package com.github.yu_zhejian.ystr.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

class PyUtilsTest {

    @Test
    void print() {
        try (var ss = new ByteArrayOutputStream()) {
            var pp = new PyUtils.PrintParams(" ", "\n", ss, true);
            PyUtils.print(pp, 1, 2, 3);
            assertEquals("1 2 3\n", pp.file().toString());
            PyUtils.print(pp, null, 5, 6);
            assertEquals("1 2 3\nnull 5 6\n", pp.file().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void range() {
        assertIterableEquals(List.of(1, 2, 3), PyUtils.range(1, 4, 1));
        assertIterableEquals(List.of(1, 2, 3), PyUtils.range(1, 4));
        assertIterableEquals(List.of(0, 1, 2, 3), PyUtils.range(4));
        assertIterableEquals(List.of(4, 3, 2, 1), PyUtils.range(4, 0, -1));
        assertIterableEquals(List.of(0, 1, 2), PyUtils.rangeAlong(List.of(1, 2, 3)));
    }
}
