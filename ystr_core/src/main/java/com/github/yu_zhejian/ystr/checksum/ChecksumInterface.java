package com.github.yu_zhejian.ystr.checksum;

import com.github.yu_zhejian.ystr.base.UpdatableInterface;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.zip.Checksum;

/**
 * Representing checksum algorithms that generate checksums no longer than 64 bits.
 *
 * <p>Some checksums may be only 16 bit or 32 bit (e.g., {@link CRC32}). Cast them to corresponding
 * data types after {@link #getValue()}.
 *
 * <p>This class is inspired by the {@link java.util.zip.Checksum } interface.
 */
public interface ChecksumInterface extends UpdatableInterface {

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
     * @param supplier Supplier of {@link ChecksumInterface} implementations.
     * @param string As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    static long fastChecksum(
            final @NotNull Supplier<ChecksumInterface> supplier,
            final byte[] string,
            int start,
            int end) {
        final var digest = supplier.get();
        digest.update(string, start, end);
        return digest.getValue();
    }

    /**
     * Compute checksum of the entire string.
     *
     * @param supplier Supplier of {@link ChecksumInterface} implementations.
     * @param string As described.
     * @return As described.
     */
    static long fastChecksum(
            final @NotNull Supplier<ChecksumInterface> supplier, final byte[] string) {
        return fastChecksum(supplier, string, 0, string.length);
    }

    /**
     * {@link #fastChecksum(Supplier, byte[], int, int)} on {@link Checksum}.
     *
     * @param supplier As described.
     * @param string As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    static long fastJULZipChecksum(
            final @NotNull Supplier<Checksum> supplier, final byte[] string, int start, int end) {
        final var digest = supplier.get();
        digest.update(string, start, end);
        return digest.getValue();
    }

    /**
     * {@link #fastChecksum(Supplier, byte[])} on {@link Checksum}.
     *
     * @param supplier As described.
     * @param string As described.
     * @return As described.
     */
    static long fastJULZipChecksum(
            final @NotNull Supplier<Checksum> supplier, final byte[] string) {
        return fastJULZipChecksum(supplier, string, 0, string.length);
    }
}
