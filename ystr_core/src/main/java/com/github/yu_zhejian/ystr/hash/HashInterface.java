package com.github.yu_zhejian.ystr.hash;

import com.github.yu_zhejian.ystr.base.UpdatableInterface;
import com.github.yu_zhejian.ystr.checksum.CRC32;

import org.jetbrains.annotations.NotNull;

/**
 * Representing hash algorithms that generate checksums no longer than 64 bits.
 *
 * <p>Some hashes may be only 16 bit or 32 bit (e.g., {@link CRC32}). Cast them to corresponding
 * data types after {@link #getValue()}.
 *
 * <p>Hashes are not Checksums
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
    static long fastHash(
            final @NotNull HashInterface instance, final byte[] string, int start, int end) {
        instance.reset();
        instance.update(string, start, end);
        return instance.getValue();
    }

    static long fastHash(final @NotNull HashInterface instance, final byte[] string) {
        return fastHash(instance, string, 0, string.length);
    }
}
