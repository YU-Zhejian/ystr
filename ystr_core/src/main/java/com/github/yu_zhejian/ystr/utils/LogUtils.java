package com.github.yu_zhejian.ystr.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class LogUtils {
    private LogUtils() {}

    /**
     * Return an lazy evaluated {@link #lazy(Supplier)}-compatible supplier for percentage
     * calculation.
     *
     * @param current As described.
     * @param total As described.
     * @return As described.
     */
    @Contract(pure = true)
    public static @NotNull Supplier<String> calcPctLazy(double current, double total) {
        return () -> "%.2f%%".formatted(100.0 * current / total);
    }

    @Contract(pure = true)
    public static @NotNull Supplier<String> calcPctLazy(
            @NotNull final Number current, @NotNull final Number total) {
        return calcPctLazy(current.doubleValue(), total.doubleValue());
    }

    /**
     * Lazy evaluated logger. Inspired under <a
     * href="https://www.seropian.eu/2021/02/slf4j-performance-lazy-argument-evaluation.html">...</a>.
     *
     * @param stringSupplier A lambda function that produces strings.
     * @return What you put as SLF4J parameters.
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Object lazy(final Supplier<?> stringSupplier) {
        return new Object() {
            @Override
            public String toString() {
                return String.valueOf(stringSupplier.get());
            }
        };
    }
}
