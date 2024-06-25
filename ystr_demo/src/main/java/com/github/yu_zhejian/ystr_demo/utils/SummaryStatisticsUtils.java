package com.github.yu_zhejian.ystr_demo.utils;

import com.github.yu_zhejian.ystr.utils.FrontendUtils;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class SummaryStatisticsUtils {
    private SummaryStatisticsUtils() {}

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
