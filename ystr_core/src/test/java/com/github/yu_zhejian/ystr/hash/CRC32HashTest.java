package com.github.yu_zhejian.ystr.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

class CRC32HashTest {

    @Test
    void generateCrcLookupTable() {
        Assertions.assertArrayEquals(
                CRC32Hash.CRC_POLY8_LOOKUP_TABLE, CRC32Hash.generateCrcLookupTable());
    }

    static byte[] x = Base64.getDecoder()
            .decode(
                    "iQ6PsLwRw3axwkvybygNhfmjIR/mww07/ys9C1wNdUggzG15JfbEKuaAMmi6gEnRswwSF2FaCK5foisQy5fpefoBXfrG3hvY2uVA1eKhTJibtWZfVnBaSrsn7bg6+WSBvdcxW30pgZ7CrfWBSqGJowFuzug0r7kRHsjSY/P88EY="); // a 128 byte random string.

    @Test
    void crc32() {
        final var hashConstants = new HashConstants();
        assertEquals(
                0x00000000,
                HashInterface.convenientHash(
                        hashConstants.CRC32_HASH, "".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(
                0x66a031a7,
                HashInterface.convenientHash(
                        hashConstants.CRC32_HASH, "AAA".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(
                0x5AB5AEDD,
                HashInterface.convenientHash(
                        hashConstants.CRC32_HASH, "ATCG".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(
                0x41D912FF,
                HashInterface.convenientHash(hashConstants.CRC32_HASH, new byte[] {0, 0}));
        assertEquals(0x3B1C424C, HashInterface.convenientHash(hashConstants.CRC32_HASH, new byte[] {
            (byte) 0xA9, (byte) 0xC7
        }));
        assertEquals(0x8A1C61A0L, HashInterface.convenientHash(hashConstants.CRC32_HASH, x));
    }
}
