package com.github.yu_zhejian.ystr_demo;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public final class FrontendUtils {
    private FrontendUtils() {}

    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = K * M;
    private static final long T = K * G;
    private static final DecimalFormat DF = new DecimalFormat("###.##");

    public static @NotNull String toHumanReadable(double n, String suffix) {
        if (n > T) {
            return DF.format(n / T) + "T" + suffix;
        } else if (n > G) {
            return DF.format(n / G) + "G" + suffix;
        } else if (n > M) {
            return DF.format(n / M) + "M" + suffix;
        } else if (n > K) {
            return DF.format(n / K) + "K" + suffix;
        } else {
            return DF.format(n) + suffix;
        }
    }

    public static @NotNull String toHumanReadable(@NotNull Number n, String suffix) {
        return toHumanReadable(n.doubleValue(), suffix);
    }
}
