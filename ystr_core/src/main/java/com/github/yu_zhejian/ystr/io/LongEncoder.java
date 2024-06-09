package com.github.yu_zhejian.ystr.io;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public final class LongEncoder {

    public static @NotNull ByteArrayList encodeLong(long @NotNull ... l) {
        var bb = ByteBuffer.allocate(Long.BYTES * l.length);
        bb.clear();
        for (var i : l) {
            bb.putLong(i);
        }
        bb.rewind();
        return new ByteArrayList(bb.array());
    }
}
