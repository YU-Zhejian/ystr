package com.github.yu_zhejian.ystr.rolling;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import com.github.yu_zhejian.ystr.utils.IterUtils;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

class NtHashBaseTest {
    void testPrecomputedNtHash(List<Long> expected, @NotNull String bases) {
        for (var hasher : List.of(new PrecomputedNtHash(), new NtHash())) {
            hasher.attach(bases.getBytes(StandardCharsets.UTF_8), 5);
            assertIterableEquals(expected, IterUtils.collect(hasher));
            hasher.detach();
        }
    }

    @Test
    void convenient() {
        assertEquals(
                0x0baf_a672_8fc6_dabfL,
                PrecomputedNtHash.convenient("TGCAG".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(
                0x4802_02d5_4e8e_becdL,
                PrecomputedNtHash.convenient("ACGTC".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(
                0x1cdc_f223_eb42_cf5bL,
                PrecomputedNtHash.convenient(
                        "TGACAGATGATAGATAGATCGCTCGCTAGCTAGTCAACTCGTAGTGCTGATGCTGTAGTGCAAGTCGGCTCTGCTCGCTCGC"
                                .getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void canonicalTestFromRust() {
        testPrecomputedNtHash(List.of(0x0baf_a672_8fc6_dabfL), "TGCAG");
        testPrecomputedNtHash(List.of(0x4802_02d5_4e8e_becdL), "ACGTC");
        testPrecomputedNtHash(
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
        testPrecomputedNtHash(
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
        testPrecomputedNtHash(List.of(0x4802_02d5_4e8e_becdL), "ACGTC");
    }

    /**
     * Test from ntHash C++ implementation at release 1.0.0.
     *
     * @see <a
     *     href="https://github.com/bcgsc/ntHash/releases/download/1.0.0/ntHash-1.0.0.tar.gz">here</a>
     */
    @Test
    void testFromCxxImpl() {
        testPrecomputedNtHash(
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
    }

    @Test
    void multiHash() {
        assertArrayEquals(
                new long[] {0x83d852ea31167cd4L, 0xad35e252f23e962L, 0x7f4a656a0b481bccL},
                NtHashBase.multiHash(3, 3, 0x83d852ea31167cd4L));
        assertArrayEquals(
                new long[] {0x9f6acfa2235b86fcL, 0xac0135b19134c5c6L, 0xcdc0c6a41ee70be4L},
                NtHashBase.multiHash(3, 3, 0x9f6acfa2235b86fcL));
        assertArrayEquals(
                new long[] {0xd9baa1c7bee15364L, 0xc1aabe1af5428e04L, 0x347ad8ad0c2a4140L},
                NtHashBase.multiHash(3, 3, 0xd9baa1c7bee15364L));
        assertArrayEquals(
                new long[] {0x2ee5ec2129cebdffL, 0xc4e5832ca5160090L, 0xf3cb6f4b1a18cebaL},
                NtHashBase.multiHash(3, 4, 0x2ee5ec2129cebdffL));
        assertArrayEquals(
                new long[] {
                    0xf465ec1241726b5L, 0x80f020603a2da3b9L, 0x90367f234e8f325bL,
                },
                NtHashBase.multiHash(3, 4, 0xf465ec1241726b5L));
        assertArrayEquals(
                new long[] {
                    0x5524adecabe00905L, 0xd9ce1c35a7f45d5eL, 0x2ef2ca1e944ea2fbL,
                },
                NtHashBase.multiHash(3, 4, 0x5524adecabe00905L));
        assertArrayEquals(
                new long[] {
                    0x1d70cade7f8483f9L,
                    0xbf95715a5cbb4940L,
                    0xdd063c378d5e616bL,
                    0xfa770715e3fe89b2L,
                    0x2c617af0bc5193beL,
                    0x49d245da8a4beb67L,
                    0x674310bec7e5c34fL,
                    0x84b3db8039772b16L,
                    0xb69e4f6de1a55252L,
                    0xd40f1a4330120a1bL,
                    0xf17fe5261e896220L,
                    0xef0b0176eec4bc9L,
                    0x40db24092f3e2d76L,
                    0x5e4beed47aa2eabfL,
                    0x7bbcb9b144f48284L,
                    0x992d848f9758aaadL,
                    0xcb17f89e54b78d8aL,
                    0xe888c378a4d54dd3L,
                    0x5f98e448a606dd8L,
                    0x236a5926d99d0d01L,
                    0x5554cd0792af2c2fL,
                    0x72c597e5ef09ecf7L,
                    0x903662d831f3cc3cL,
                    0xada72dbd0221ac65L,
                    0xdf91a188c8104fc3L,
                    0xfd026c6e19e48faaL,
                    0x1a733753774b6f91L,
                    0x37e4022844324c58L,
                    0x69ce761405dcaee7L,
                    0x873f40e75c7eef4eL,
                    0xa4b00bc2ad1a0f75L,
                    0xc220d6acfe852f3cL,
                    0x503e023518e11bffL,
                    0x6daecd102b7c43a6L,
                    0x8b1f97ed7dd89b8dL,
                    0xa89062cf8fa2b354L,
                    0xda7ad6de6e55949cL,
                    0xf7eba1bd5de7e445L,
                    0x155c6c808b95c46eL,
                    0x32cd3766c63a3430L,
                    0x64b7ab47b4085530L,
                    0x82287639e78f0579L,
                    0x9f99411bd12e6542L,
                    0xbd0a0bf100cbb4ebL,
                    0xeef47fccfa64d654L,
                    0xc654aae28c6e59dL,
                    0x29d6158b193685a6L,
                    0x4746e0644ba4b5cfL,
                    0x7931545800f39668L,
                    0x96a21f27721e4631L,
                    0xb412ea02a7bf66faL,
                    0xd183b4e895103623L,
                    0x36e28dd4502170dL,
                    0x20def3bfb395e7d4L,
                    0x3e4fbe9dee27c7deL,
                    0x5bc08973df45b787L,
                    0x8daafd729feb50a1L,
                    0xab1bc854c9408888L,
                    0xc88c9339382c60b3L,
                    0xe5fd5de216e9377aL,
                    0x17e7d1ebd058d1c5L,
                    0x35589cc10734e82cL,
                    0x52c967ac72870017L,
                    0x703a328aac76285eL,
                    0x45f1eec7f3520755L,
                    0x6362b9a1a0a3570cL,
                    0x80d3849c56087727L,
                    0x9e444f79037487feL,
                    0xd02ec34f4a85a471L,
                    0xed9f8e2e7d1394a8L,
                    0xb1059132c6df483L,
                    0x288123f5ddcf24daL,
                    0x5a6b97d01c39459eL,
                    0x77dc62b72b5c15d7L,
                    0x954d2d8af9f175ecL,
                    0xb2bdf86098664406L,
                    0xe4a86c7dc634233aL,
                    0x219373f9198d4f3L,
                    0x1f8a021da302f4c8L,
                    0x3cfaccfb4e9ea561L,
                    0x6ee540ea898f83c6L,
                    0x8c560bd45e6d5b9fL,
                    0xa9c6d6b16cea7394L,
                    0xc737a19a35411b4dL,
                    0xf9221572735722e2L,
                    0x1692e04904c61a3bL,
                    0x3403ab2cd66bf270L,
                    0x51747602fb2dda29L,
                    0x835ee9e332de410fL,
                    0xa0cfb4c5c17e9966L,
                    0xbe407fa79003715dL,
                    0xdbb14a95a1b85a95L,
                    0xd9bbe78e986a0abL,
                    0x2b0c8952bbe4d982L,
                    0x487d543f5650f1b9L,
                    0x65ee1f1864bd19f0L,
                    0xf40b4a81bf690e3bL,
                    0x117c157f92064e62L,
                    0x2eece059e0956e49L,
                    0x4c5dab3337388e90L,
                    0x7e481f2af6eda95fL,
                    0x9bb8ea10c86f9186L,
                    0xb929b4f51bc1c9adL,
                    0xd69a7fda6d3421f4L,
                    0x884f3aa51424b7cL,
                    0x25f5be8d7fd71335L,
                    0x436689602c346b0eL,
                    0x60d7544ada834127L,
                    0x92c1c8231bf32818L,
                    0xb0329319c51cd3d1L,
                    0xcda35df7f6b88beaL,
                    0xeb1428d1a0d2a383L,
                    0x1cfe9cb4da6b88a4L,
                    0x3a6f679288d050fdL,
                    0x57e0327eb8c768b6L,
                    0x7550fd5c6e5a006fL,
                    0xa73b7129a0c829c0L,
                    0xc4ac3c0352ad1119L,
                    0xe21d06e6050fc912L,
                    0xff8dd1c73789a14bL,
                    0x317845c5669b46edL,
                    0x4ee910a8143a9644L,
                    0x6c59db8dc3a6767fL,
                    0x89caa64ff17541b6L,
                    0xbbb51a5e3d22a789L,
                    0xd925e53cefbad660L,
                    0xf696b018985cf65bL,
                    0x14077ae64ae02612L,
                },
                NtHashBase.multiHash(128, 6, 0x1d70cade7f8483f9L));
    }
}
