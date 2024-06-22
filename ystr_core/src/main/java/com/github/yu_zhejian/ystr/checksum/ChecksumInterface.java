package com.github.yu_zhejian.ystr.checksum;

import com.github.yu_zhejian.ystr.StrUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

/**
 * Representing checksum algorithms that generate checksums no longer than 64 bits.
 *
 * <p>Some checksums may be only 16 bit or 32 bit (e.g., {@link CRC32}). Cast them to corresponding
 * data types after {@link #getValue()}.
 *
 * <p>This class is inspired by the {@link java.util.zip.Checksum } interface.
 */
public interface ChecksumInterface {

    /**
     * Get the current checksum.
     *
     * @return As described.
     */
    long getValue();

    /**
     * Add one additional byte.
     *
     * @param b Signed byte, ranged {@code [-127, 128)}.
     */
    default void update(final byte b) {
        update(StrUtils.byteToUnsigned(b));
    }

    /**
     * Add one additional byte.
     *
     * @param b Unsigned int converted from signed byte, ranged {@code [0, 256)}.
     */
    void update(final int b);

    /**
     * Add all bytes inside the given string.
     *
     * @param string As described.
     */
    default void update(final byte[] string) {
        update(string, 0, string.length);
    }

    /**
     * Add selected bytes inside the given string.
     *
     * @param string As described.
     * @param start As described.
     * @param end As described.
     * @throws IllegalArgumentException See {@link StrUtils#ensureStartEndValid(int, int, int)}.
     */
    default void update(final byte @NotNull [] string, final int start, final int end) {
        StrUtils.ensureStartEndValid(start, end, string.length);
        for (int i = start; i < end; i++) {
            update(string[i]);
        }
    }

    /**
     * This method is copied from {@link java.util.zip.Checksum#update(ByteBuffer)}.
     *
     * <p>Updates the current checksum with the bytes from the specified buffer.
     *
     * <p>The checksum is updated with the remaining bytes in the buffer, starting at the buffer's
     * position. Upon return, the buffer's position will be updated to its limit; its limit will not
     * have been changed.
     *
     * @param buffer the ByteBuffer to update the checksum with
     * @see java.util.zip.Checksum#update(ByteBuffer).
     */
    default void update(@NotNull ByteBuffer buffer) {
        int pos = buffer.position();
        int limit = buffer.limit();
        int rem = limit - pos;
        if (rem <= 0) {
            return;
        }
        if (buffer.hasArray()) {
            update(buffer.array(), pos + buffer.arrayOffset(), rem);
        } else {
            byte[] b = new byte[Math.min(buffer.remaining(), 4096)];
            while (buffer.hasRemaining()) {
                int length = Integer.min(buffer.remaining(), b.length);
                buffer.get(b, 0, length);
                update(b, 0, length);
            }
        }
        buffer.position(limit);
    }

    /** Reset the checksum to its initial state. */
    void reset();

    /**
     * Compute checksum of the entire string.
     *
     * @param supplier Supplier of {@link ChecksumInterface} implementations.
     * @param string As described.
     * @return As described.
     */
    static long fastChecksum(
            final @NotNull Supplier<ChecksumInterface> supplier, final byte[] string) {
        final var digest = supplier.get();
        digest.update(string);
        return digest.getValue();
    }
}
