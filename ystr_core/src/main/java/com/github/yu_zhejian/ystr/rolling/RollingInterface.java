package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface RollingInterface<T> extends Iterator<T> {

    default void attach(final byte @NotNull [] string, final int k, final int skipFirst) {
        checkInput(string, k, skipFirst);
        attachUnchecked(string, k, skipFirst);
    }

    void attachUnchecked(byte[] string, int k, int skipFirst);

    default void attach(final byte @NotNull [] string, final int k) {
        attachUnchecked(string, k, 0);
    }

    void detach();

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     */
    static void checkInput(final byte @NotNull [] string, final int k, final int skipFirst) {
        if (k <= 0) {
            throw new IllegalArgumentException("k should be positive. Actual: %d".formatted(k));
        }
        if (k > string.length) {
            throw new IllegalArgumentException(
                    "k cannot be larger than string length (current: %d vs. %d)"
                            .formatted(k, string.length));
        }
        if (skipFirst < 0) {
            throw new IllegalArgumentException(
                    "start should be positive or zero. Actual: %d".formatted(skipFirst));
        }
        if (skipFirst > string.length - k) {
            throw new IllegalArgumentException(
                    "start too long. Max: %d. Actual: %d".formatted(string.length - k, skipFirst));
        }
    }
}
