package com.github.yu_zhejian.ystr.alphabet;

import com.github.yu_zhejian.ystr.utils.StrUtils;

import it.unimi.dsi.fastutil.bytes.ByteIterable;
import it.unimi.dsi.fastutil.bytes.ByteIterator;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

public final class Alphabet implements ByteIterable, Serializable {
    @Serial
    private static final long serialVersionUID = 20240929L;

    /** The byte array that is hold inside. */
    private final byte[] value;

    /** Staged pre-computed hash value. */
    private final int hash;

    /**
     * Construct IBA using an existing byte array. Note that the values will be copied!
     *
     * @param value As described.
     * @throws IllegalArgumentException If the value is empty.
     */
    public Alphabet(final byte @NotNull [] value) {
        if (value.length == 0) {
            throw new IllegalArgumentException("Empty alphabet is not allowed!");
        }
        var dupChecker = new boolean[StrUtils.ALPHABET_SIZE];
        for (final var b : value) {
            if (dupChecker[b & StrUtils.BYTE_TO_UNSIGNED_MASK]) {
                throw new IllegalArgumentException("Byte %d duplicated!".formatted(b));
            }
            dupChecker[b & StrUtils.BYTE_TO_UNSIGNED_MASK] = true;
        }
        this.value = new byte[value.length];
        System.arraycopy(value, 0, this.value, 0, value.length);
        hash = Arrays.hashCode(this.value);
    }

    /**
     * Constructor with strings.
     *
     * @param string As described.
     * @param charset As described.
     */
    public Alphabet(final @NotNull String string, Charset charset) {
        this(string.getBytes(charset));
    }

    /**
     * Convert back to string.
     *
     * @param charset As described.
     * @return As described.
     */
    public @NotNull String encode(Charset charset) {
        return new String(value, charset);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "Alphabet{" + "value=" + Arrays.toString(value) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alphabet that = (Alphabet) o;
        if (that.hash != hash) return false;
        return Objects.deepEquals(value, that.value);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    /**
     * Get the number of bytes held.
     *
     * @return As described.
     */
    public int length() {
        return value.length;
    }

    /**
     * Get byte at specific position.
     *
     * @param index As described.
     * @return As described.
     * @throws IndexOutOfBoundsException As described.
     */
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

    /**
     * Get the internal buffer. Note that the buffer will be copied!
     *
     * @return As describe.
     */
    public byte @NotNull [] getValue() {
        byte[] retv = new byte[value.length];
        System.arraycopy(value, 0, retv, 0, value.length);
        return retv;
    }
}
