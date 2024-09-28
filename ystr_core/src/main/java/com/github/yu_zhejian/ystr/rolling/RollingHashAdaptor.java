package com.github.yu_zhejian.ystr.rolling;

import com.github.yu_zhejian.ystr.hash.HashInterface;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Adaptor that converts {@link HashInterface} to {@link RollingHashInterface} that computes hash of
 * sliding windows using brute force.
 */
public final class RollingHashAdaptor extends RollingHashBase {
    private final HashInterface hashInstance;

    /**
     * Default constructor.
     *
     * @param hashInstance As described.
     */
    public RollingHashAdaptor(@NotNull HashInterface hashInstance) {
        this.hashInstance = hashInstance;
    }

    /**
     * Default constructor.
     *
     * @param hashSupplier As described.
     */
    public RollingHashAdaptor(@NotNull Supplier<HashInterface> hashSupplier) {
        this(hashSupplier.get());
    }

    @Override
    protected void initCurrentValue() {
        ensureAttached();
        hashInstance.reset();
        hashInstance.update(string, skipFirst, skipFirst + k);
        currentValueUnboxed = hashInstance.getValue();
    }

    @Override
    protected void updateCurrentValueToNextState() {
        ensureAttached();
        hashInstance.reset();
        hashInstance.update(string, curPos, curPos + k);
        currentValueUnboxed = hashInstance.getValue();
    }
}
