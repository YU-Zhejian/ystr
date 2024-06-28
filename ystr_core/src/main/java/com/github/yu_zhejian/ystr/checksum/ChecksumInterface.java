package com.github.yu_zhejian.ystr.checksum;

import com.github.yu_zhejian.ystr.base.UpdatableInterface;

import org.jetbrains.annotations.NotNull;

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
     * @param instance As described
     * @param string As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    static long convenientChecksum(
            final @NotNull Checksum instance, final byte[] string, int start, int end) {
        instance.reset();
        instance.update(string, start, end);
        return instance.getValue();
    }

    /**
     * Compute checksum of the entire string.
     *
     * @param instance As described
     * @param string As described.
     * @return As described.
     */
    static long convenientChecksum(final @NotNull Checksum instance, final byte[] string) {
        instance.reset();
        instance.update(string);
        return instance.getValue();
    }

    /**
     * Compute checksum of part of the string.
     *
     * @param instance As described
     * @param string As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    static long convenientChecksum(
            final @NotNull ChecksumInterface instance, final byte[] string, int start, int end) {
        instance.reset();
        instance.update(string, start, end);
        return instance.getValue();
    }

    /**
     * Compute checksum of the entire string.
     *
     * @param instance As described
     * @param string As described.
     * @return As described.
     */
    static long convenientChecksum(final @NotNull ChecksumInterface instance, final byte[] string) {
        instance.reset();
        instance.update(string);
        return instance.getValue();
    }
}
