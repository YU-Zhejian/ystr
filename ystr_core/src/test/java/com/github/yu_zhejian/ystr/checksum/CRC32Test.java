package com.github.yu_zhejian.ystr.checksum;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

class CRC32Test {

    @Test
    void generateCrcLookupTable() {
        assertArrayEquals(CRC32.CRC_POLY8_LOOKUP_TABLE, CRC32.generateCrcLookupTable());
    }

    static byte[] x = Base64.getDecoder()
            .decode(
                    "iQ6PsLwRw3axwkvybygNhfmjIR/mww07/ys9C1wNdUggzG15JfbEKuaAMmi6gEnRswwSF2FaCK5foisQy5fpefoBXfrG3hvY2uVA1eKhTJibtWZfVnBaSrsn7bg6+WSBvdcxW30pgZ7CrfWBSqGJowFuzug0r7kRHsjSY/P88EY="); // a 128 byte random string.

    @Test
    void crc32() {
        var crc32 = new CRC32();
        assertEquals(
                0x00000000,
                ChecksumInterface.fastChecksum(crc32, "".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(
                0x66a031a7,
                ChecksumInterface.fastChecksum(crc32, "AAA".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(
                0x5AB5AEDD,
                ChecksumInterface.fastChecksum(crc32, "ATCG".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(0x41D912FF, ChecksumInterface.fastChecksum(crc32, new byte[] {0, 0}));
        assertEquals(
                0x3B1C424C,
                ChecksumInterface.fastChecksum(crc32, new byte[] {(byte) 0xA9, (byte) 0xC7}));
        assertEquals((long) 0x8A1C61A0, ChecksumInterface.fastChecksum(crc32, x));
    }
}
