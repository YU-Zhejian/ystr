package com.github.yu_zhejian.ystr.rolling;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import it.unimi.dsi.fastutil.longs.LongArrayList;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/** Constants for {@link NtHash} and {@link PrecomputedNtHash}. */
public abstract class NtHashBase extends RollingHashBase {
    protected static final long SEED_A = 0x3c8bfbb395c60474L;
    protected static final long SEED_C = 0x3193c18562a02b4cL;
    protected static final long SEED_G = 0x20323ed082572324L;
    protected static final long SEED_T = 0x295549f54be24456L;
    protected static final long SEED_N = 0x0000000000000000L;
    protected static final int MULTI_SHIFT = 27;
    protected static final long MULTI_SEED = 0x90b45d39fb6da1faL;

    protected static final byte CP_OFF = 0x07;
    protected static final long[] VEC_A = {
        0x3c8bfbb395c60474L, 0x7917f7672b8c08e8L, 0xf22feece571811d0L, 0xe45fdd9cae3023a1L,
        0xc8bfbb395c604743L, 0x917f7672b8c08e87L, 0x22feece571811d0fL, 0x45fdd9cae3023a1eL,
        0x8bfbb395c604743cL, 0x17f7672b8c08e879L, 0x2feece571811d0f2L, 0x5fdd9cae3023a1e4L,
        0xbfbb395c604743c8L, 0x7f7672b8c08e8791L, 0xfeece571811d0f22L, 0xfdd9cae3023a1e45L,
        0xfbb395c604743c8bL, 0xf7672b8c08e87917L, 0xeece571811d0f22fL, 0xdd9cae3023a1e45fL,
        0xbb395c604743c8bfL, 0x7672b8c08e87917fL, 0xece571811d0f22feL, 0xd9cae3023a1e45fdL,
        0xb395c604743c8bfbL, 0x672b8c08e87917f7L, 0xce571811d0f22feeL, 0x9cae3023a1e45fddL,
        0x395c604743c8bfbbL, 0x72b8c08e87917f76L, 0xe571811d0f22feecL, 0xcae3023a1e45fdd9L,
        0x95c604743c8bfbb3L, 0x2b8c08e87917f767L, 0x571811d0f22feeceL, 0xae3023a1e45fdd9cL,
        0x5c604743c8bfbb39L, 0xb8c08e87917f7672L, 0x71811d0f22feece5L, 0xe3023a1e45fdd9caL,
        0xc604743c8bfbb395L, 0x8c08e87917f7672bL, 0x1811d0f22feece57L, 0x3023a1e45fdd9caeL,
        0x604743c8bfbb395cL, 0xc08e87917f7672b8L, 0x811d0f22feece571L, 0x023a1e45fdd9cae3L,
        0x04743c8bfbb395c6L, 0x08e87917f7672b8cL, 0x11d0f22feece5718L, 0x23a1e45fdd9cae30L,
        0x4743c8bfbb395c60L, 0x8e87917f7672b8c0L, 0x1d0f22feece57181L, 0x3a1e45fdd9cae302L,
        0x743c8bfbb395c604L, 0xe87917f7672b8c08L, 0xd0f22feece571811L, 0xa1e45fdd9cae3023L,
        0x43c8bfbb395c6047L, 0x87917f7672b8c08eL, 0x0f22feece571811dL, 0x1e45fdd9cae3023aL
    };
    protected static final long[] VEC_C = {
        0x3193c18562a02b4cL, 0x6327830ac5405698L, 0xc64f06158a80ad30L, 0x8c9e0c2b15015a61L,
        0x193c18562a02b4c3L, 0x327830ac54056986L, 0x64f06158a80ad30cL, 0xc9e0c2b15015a618L,
        0x93c18562a02b4c31L, 0x27830ac540569863L, 0x4f06158a80ad30c6L, 0x9e0c2b15015a618cL,
        0x3c18562a02b4c319L, 0x7830ac5405698632L, 0xf06158a80ad30c64L, 0xe0c2b15015a618c9L,
        0xc18562a02b4c3193L, 0x830ac54056986327L, 0x06158a80ad30c64fL, 0x0c2b15015a618c9eL,
        0x18562a02b4c3193cL, 0x30ac540569863278L, 0x6158a80ad30c64f0L, 0xc2b15015a618c9e0L,
        0x8562a02b4c3193c1L, 0x0ac5405698632783L, 0x158a80ad30c64f06L, 0x2b15015a618c9e0cL,
        0x562a02b4c3193c18L, 0xac54056986327830L, 0x58a80ad30c64f061L, 0xb15015a618c9e0c2L,
        0x62a02b4c3193c185L, 0xc54056986327830aL, 0x8a80ad30c64f0615L, 0x15015a618c9e0c2bL,
        0x2a02b4c3193c1856L, 0x54056986327830acL, 0xa80ad30c64f06158L, 0x5015a618c9e0c2b1L,
        0xa02b4c3193c18562L, 0x4056986327830ac5L, 0x80ad30c64f06158aL, 0x015a618c9e0c2b15L,
        0x02b4c3193c18562aL, 0x056986327830ac54L, 0x0ad30c64f06158a8L, 0x15a618c9e0c2b150L,
        0x2b4c3193c18562a0L, 0x56986327830ac540L, 0xad30c64f06158a80L, 0x5a618c9e0c2b1501L,
        0xb4c3193c18562a02L, 0x6986327830ac5405L, 0xd30c64f06158a80aL, 0xa618c9e0c2b15015L,
        0x4c3193c18562a02bL, 0x986327830ac54056L, 0x30c64f06158a80adL, 0x618c9e0c2b15015aL,
        0xc3193c18562a02b4L, 0x86327830ac540569L, 0x0c64f06158a80ad3L, 0x18c9e0c2b15015a6L
    };
    protected static final long[] VEC_G = {
        0x20323ed082572324L, 0x40647da104ae4648L, 0x80c8fb42095c8c90L, 0x0191f68412b91921L,
        0x0323ed0825723242L, 0x0647da104ae46484L, 0x0c8fb42095c8c908L, 0x191f68412b919210L,
        0x323ed08257232420L, 0x647da104ae464840L, 0xc8fb42095c8c9080L, 0x91f68412b9192101L,
        0x23ed082572324203L, 0x47da104ae4648406L, 0x8fb42095c8c9080cL, 0x1f68412b91921019L,
        0x3ed0825723242032L, 0x7da104ae46484064L, 0xfb42095c8c9080c8L, 0xf68412b919210191L,
        0xed08257232420323L, 0xda104ae464840647L, 0xb42095c8c9080c8fL, 0x68412b919210191fL,
        0xd08257232420323eL, 0xa104ae464840647dL, 0x42095c8c9080c8fbL, 0x8412b919210191f6L,
        0x08257232420323edL, 0x104ae464840647daL, 0x2095c8c9080c8fb4L, 0x412b919210191f68L,
        0x8257232420323ed0L, 0x04ae464840647da1L, 0x095c8c9080c8fb42L, 0x12b919210191f684L,
        0x257232420323ed08L, 0x4ae464840647da10L, 0x95c8c9080c8fb420L, 0x2b919210191f6841L,
        0x57232420323ed082L, 0xae464840647da104L, 0x5c8c9080c8fb4209L, 0xb919210191f68412L,
        0x7232420323ed0825L, 0xe464840647da104aL, 0xc8c9080c8fb42095L, 0x919210191f68412bL,
        0x232420323ed08257L, 0x464840647da104aeL, 0x8c9080c8fb42095cL, 0x19210191f68412b9L,
        0x32420323ed082572L, 0x64840647da104ae4L, 0xc9080c8fb42095c8L, 0x9210191f68412b91L,
        0x2420323ed0825723L, 0x4840647da104ae46L, 0x9080c8fb42095c8cL, 0x210191f68412b919L,
        0x420323ed08257232L, 0x840647da104ae464L, 0x080c8fb42095c8c9L, 0x10191f68412b9192L
    };
    protected static final long[] VEC_T = {
        0x295549f54be24456L, 0x52aa93ea97c488acL, 0xa55527d52f891158L, 0x4aaa4faa5f1222b1L,
        0x95549f54be244562L, 0x2aa93ea97c488ac5L, 0x55527d52f891158aL, 0xaaa4faa5f1222b14L,
        0x5549f54be2445629L, 0xaa93ea97c488ac52L, 0x5527d52f891158a5L, 0xaa4faa5f1222b14aL,
        0x549f54be24456295L, 0xa93ea97c488ac52aL, 0x527d52f891158a55L, 0xa4faa5f1222b14aaL,
        0x49f54be244562955L, 0x93ea97c488ac52aaL, 0x27d52f891158a555L, 0x4faa5f1222b14aaaL,
        0x9f54be2445629554L, 0x3ea97c488ac52aa9L, 0x7d52f891158a5552L, 0xfaa5f1222b14aaa4L,
        0xf54be24456295549L, 0xea97c488ac52aa93L, 0xd52f891158a55527L, 0xaa5f1222b14aaa4fL,
        0x54be24456295549fL, 0xa97c488ac52aa93eL, 0x52f891158a55527dL, 0xa5f1222b14aaa4faL,
        0x4be24456295549f5L, 0x97c488ac52aa93eaL, 0x2f891158a55527d5L, 0x5f1222b14aaa4faaL,
        0xbe24456295549f54L, 0x7c488ac52aa93ea9L, 0xf891158a55527d52L, 0xf1222b14aaa4faa5L,
        0xe24456295549f54bL, 0xc488ac52aa93ea97L, 0x891158a55527d52fL, 0x1222b14aaa4faa5fL,
        0x24456295549f54beL, 0x488ac52aa93ea97cL, 0x91158a55527d52f8L, 0x222b14aaa4faa5f1L,
        0x4456295549f54be2L, 0x88ac52aa93ea97c4L, 0x1158a55527d52f89L, 0x22b14aaa4faa5f12L,
        0x456295549f54be24L, 0x8ac52aa93ea97c48L, 0x158a55527d52f891L, 0x2b14aaa4faa5f122L,
        0x56295549f54be244L, 0xac52aa93ea97c488L, 0x58a55527d52f8911L, 0xb14aaa4faa5f1222L,
        0x6295549f54be2445L, 0xc52aa93ea97c488aL, 0x8a55527d52f89115L, 0x14aaa4faa5f1222bL
    };

