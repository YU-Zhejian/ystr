package com.github.yu_zhejian.ystr.rolling;

import static org.junit.jupiter.api.Assertions.*;

import com.github.yu_zhejian.ystr.StrHash;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class NtHashTest {
    long getNthNtHash1(@NotNull String input, int n, int k) {
        return new NtHash(input.getBytes(StandardCharsets.UTF_8), k, n).next();
    }

    long getNthNtHash2(@NotNull String input, int n, int k) {
        var nLeft = n;
        var nth = new NtHash(input.getBytes(StandardCharsets.UTF_8), k, 0);
        while (nLeft > 0) {
            nth.next();
            nLeft--;
        }
        return nth.next();
    }

    void testNtHash(List<Long> expected, @NotNull String bases, int k) {
        var nth = new NtHash(bases.getBytes(StandardCharsets.UTF_8), k, 0);
        var actual = new ArrayList<Long>();
        while (nth.hasNext()) {
            actual.add(nth.next());
        }
        assertIterableEquals(expected, actual);
    }

    void testNtHash(List<Long> expected, @NotNull String bases) {
        testNtHash(expected, bases, 5);
    }

    @Test
    void testEqualHashAtDifferentPosition() {
        var str1 = "NNNAGCUNNN";
        var str2 = "AGCTNN";

        assertEquals(getNthNtHash2(str1, 3, 4), getNthNtHash2(str2, 0, 4));
        assertEquals(getNthNtHash2(str1, 4, 4), getNthNtHash2(str2, 1, 4));
        assertEquals(getNthNtHash2(str1, 4, 3), getNthNtHash2(str2, 1, 3));

        // Simple ntHash
        Assertions.assertEquals(
                StrHash.ntHash("AGCT".getBytes(StandardCharsets.UTF_8)), getNthNtHash2(str2, 0, 4));
        assertEquals(
                StrHash.ntHash("GCTN".getBytes(StandardCharsets.UTF_8)), getNthNtHash2(str2, 1, 4));
        assertEquals(
                StrHash.ntHash("GCT".getBytes(StandardCharsets.UTF_8)), getNthNtHash2(str2, 1, 3));

        // Different skipping strategy
        assertEquals(getNthNtHash1(str1, 3, 4), getNthNtHash2(str2, 0, 4));
        assertEquals(getNthNtHash1(str1, 4, 4), getNthNtHash2(str2, 1, 4));
        assertEquals(getNthNtHash1(str1, 4, 3), getNthNtHash2(str2, 1, 3));
    }

    @Test
    void canonicalTestFromRust() {
        testNtHash(List.of(0x0baf_a672_8fc6_dabfL), "TGCAG");
        testNtHash(List.of(0x4802_02d5_4e8e_becdL), "ACGTC");
        testNtHash(
                List.of(
                        0x4802_02d5_4e8e_becdL,
                        0xa997_bdc6_28b4_c98eL,
                        0x8c6d_7ab2_0911_b216L,
                        0x5ddc_b093_90aa_feefL,
                        0x25ff_3ac4_bc92_382fL,
                        0x9bda_9a5c_3560_3946L,
                        0x82d4_49e5_b371_0ccdL,
                        0x1e92_6ce7_780a_b812L,
                        0x2f6e_d7b2_2647_3a86L,
                        0xd186_5edf_eb55_b037L,
                        0x38b5_7494_189a_8afeL,
                        0x1b23_5fc5_ecac_f386L,
                        0x1eab_5d82_920f_da13L,
                        0x02c8_d157_4673_bdcdL,
                        0x0baf_a672_8fc6_dabfL,
                        0x14a3_3bb9_2827_7bedL),
                "ACGTCGTCAGTCGATGCAGT");
        testNtHash(
                List.of(
                        0x4802_02d5_4e8e_becdL,
                        0xa997_bdc6_28b4_c98eL,
                        0xd186_5edf_eb55_b037L,
                        0xe015_9f5a_89f5_9b7bL,
                        0xe640_9a0f_689e_64e4L,
                        0x7a05_4a39_df66_1723L,
                        0x6d74_fee7_0283_5974L,
                        0xb744_44dd_9a94_cbf3L),
                "ACGTCGANNGTA");
        testNtHash(List.of(0x4802_02d5_4e8e_becdL), "ACGTC");
    }

    /**
     * Test from ntHash C++ implementation at release 1.0.0.
     *
     * @see <a
     *     href="https://github.com/bcgsc/ntHash/releases/download/1.0.0/ntHash-1.0.0.tar.gz">here</a>
     */
    @Test
    void testFromCxxImpl() {
        testNtHash(
                List.of(
                        0x152c66ec978d36fbL,
                        0x54a5e3c2e1c4d24L,
                        0x69b7006348ee9f77L,
                        0x3946ffa7ab031f7fL,
                        0x17b21d5ea146c8e7L,
                        0x64471395fd734b7L,
                        0x47c81de69a06a516L,
                        0x4a6f941a5f3281e1L,
                        0x7922d5ced6082598L,
                        0xcdf183c3c25fc008L,
                        0x3b0fb0705edf25daL,
                        0x36a8398c9beb012dL,
                        0x40941f5cc26de12fL,
                        0x970afba36d554cefL,
                        0x352f086ac4423856L),
                "GAGTGTCAAACATTCAGAC");
        testNtHash(
                List.of(
                        0xa576547a7fe40ed0L,
                        0x9b1eda9a185413ceL,
                        0x8ecb30f7c3ac8e94L,
                        0xad839677079e433cL,
                        0xc84ccda04070af34L,
                        0xd9baa1c7bee15364L),
                "GAGTGTCA",
                3);
    }
}
