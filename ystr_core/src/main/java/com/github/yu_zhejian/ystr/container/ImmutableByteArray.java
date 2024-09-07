package com.github.yu_zhejian.ystr.container;

import it.unimi.dsi.fastutil.bytes.ByteIterable;
import it.unimi.dsi.fastutil.bytes.ByteIterator;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

public final class ImmutableByteArray implements ByteIterable {
    private final byte[] value;

    public ImmutableByteArray(final byte @NotNull [] value) {
        this.value = new byte[value.length];
        System.arraycopy(value, 0, this.value, 0, value.length);
    }

    public ImmutableByteArray(final @NotNull String string, Charset charset) {
        this.value = (string.getBytes(charset));
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "ImmutableByteArray{" + "value=" + Arrays.toString(value) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableByteArray that = (ImmutableByteArray) o;
        return Objects.deepEquals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    public int length() {
        return value.length;
    }

    public byte at(int index) {
        return value[index];
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull ByteIterator iterator() {
        return new ByteIterator() {
            int curPos = 0;

            @Override
            public boolean hasNext() {
                return curPos < value.length;
            }

            @Override
            public byte nextByte() {
                byte retv = value[curPos];
                curPos++;
                return retv;
            }
        };
    }

    public byte @NotNull [] getValue() {
        byte[] retv = new byte[value.length];
        System.arraycopy(value, 0, retv, 0, value.length);
        return retv;
    }
}