    protected static final long[] VEC_N = {
        SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N,
        SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N,
        SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N,
        SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N,
        SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N,
        SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N, SEED_N
    };

    protected static final long[][] MS_TAB = {
        VEC_N, VEC_T, VEC_N, VEC_G, VEC_A, VEC_A, VEC_N, VEC_C, // 0..7
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 8..15
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 16..23
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 24..31
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 32..39
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 40..47
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 48..55
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 56..63
        VEC_N, VEC_A, VEC_N, VEC_C, VEC_N, VEC_N, VEC_N, VEC_G, // 64..71
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 72..79
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_T, VEC_T, VEC_N, VEC_N, // 80..87
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 88..95
        VEC_N, VEC_A, VEC_N, VEC_C, VEC_N, VEC_N, VEC_N, VEC_G, // 96..103
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 104..111
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_T, VEC_T, VEC_N, VEC_N, // 112..119
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 120..127
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 128..135
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 136..143
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 144..151
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 152..159
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 160..167
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 168..175
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 176..183
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 184..191
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 192..199
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 200..207
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 208..215
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 216..223
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 224..231
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 232..239
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 240..247
        VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, VEC_N, // 248..255
    };

    protected long fwdHash;
    protected long revHash;

    /**
     * Default constructor.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     */
    protected NtHashBase(final byte @NotNull [] string, final int k, final int skipFirst) {
        super(string, k, skipFirst);
        initCurrentValue();
    }

