package com.github.yu_zhejian.ystr.checksum;

import com.github.yu_zhejian.ystr.StrUtils;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Some checksum that are within 64 bits,
 *
 * <p>Some checksums may be only 16 bit or 32 bit (e.g., {@link CRC32}). Cast them to corresponding
 * data typpes after {@link #getValue()}.
 *
 * <p>This class is inspired by the {@link java.util.zip.Checksum } interface.
 */
public interface ChecksumInterface {

    long getValue();

    void update(final byte b);

    default void update(final byte[] string) {
        update(string, 0, string.length);
    }

    default void update(final byte @NotNull [] string, final int start, final int end) {
        StrUtils.ensureStartEndValid(start, end, string.length);
        for (int i = start; i < end; i++) {
            update(string[i]);
        }
    }

    void reset();

    static long fastChecksum(
            final @NotNull Supplier<ChecksumInterface> supplier, final byte[] string) {
        final var digest = supplier.get();
        digest.update(string);
        return digest.getValue();
    }
}
