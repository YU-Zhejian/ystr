package com.github.yu_zhejian.ystr.utils;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public final class FrontendUtils {
    private FrontendUtils() {}

    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = K * M;
    private static final long T = K * G;
    private static final DecimalFormat DF = new DecimalFormat("###.##");

    public static @NotNull String toHumanReadable(long n) {
        return toHumanReadable(n, "B");
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

    public static @NotNull String toHumanReadable(@NotNull Number n, String suffix) {
        return toHumanReadable(n.doubleValue(), suffix);
    }
}
