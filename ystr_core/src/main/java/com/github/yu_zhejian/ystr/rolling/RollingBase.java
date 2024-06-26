package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/** A base implementation for all rolling algorithms. */
abstract class RollingBase<T> implements Iterator<T> {
    /** The string you're rolling on. */
    protected final byte[] string;
    /**
     * The size of the sliding window. Also, the size of K-mer in biological sequence rolling hash
     * functions like {@link NtHash}.
     */
    protected final int k;
    /** The offset of the current window is. */
    protected int curPos;
    /** Arbitrary starting position. */
    protected final int skipFirst;

    /** Populate the initial state with value of the first window. */
    protected abstract void initCurrentValue();

    /** Slide the window and update the initial state */
    protected abstract void updateCurrentValueToNextState();

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     */
    private void checkInput(final byte @NotNull [] string, final int k, final int skipFirst) {
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

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    protected RollingBase(final byte @NotNull [] string, final int k, final int skipFirst) {
        checkInput(string, k, skipFirst);
        this.string = string;
        this.k = k;
        this.skipFirst = skipFirst;
        curPos = skipFirst;
    }

    @Override
    public boolean hasNext() {
        return curPos != string.length - k + 1;
    }
}
