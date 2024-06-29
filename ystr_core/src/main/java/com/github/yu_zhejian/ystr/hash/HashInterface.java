package com.github.yu_zhejian.ystr.hash;

import com.github.yu_zhejian.ystr.base.UpdatableInterface;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
 *
 * <p>Hashes are not Checksums
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

    static long convenientHash(final @NotNull HashInterface instance, final byte[] string) {
        instance.reset();
        instance.update(string);
        return instance.getValue();
    }

    static long convenientHash(
            final @NotNull Checksum instance, final byte[] string, int start, int end) {
        instance.reset();
        instance.update(string, start, end);
        return instance.getValue();
    }

    static long convenientHash(final @NotNull Checksum instance, final byte[] string) {
        instance.reset();
        instance.update(string);
        return instance.getValue();
    }

    static long convenientHash(
            final @NotNull Supplier<?> supplier, final byte[] string, int start, int end) {
        var instance = supplier.get();
        if (instance instanceof HashInterface hashInterface) {
            return convenientHash(hashInterface, string, start, end);
        } else if (instance instanceof Checksum checksum) {
            return convenientHash(checksum, string, start, end);
        }
        throw new IllegalArgumentException("Unsupported hash type: " + instance.getClass());
    }

    static long convenientHash(final @NotNull Supplier<?> supplier, final byte[] string) {
        var instance = supplier.get();
        if (instance instanceof HashInterface hashInterface) {
            return convenientHash(hashInterface, string);
        } else if (instance instanceof Checksum checksum) {
            return convenientHash(checksum, string);
        }
        throw new IllegalArgumentException("Unsupported hash type: " + instance.getClass());
    }
}
