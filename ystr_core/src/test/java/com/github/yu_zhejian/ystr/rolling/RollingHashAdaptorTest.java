package com.github.yu_zhejian.ystr.rolling;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import com.github.yu_zhejian.ystr.hash.CRC32Hash;
import com.github.yu_zhejian.ystr.hash.HashInterface;
import com.github.yu_zhejian.ystr.utils.IterUtils;

import io.vavr.collection.List;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class RollingHashAdaptorTest {
    @Test
    void test() {
        var string = "AAAGCTCGA".getBytes(StandardCharsets.US_ASCII);
        var ans = List.of(
                0x66a031a7L,
                0x8fc39492L,
                0xdef4f70dL,
                0x3dc6cb7cL,
                0xbc9f62f1L,
                0xa718978bL,
                0x337e424fL);
        assertIterableEquals(
                ans,
                IterUtils.collect(new RollingHashAdaptor(string, 3, 0, HashInterface.CRC32_HASH)));
        assertIterableEquals(
                ans,
                IterUtils.collect(
                        RollingHashAdaptor.supply(HashInterface.CRC32_HASH).apply(string, 3, 0)));
        assertIterableEquals(
                ans,
                IterUtils.collect(RollingHashAdaptor.supply(CRC32Hash::new).apply(string, 3, 0)));
    }
}
