package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
}
