package com.github.yu_zhejian.ystr_demo;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class LogUtils {
    private LogUtils() {}

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

    @Contract(pure = true)
    public static @NotNull Supplier<String> summarizeStatisticsToHumanReadable(
            @NotNull DescriptiveStatistics descriptiveStatistics, String suffix) {
        return () -> ("[%s, %s, %s, %s, %s] (mean: %s)"
                .formatted(
                        FrontendUtils.toHumanReadable(descriptiveStatistics.getMin(), suffix),
                        FrontendUtils.toHumanReadable(
                                descriptiveStatistics.getPercentile(25), suffix),
                        FrontendUtils.toHumanReadable(
                                descriptiveStatistics.getPercentile(50), suffix),
                        FrontendUtils.toHumanReadable(
                                descriptiveStatistics.getPercentile(75), suffix),
                        FrontendUtils.toHumanReadable(descriptiveStatistics.getMax(), suffix),
                        FrontendUtils.toHumanReadable(descriptiveStatistics.getMean(), suffix)));
    }

    @Contract(pure = true)
    public static @NotNull Supplier<String> summarizeStatisticsWithFormatStr(
            @NotNull StatisticalSummary descriptiveStatistics, String precision) {
        return () -> ("[%s, %s] (mean: %s sd.: %s)"
                        .formatted(precision, precision, precision, precision))
                .formatted(
                        descriptiveStatistics.getMin(),
                        descriptiveStatistics.getMax(),
                        descriptiveStatistics.getMean(),
                        descriptiveStatistics.getStandardDeviation());
    }

    @Contract(pure = true)
    public static @NotNull Supplier<String> summarizeStatisticsToHumanReadable(
            @NotNull StatisticalSummary descriptiveStatistics, String suffix) {
        return () -> ("[%s, %s] (mean: %s sd.: %s)"
                .formatted(
                        FrontendUtils.toHumanReadable(descriptiveStatistics.getMin(), suffix),
                        FrontendUtils.toHumanReadable(descriptiveStatistics.getMax(), suffix),
                        FrontendUtils.toHumanReadable(descriptiveStatistics.getMean(), suffix),
                        FrontendUtils.toHumanReadable(
                                descriptiveStatistics.getStandardDeviation(), suffix)));
    }
}
