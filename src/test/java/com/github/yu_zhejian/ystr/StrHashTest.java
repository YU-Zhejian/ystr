package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class StrHashTest {

    @Test
    void ntHash() {
        assertEquals(
                0x0baf_a672_8fc6_dabfL, StrHash.ntHash("TGCAG".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                0x4802_02d5_4e8e_becdL, StrHash.ntHash("ACGTC".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                0x1cdc_f223_eb42_cf5bL,
                StrHash.ntHash(
                        "TGACAGATGATAGATAGATCGCTCGCTAGCTAGTCAACTCGTAGTGCTGATGCTGTAGTGCAAGTCGGCTCTGCTCGCTCGC"
                                .getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void simpleKmerHashing() {
        assertEquals(
                0b0001_0001_0001,
                StrHash.simpleKmerHashing("AAA".getBytes(StandardCharsets.UTF_8)));
        assertEquals(
                0b0001_0010_0011_0100,
                StrHash.simpleKmerHashing("ACGT".getBytes(StandardCharsets.UTF_8)));
    }
}
