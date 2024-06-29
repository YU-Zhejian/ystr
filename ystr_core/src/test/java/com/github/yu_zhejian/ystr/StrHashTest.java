package com.github.yu_zhejian.ystr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class StrHashTest {

    @Test
    void ntHash() {
        assertEquals(
                0x0baf_a672_8fc6_dabfL,
                StrHash.ntHash("TGCAG".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(
                0x4802_02d5_4e8e_becdL,
                StrHash.ntHash("ACGTC".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(
                0x1cdc_f223_eb42_cf5bL,
                StrHash.ntHash(
                        "TGACAGATGATAGATAGATCGCTCGCTAGCTAGTCAACTCGTAGTGCTGATGCTGTAGTGCAAGTCGGCTCTGCTCGCTCGC"
                                .getBytes(StandardCharsets.US_ASCII)));
    }
}
