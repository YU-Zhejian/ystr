package com.github.yu_zhejian.ystr.io;

import it.unimi.dsi.fastutil.longs.LongList;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public final class LongEncoder {

    public static @NotNull ByteBuffer encodeLongList(@NotNull LongList l) {
        final var bb = ByteBuffer.allocate(Long.BYTES * l.size());
        bb.clear();
        for (final var i : l) {
            bb.putLong(i);
        }
        bb.rewind();
        return bb;
    }

    public static @NotNull ByteBuffer encodeLongArray(long @NotNull [] l) {
        final var bb = ByteBuffer.allocate(Long.BYTES * l.length);
        bb.clear();
        for (final var i : l) {
            bb.putLong(i);
        }
        bb.rewind();
        return bb;
    }

    @SuppressWarnings("PMD.LooseCoupling")
    public static @NotNull ByteBuffer encodeLongs(long @NotNull ... l) {
        return encodeLongArray(l);
    }
}
