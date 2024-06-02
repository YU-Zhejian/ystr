package com.github.yu_zhejian.ystr.rolling_hash;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/** A base implementation for all rolling hash algorithms. */
public abstract class RollingHashBase implements RollingHashInterface {
    /** The string you're hashing. */
    protected final byte[] string;

    /**
     * The size of the sliding window. Also, the size of K-mer in biological sequence hashing
     * functions like {@link NtHash}.
     */
    protected final int k;
    /** The offset of the current window is. */
    protected int curPos;
    /** Arbitrary starting position. */
    protected final int start;

    /** Populate {@link #currentHash} with hash of the first window. */
    protected abstract void initCurrentHash();

    /** Slide the window and update {@link #currentHash} */
    protected abstract void updateCurrentHashToNextState();

    /** The current hash value. */
    protected Long currentHash;

    /**
     * As described.
     *
     * @param string As described.
     * @param k As described.
     * @param start As described.
     */
    private void checkInput(final byte @NotNull [] string, int k, int start) {
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
    public RollingHashBase(final byte @NotNull [] string, int k, int start) {
        checkInput(string, k, start);
        this.string = string;
        this.k = k;
        this.start = start;
        curPos = start;
    }

    @Override
    public Long next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        if (curPos != start) {
            updateCurrentHashToNextState();
        }
        curPos++;
        return currentHash;
    }

    @Override
    public boolean hasNext() {
        return curPos != string.length - k + 1;
    }
}
