package com.github.yu_zhejian.ystr.base;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/** A string consumer that consumes bytes. */
public interface UpdatableInterface {

    /**
     * Add one additional byte.
     *
     * @param b Signed byte, ranged {@code [-127, 128)}.
     */
    default void update(final byte b) {
        update(b & 0xFF);
    }

    /**
     * Add one additional byte.
     *
     * @param b Unsigned int converted from signed byte, ranged {@code [0, 256)}.
     */
    void update(int b);

    /**
     * Add all bytes inside the given string.
     *
     * @param string As described.
     */
    default void update(final byte @NotNull [] string) {
        // Avoid boundary check.
        for (byte b : string) {
            update(b & 0xFF);
        }
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
            update(string[i] & 0xFF);
        }
    }

    /**
     * This method is copied from {@link java.util.zip.Checksum#update(ByteBuffer)}.
     *
     * <p>Updates the current instance with the bytes from the specified buffer.
     *
     * <p>The instance is updated with the remaining bytes in the buffer, starting at the buffer's
     * position. Upon return, the buffer's position will be updated to its limit; its limit will not
     * have been changed.
     *
     * @param buffer the ByteBuffer to update the checksum with
     * @see java.util.zip.Checksum#update(ByteBuffer).
     */
    default void update(@NotNull ByteBuffer buffer) {
        final int pos = buffer.position();
        final int limit = buffer.limit();
        final int rem = limit - pos;
        if (rem <= 0) {
            return;
        }
        if (buffer.hasArray()) {
            update(buffer.array(), pos + buffer.arrayOffset(), rem);
        } else {
            final byte[] b = new byte[Math.min(buffer.remaining(), 4096)];
            while (buffer.hasRemaining()) {
                final int length = Integer.min(buffer.remaining(), b.length);
                buffer.get(b, 0, length);
                update(b, 0, length);
            }
        }
        buffer.position(limit);
    }
}
