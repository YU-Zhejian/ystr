package com.github.yu_zhejian.ystr.rolling;

import com.github.yu_zhejian.ystr.hash.HashInterface;

import io.vavr.Function3;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class RollingHashAdaptor extends RollingHashBase {
    private final HashInterface hashInstance;

    /**
     * Default constructor.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     * @param hashInstance As described.
     */
    public RollingHashAdaptor(
            byte @NotNull [] string, int k, int skipFirst, HashInterface hashInstance) {
        super(string, k, skipFirst);
        this.hashInstance = hashInstance;
        initCurrentValue();
    }

    /**
     * Default constructor.
     *
     * @param string As described.
     * @param k As described.
     * @param skipFirst As described.
     * @param hashSupplier As described.
     */
    public RollingHashAdaptor(
            byte @NotNull [] string,
            int k,
            int skipFirst,
            @NotNull Supplier<HashInterface> hashSupplier) {
        this(string, k, skipFirst, hashSupplier.get());
    }

    @Override
    protected void initCurrentValue() {
        hashInstance.reset();
        hashInstance.update(string, skipFirst, skipFirst + k);
        currentValueUnboxed = hashInstance.getValue();
    }

    @Contract(pure = true)
    public static @NotNull Function3<byte @NotNull [], Integer, Integer, RollingHashInterface>
            supply(HashInterface hashInstance) {
        return (string, k, skipFirst) -> new RollingHashAdaptor(string, k, skipFirst, hashInstance);
    }

    @Contract(pure = true)
    public static @NotNull Function3<byte @NotNull [], Integer, Integer, RollingHashInterface>
            supply(@NotNull Supplier<HashInterface> supplier) {
        return supply(supplier.get());
    }

    @Override
    protected void updateCurrentValueToNextState() {
        hashInstance.reset();
        hashInstance.update(string, curPos, curPos + k);
        currentValueUnboxed = hashInstance.getValue();
    }
}
