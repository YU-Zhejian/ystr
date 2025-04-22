package com.github.yu_zhejian.ystr.hash;

import com.github.yu_zhejian.ystr.container.UpdatableInterface;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Representing hash algorithms that generate checksums no longer than 64 bits.
 *
 * <p>Some hashes may be only 16 bit or 32 bit (e.g., {@link CRC32Hash}). Cast them to corresponding
 * data types after {@link #getValue()}.
 */
public interface HashInterface extends UpdatableInterface {

    /**
     * Get the current checksum.
     *
     * @return As described.
     */
    long getValue();

    /** Reset the checksum to its initial state. */
    void reset();

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
            final @NotNull HashInterface instance,
            final byte[] string,
            final int start,
            final int end) {
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
            final @NotNull HashInterface instance,
            final byte[] string,
            final int start,
            final int end) {
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
            final int start,
            final int end) {
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
            final int start,
            final int end) {
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
