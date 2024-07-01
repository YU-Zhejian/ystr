package com.github.yu_zhejian.ystr.rolling;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import com.github.yu_zhejian.ystr.hash.CRC32Hash;
import com.github.yu_zhejian.ystr.hash.HashInterface;
import com.github.yu_zhejian.ystr.utils.IterUtils;

import io.vavr.collection.List;

import it.unimi.dsi.fastutil.longs.LongList;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class RollingHashAdaptorTest {

    LongList hash(@NotNull RollingHashInterface rollingHash, byte[] string, int k) {
        rollingHash.attach(string, k);
        var retv = IterUtils.collect(rollingHash);
        rollingHash.detach();
        return retv;
    }

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
                ans, hash(new RollingHashAdaptor(HashInterface.CRC32_HASH), string, 3));
        assertIterableEquals(ans, hash(new RollingHashAdaptor(CRC32Hash::new), string, 3));
    }
}
