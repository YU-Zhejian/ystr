package com.github.yu_zhejian.ystr.hash;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

/** Hash constants. */
public final class HashConstants {
    /** Static non-thread-safe instance of {@link APHash}. */
    public static final HashInterface AP_HASH = new APHash();
    /** Static non-thread-safe instance of {@link BitwiseFNV1a32}. */
    public static final HashInterface BITWISE_FNV1A_32 = new BitwiseFNV1a32();
    /** Static non-thread-safe instance of {@link BitwiseFNV1a64}. */
    public static final HashInterface BITWISE_FNV1A_64 = new BitwiseFNV1a64();
    /** Static non-thread-safe instance of {@link BKDRHash}. */
    public static final HashInterface BKDR_HASH = new BKDRHash();
    /** Static non-thread-safe instance of {@link BPHash}. */
    public static final HashInterface BPH_HASH = new BPHash();
    /** Static non-thread-safe instance of {@link DJBHash}. */
    public static final HashInterface DJB_HASH = new DJBHash();
    /** Static non-thread-safe instance of {@link ELFHash}. */
    public static final HashInterface ELF_HASH = new ELFHash();
    /** Static non-thread-safe instance of {@link JSHash}. */
    public static final HashInterface JS_HASH = new JSHash();
    /** Static non-thread-safe instance of {@link MultiplyFNV1a32}. */
    public static final HashInterface MULTIPLY_FNV1A_32 = new MultiplyFNV1a32();
    /** Static non-thread-safe instance of {@link MultiplyFNV1a64}. */
    public static final HashInterface MULTIPLY_FNV1A_64 = new MultiplyFNV1a64();
    /** Static non-thread-safe instance of {@link PJWHash}. */
    public static final HashInterface PJW_HASH = new PJWHash();
    /** Static non-thread-safe instance of {@link RSHash}. */
    public static final HashInterface RS_HASH = new RSHash();
    /** Static non-thread-safe instance of {@link SDBMHash}. */
    public static final HashInterface SDBM_HASH = new SDBMHash();
    /** Static non-thread-safe instance of {@link SDBMHash}. */
    public static final HashInterface CRC32_HASH = new CRC32Hash();
    /** Static non-thread-safe instance of {@link CRC32}. */
    public static final HashInterface JUL_CRC32_CHECKSUM = cast(new CRC32());
    /** Static non-thread-safe instance of {@link CRC32C}. */
    public static final HashInterface JUL_CRC32C_CHECKSUM = cast(new CRC32C());
    /** Static non-thread-safe instance of {@link Adler32}. */
    public static final HashInterface JUL_ALDER32_CHECKSUM = cast(new Adler32());

    private HashConstants() {}

    /**
     * Cast implementations of other hashes to this interface.
     *
     * @param checksum Instance of {@link Checksum}.
     * @return Converted hash instance.
     */
    @Contract(value = "_ -> new", pure = true)
    private static @NotNull HashInterface cast(Checksum checksum) {
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
            public void update(byte @NotNull [] string) {
                checksum.update(string);
            }

            @Override
            public void update(byte @NotNull [] string, int start, int end) {
                checksum.update(string, start, end - start);
            }

            @Override
            public void updateUnchecked(byte @NotNull [] string, int start, int end) {
                checksum.update(string, start, end - start);
            }

            @Override
            public void update(@NotNull ByteBuffer buffer) {
                checksum.update(buffer);
            }
        };
    }
}
