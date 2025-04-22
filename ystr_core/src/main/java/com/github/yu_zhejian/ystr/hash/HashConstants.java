package com.github.yu_zhejian.ystr.hash;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

/**
 * Hash constants.
 *
 * @apiNote All constants defined in this class are thread safe.
 *     <p>However, you need to initialize a new instance for every thread.
 */
public final class HashConstants {
    /** Instance of {@link APHash}. */
    public final HashInterface AP_HASH = new APHash();
    /** Instance of {@link BitwiseFNV1a32}. */
    public final HashInterface BITWISE_FNV1A_32 = new BitwiseFNV1a32();
    /** Instance of {@link BitwiseFNV1a64}. */
    public final HashInterface BITWISE_FNV1A_64 = new BitwiseFNV1a64();
    /** Instance of {@link BKDRHash}. */
    public final HashInterface BKDR_HASH = new BKDRHash();
    /** Instance of {@link BPHash}. */
    public final HashInterface BPH_HASH = new BPHash();
    /** Instance of {@link DJBHash}. */
    public final HashInterface DJB_HASH = new DJBHash();
    /** Instance of {@link ELFHash}. */
    public final HashInterface ELF_HASH = new ELFHash();
    /** Instance of {@link JSHash}. */
    public final HashInterface JS_HASH = new JSHash();
    /** Instance of {@link MultiplyFNV1a32}. */
    public final HashInterface MULTIPLY_FNV1A_32 = new MultiplyFNV1a32();
    /** Instance of {@link MultiplyFNV1a64}. */
    public final HashInterface MULTIPLY_FNV1A_64 = new MultiplyFNV1a64();
    /** Instance of {@link PJWHash}. */
    public final HashInterface PJW_HASH = new PJWHash();
    /** Instance of {@link RSHash}. */
    public final HashInterface RS_HASH = new RSHash();
    /** Instance of {@link SDBMHash}. */
    public final HashInterface SDBM_HASH = new SDBMHash();
    /** Instance of {@link SDBMHash}. */
    public final HashInterface CRC32_HASH = new CRC32Hash();
    /** Instance of {@link CRC32}. */
    public final HashInterface JUL_CRC32_CHECKSUM = cast(new CRC32());
    /** Instance of {@link CRC32C}. */
    public final HashInterface JUL_CRC32C_CHECKSUM = cast(new CRC32C());
    /** Instance of {@link Adler32}. */
    public final HashInterface JUL_ALDER32_CHECKSUM = cast(new Adler32());

    /** Default initializer. */
    public HashConstants() {}

    /**
     * Cast implementations of other hashes to this interface.
     *
     * @param checksum Instance of {@link Checksum}.
     * @return Converted hash instance.
     */
    @Contract(value = "_ -> new", pure = true)
    private static @NotNull HashInterface cast(final Checksum checksum) {
        return new HashInterface() {
            @Override
            public long getValue() {
                return checksum.getValue();
            }

            @Override
            public void reset() {
                checksum.reset();
            }

            @Override
            public void update(int b) {
                checksum.update(b);
            }

            @Override
            public void update(final byte @NotNull [] string) {
                checksum.update(string);
            }

            @Override
            public void update(final byte @NotNull [] string, final int start, final int end) {
                checksum.update(string, start, end - start);
            }

            @Override
            public void updateUnchecked(
                    final byte @NotNull [] string, final int start, final int end) {
                checksum.update(string, start, end - start);
            }

            @Override
            public void update(final @NotNull ByteBuffer buffer) {
                checksum.update(buffer);
            }
        };
    }
}
