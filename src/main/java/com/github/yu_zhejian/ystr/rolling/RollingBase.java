package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
    protected final int start;

    /** The current value of some kind. */
    protected T currentValue;

    /** Populate {@link #currentValue} with value of the first window. */
    protected abstract void initCurrentValue();

    /** Slide the window and update {@link #currentValue} */
    protected abstract void updateCurrentValueToNextState();

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param start As described.
     */
    private void checkInput(final byte @NotNull [] string, final int k, final int start) {
        if (k <= 0) {
            throw new IllegalArgumentException("k should be positive. Actual: %d".formatted(k));
        }
        if (k > string.length) {
            throw new IllegalArgumentException(
                    "k cannot be larger than string length (current: %d vs. %d)"
                            .formatted(k, string.length));
        }
        if (start < 0) {
            throw new IllegalArgumentException(
                    "start should be positive or zero. Actual: %d".formatted(start));
        }
        if (start > string.length - k) {
            throw new IllegalArgumentException(
                    "start too long. Max: %d. Actual: %d".formatted(string.length - k, start));
        }
    }

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param start As described.
     */
    protected RollingBase(final byte @NotNull [] string, final int k, final int start) {
        checkInput(string, k, start);
        this.string = string;
        this.k = k;
        this.start = start;
        curPos = start;
    }

    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        if (curPos != start) {
            updateCurrentValueToNextState();
        }
        curPos++;
        return currentValue;
    }

    @Override
    public boolean hasNext() {
        return curPos != string.length - k + 1;
    }
}