    /**
     * As described.
     *
     * @return As described.
     */
    public long getFwdHash() {
        return fwdHash;
    }

    @SuppressWarnings("PMD.LooseCoupling")
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Tuple2<LongArrayList, LongArrayList> getAllBothHash(
            @NotNull NtHashBase ntHash, int estimatedLength) {
        final var rett =
                Tuple.of(new LongArrayList(estimatedLength), new LongArrayList(estimatedLength));
        while (ntHash.hasNext()) {
            ntHash.nextLong();
            rett._1().add(ntHash.getFwdHash());
            rett._2().add(ntHash.getRevHash());
        }
        return rett;
    }

    /**
     * As described.
     *
     * @return As described.
     */
    public long getRevHash() {
        return revHash;
    }

    /**
     * Generate multiple hashes on one k-mer allowing utilization of Bloom filters. Copied from the
     * original implementation.
     *
     * @param m Number of hashes to generate.
     * @param k K-mer size.
     * @param currentHash Hash of the current K-mer.
     * @return As described.
     */
    @Contract(pure = true)
    public static long @NotNull [] multiHash(final int m, final int k, final long currentHash) {
        long tVal;
        final var retl = new long[m];
        retl[0] = currentHash;
        for (int i = 1; i < m; i++) {
            tVal = currentHash * (i ^ k * MULTI_SEED);
            tVal ^= tVal >>> MULTI_SHIFT;
            retl[i] = tVal;
        }
        return retl;
    }

    /**
     * Generate multiple hashes on one k-mer allowing utilization of Bloom filters.
     *
     * @param m Number of hashes to generate.
     * @return As described.
     */
    @Contract(pure = true)
    public long @NotNull [] multiHash(final int m) {
        return multiHash(m, k, currentValueUnboxed);
    }
}
