package com.github.yu_zhejian.ystr.codec;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.yu_zhejian.ystr.StrLibc;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

class TwoBitCodecTest {
    Map<byte[], byte[]> map = Map.ofEntries(
            Map.entry(new byte[] {(byte) 0x9C}, "ACGT".getBytes(StandardCharsets.US_ASCII)),
            Map.entry(new byte[] {(byte) 0x80}, "A".getBytes(StandardCharsets.US_ASCII)),
            Map.entry(new byte[] {}, "".getBytes(StandardCharsets.US_ASCII)),
            Map.entry(
                    new byte[] {27, -92, 75, 5, -101, 2, -128},
                    "TCAGAACTCTAGTTCCACAGTTTAA".getBytes(StandardCharsets.US_ASCII)),
            Map.entry(
                    new byte[] {(byte) 0x9C, (byte) 0x80},
                    "ACGTA".getBytes(StandardCharsets.US_ASCII)));

    @Test
    void testEncode() {
        var codec = new TwoBitCodec();
        for (var entry : map.entrySet()) {
            assertArrayEquals(entry.getKey(), codec.encode(entry.getValue()));
        }
        var retb = new byte[4];
        codec.encode("AAAAACGTATTTT".getBytes(StandardCharsets.US_ASCII), retb, 4, 1, 5);
        assertEquals(0, StrLibc.strncmp(new byte[] {(byte) 0x9C, (byte) 0x80}, retb, 0, 1, 2));
    }

    @Test
    void testDecode() {
        var codec = new TwoBitCodec();
        for (var entry : map.entrySet()) {
            assertEquals(
                    0,
                    StrLibc.strncmp(
                            entry.getValue(),
                            codec.decode(entry.getKey()),
                            entry.getValue().length));
        }
        var retb = new byte[20];
        codec.decode(new byte[] {0, 8, (byte) 0x9C, (byte) 0x80, 0, -12}, retb, 2, 2, 2);
        assertEquals(0, StrLibc.strncmp(new byte[] {'A', 'C', 'G', 'T', 'A'}, retb, 0, 2, 5));
    }
}
