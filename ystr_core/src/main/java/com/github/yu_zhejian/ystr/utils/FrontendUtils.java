package com.github.yu_zhejian.ystr.utils;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

/** Helper functions for various text-based frontends. */
public final class FrontendUtils {
    /** Defunct Constructor * */
    private FrontendUtils() {}

    private static final long K = 1L << 10;
    private static final long M = 1L << 20;
    private static final long G = 1L << 30;
    private static final long T = 1L << 40;
    private static final DecimalFormat DF = new DecimalFormat("###.##");

    public static @NotNull String toHumanReadable(long n) {
        return toHumanReadable(n, "B");
    }

    public static @NotNull String toHumanReadable(@NotNull Number n) {
        return toHumanReadable(n.longValue());
    }

    public static @NotNull String toHumanReadable(@NotNull Number n, String suffix) {
        return toHumanReadable(n.longValue(), suffix);
    }

    public static @NotNull String toHumanReadable(long n, String suffix) {
        if (n > T) {
            return DF.format(n * 1.0 / T) + "T" + suffix;
        } else if (n > G) {
            return DF.format(n * 1.0 / G) + "G" + suffix;
        } else if (n > M) {
            return DF.format(n * 1.0 / M) + "M" + suffix;
        } else if (n > K) {
            return DF.format(n * 1.0 / K) + "K" + suffix;
        } else {
            return DF.format(n * 1.0) + suffix;
        }
    }
}
