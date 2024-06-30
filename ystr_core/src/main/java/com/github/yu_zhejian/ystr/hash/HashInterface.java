package com.github.yu_zhejian.ystr.hash;

import com.github.yu_zhejian.ystr.base.UpdatableInterface;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.function.Supplier;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

/**
 * Representing hash algorithms that generate checksums no longer than 64 bits.
 *
 * <p>Some hashes may be only 16 bit or 32 bit (e.g., {@link CRC32Hash}). Cast them to corresponding
 * data types after {@link #getValue()}.
 */
public interface HashInterface extends UpdatableInterface {
    /** Static non-thread-safe instance of {@link APHash}. */
    HashInterface AP_HASH = new APHash();
    /** Static non-thread-safe instance of {@link BitwiseFNV1a32}. */
    HashInterface BITWISE_FNV1A_32 = new BitwiseFNV1a32();
    /** Static non-thread-safe instance of {@link BitwiseFNV1a64}. */
    HashInterface BITWISE_FNV1A_64 = new BitwiseFNV1a64();
    /** Static non-thread-safe instance of {@link BKDRHash}. */
    HashInterface BKDR_HASH = new BKDRHash();
    /** Static non-thread-safe instance of {@link BPHash}. */
    HashInterface BPH_HASH = new BPHash();
    /** Static non-thread-safe instance of {@link DJBHash}. */
    HashInterface DJB_HASH = new DJBHash();
    /** Static non-thread-safe instance of {@link ELFHash}. */
    HashInterface ELF_HASH = new ELFHash();
    /** Static non-thread-safe instance of {@link JSHash}. */
    HashInterface JS_HASH = new JSHash();
    /** Static non-thread-safe instance of {@link MultiplyFNV1a32}. */
    HashInterface MULTIPLY_FNV1A_32 = new MultiplyFNV1a32();
    /** Static non-thread-safe instance of {@link MultiplyFNV1a64}. */
    HashInterface MULTIPLY_FNV1A_64 = new MultiplyFNV1a64();
    /** Static non-thread-safe instance of {@link PJWHash}. */
    HashInterface PJW_HASH = new PJWHash();
    /** Static non-thread-safe instance of {@link RSHash}. */
    HashInterface RS_HASH = new RSHash();
    /** Static non-thread-safe instance of {@link SDBMHash}. */
    HashInterface SDBM_HASH = new SDBMHash();
    /** Static non-thread-safe instance of {@link SDBMHash}. */
    HashInterface CRC32_HASH = new CRC32Hash();
    /** Static non-thread-safe instance of {@link CRC32}. */
    HashInterface JUL_CRC32_CHECKSUM = cast(new CRC32());
    /** Static non-thread-safe instance of {@link CRC32C}. */
    HashInterface JUL_CRC32C_CHECKSUM = cast(new CRC32C());
    /** Static non-thread-safe instance of {@link Adler32}. */
    HashInterface JUL_ALDER32_CHECKSUM = cast(new Adler32());

    /**
     * Get the current checksum.
     *
     * @return As described.
     */
    long getValue();

    /** Reset the checksum to its initial state. */
    void reset();

    /**
     * Cast implementations of other hashes to this interface.
     *
     * @param checksum Instance of {@link Checksum}.
     * @return Converted hash instance.
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull HashInterface cast(Checksum checksum) {
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

    /**
     * Compute checksum of part of the string.
     *
     * @param instance As described.
     * @param string As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    static long convenientHash(
            final @NotNull HashInterface instance, final byte[] string, int start, int end) {
        instance.reset();
        instance.update(string, start, end);
        return instance.getValue();
    }

    /**
     * Unchecked variant of {@link #convenientHash(HashInterface, byte[], int, int)}.
     *
     * @param instance As described.
     * @param string As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @see #convenientHash(HashInterface, byte[], int, int)
     */
    static long convenientHashUnchecked(
            final @NotNull HashInterface instance, final byte[] string, int start, int end) {
        instance.reset();
        instance.updateUnchecked(string, start, end);
        return instance.getValue();
    }

    /**
     * Full-length variant of {@link #convenientHash(HashInterface, byte[], int, int)}.
     *
     * @param instance As described.
     * @param string As described.
     * @return As described.
     * @see #convenientHash(HashInterface, byte[], int, int)
     */
    static long convenientHash(final @NotNull HashInterface instance, final byte[] string) {
        instance.reset();
        instance.update(string);
        return instance.getValue();
    }

    /**
     * Supplier variant of {@link #convenientHash(HashInterface, byte[], int, int)}.
     *
     * @param supplier As described.
     * @param string As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @see #convenientHash(HashInterface, byte[], int, int)
     */
    static long convenientHash(
            final @NotNull Supplier<HashInterface> supplier,
            final byte[] string,
            int start,
            int end) {
        var instance = supplier.get();
        return convenientHash(instance, string, start, end);
    }

    /**
     * Unchecked supplier variant of {@link #convenientHash(HashInterface, byte[], int, int)}.
     *
     * @param supplier As described.
     * @param string As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     * @see #convenientHash(HashInterface, byte[], int, int)
     */
    static long convenientHashUnchecked(
            final @NotNull Supplier<HashInterface> supplier,
            final byte[] string,
            int start,
            int end) {
        var instance = supplier.get();
        return convenientHashUnchecked(instance, string, start, end);
    }

    /**
     * Supplier full-length variant of {@link #convenientHash(HashInterface, byte[], int, int)}.
     *
     * @param supplier As described.
     * @param string As described.
     * @return As described.
     * @see #convenientHash(HashInterface, byte[], int, int)
     */
    static long convenientHash(
            final @NotNull Supplier<HashInterface> supplier, final byte[] string) {
        var instance = supplier.get();
        return convenientHash(instance, string);
    }
}
