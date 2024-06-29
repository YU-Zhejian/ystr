package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class StrLibcTest {

    @Test
    void strcmp() {
        assertEquals(
                0,
                StrLibc.strcmp(
                        "AGCT".getBytes(StandardCharsets.UTF_8),
                        "AGCT".getBytes(StandardCharsets.UTF_8)));
        assertTrue(StrLibc.strcmp(
                        "AGCTA".getBytes(StandardCharsets.UTF_8),
                        "AGCT".getBytes(StandardCharsets.UTF_8))
                > 0);
        assertTrue(StrLibc.strcmp(
                        "AGCTA".getBytes(StandardCharsets.UTF_8),
                        "AGCTT".getBytes(StandardCharsets.UTF_8))
                < 0);
    }
}
