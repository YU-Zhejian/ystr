package com.github.yu_zhejian.ystr.io;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public final class LongEncoder {

    @SuppressWarnings("PMD.LooseCoupling")
    public static @NotNull ByteList encodeLong(long @NotNull ... l) {
        final var bb = ByteBuffer.allocate(Long.BYTES * l.length);
        bb.clear();
        for (final var i : l) {
            bb.putLong(i);
        }
        bb.rewind();
        return new ByteArrayList(bb.array());
    }
}
