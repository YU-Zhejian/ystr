package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.yu_zhejian.ystr.utils.RngUtils;
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
    void memsetBB(){
        final int ARR_LEN = 1<<20;
        var buff = new byte[ARR_LEN];
        for (int i = 0; i < 100; i++) {
            var ranges = RngUtils.generateRandomCoordinates(1000, 0, ARR_LEN);
            for (final var range: ranges) {
                Arrays.fill(buff, range.firstInt(), range.secondInt(), (byte)1);
                StrLibc.memsetBB(buff, (byte)1, range.firstInt(), range.secondInt() - range.firstInt());
            }
        }
    }
}
