package com.github.yu_zhejian.ystr.hash;

import com.github.yu_zhejian.ystr.base.UpdatableInterface;
import com.github.yu_zhejian.ystr.checksum.CRC32;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

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
     * @param supplier Supplier of {@link HashInterface} implementations.
     * @param string As described.
     * @param start As described.
     * @param end As described.
     * @return As described.
     */
    static long fastHash(
            final @NotNull Supplier<HashInterface> supplier,
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
     * @param supplier Supplier of {@link HashInterface} implementations.
     * @param string As described.
     * @return As described.
     */
    static long fastHash(final @NotNull Supplier<HashInterface> supplier, final byte[] string) {
        return fastHash(supplier, string, 0, string.length);
    }
}
