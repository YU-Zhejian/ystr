package com.github.yu_zhejian.ystr.rolling;

import org.jetbrains.annotations.NotNull;

/** A base implementation for all rolling algorithms. */
abstract class RollingBase<T> implements RollingInterface<T> {
    /** The string you're rolling on. */
    protected byte[] string;
    /**
     * The size of the sliding window. Also, the size of K-mer in biological sequence rolling hash
     * functions like {@link NtHash}.
     */
    protected int k;
    /** The offset of the current window is. */
    protected int curPos;
    /** Arbitrary starting position. */
    protected int skipFirst;

    @Override
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    public void attachUnchecked(final byte @NotNull [] string, final int k, final int skipFirst) {
        this.string = string;
        this.k = k;
        this.skipFirst = skipFirst;
        curPos = skipFirst;
        initCurrentValue();
    }

    /** Populate the initial state with the value of the first window. */
    protected abstract void initCurrentValue();

    /** Slide the window and update the initial state */
    protected abstract void updateCurrentValueToNextState();

    /** Safety measures */
    protected void ensureAttached() {
        if (string == null) {
            throw new NullPointerException("The current instance was not attached to any string!");
        }
    }

    @Override
    public boolean hasNext() {
        return string != null && curPos != string.length - k + 1;
    }

    @Override
    public void detach() {
        string = null;
    }
}
