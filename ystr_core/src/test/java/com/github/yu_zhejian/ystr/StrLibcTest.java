package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class StrLibcTest {

    @Test
    void memcmp() {
        assertEquals(
                0,
                StrLibc.memcmp(
                        "AGCT".getBytes(StandardCharsets.UTF_8),
                        "AGCT".getBytes(StandardCharsets.UTF_8)));
        assertTrue(StrLibc.memcmp(
                        "AGCTA".getBytes(StandardCharsets.UTF_8),
                        "AGCT".getBytes(StandardCharsets.UTF_8))
                > 0);
        assertTrue(StrLibc.memcmp(
                        "AGCTA".getBytes(StandardCharsets.UTF_8),
                        "AGCTT".getBytes(StandardCharsets.UTF_8))
                < 0);
    }

    @Test
    void memset() {
        var buff = new byte[10];

        Arrays.fill(buff, (byte) 0);
        StrLibc.memset(buff, (byte) 1, 1, 0);
        assertArrayEquals(new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, buff);

        Arrays.fill(buff, (byte) 0);
        StrLibc.memset(buff, (byte) 1, 1, 1);
        assertArrayEquals(new byte[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, buff);

        Arrays.fill(buff, (byte) 0);
        StrLibc.memset(buff, (byte) 1, 1, 2);
        assertArrayEquals(new byte[] {0, 1, 1, 0, 0, 0, 0, 0, 0, 0}, buff);

        Arrays.fill(buff, (byte) 0);
        StrLibc.memset(buff, (byte) 1, 1, 3);
        assertArrayEquals(new byte[] {0, 1, 1, 1, 0, 0, 0, 0, 0, 0}, buff);

        Arrays.fill(buff, (byte) 0);
        StrLibc.memset(buff, (byte) 1, 1, 4);
        assertArrayEquals(new byte[] {0, 1, 1, 1, 1, 0, 0, 0, 0, 0}, buff);

        Arrays.fill(buff, (byte) 0);
        StrLibc.memset(buff, (byte) 1, 1, 5);
        assertArrayEquals(new byte[] {0, 1, 1, 1, 1, 1, 0, 0, 0, 0}, buff);

        Arrays.fill(buff, (byte) 0);
        StrLibc.memset(buff, (byte) 1, 5);
        assertArrayEquals(new byte[] {1, 1, 1, 1, 1, 0, 0, 0, 0, 0}, buff);

        Arrays.fill(buff, (byte) 0);
        StrLibc.memset(buff, (byte) 1);
        assertArrayEquals(new byte[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, buff);
    }
}
