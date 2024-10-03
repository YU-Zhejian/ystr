package com.github.yu_zhejian.ystr.translate;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;

public final class CodonRecord implements Serializable {
    @Serial
    private static final long serialVersionUID = 20241003L;

    /** The byte array that is hold inside. */
    private final byte[] value;

    /**
     * Construct the codon table using an existing byte array.
     *
     * @param value As described.
     * @throws IllegalArgumentException If the value is empty.
     */
    private CodonRecord(final byte @NotNull [] value) {
        if (value.length != 64) {
            throw new IllegalArgumentException("Empty alphabet is not allowed!");
        }
        this.value = value;
    }

    /**
     * Constructor with strings.
     *
     * @param string As described.
     * @param charset As described.
     */
    public CodonRecord(final @NotNull String string, Charset charset) {
        this(string.getBytes(charset));
    }

    @Override
    public @NotNull String toString() {
        return "CodonRecord{" + "value=" + Arrays.toString(value) + '}';
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
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
}
