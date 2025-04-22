package com.github.yu_zhejian.ystr_demo.tinymap;

import com.github.yu_zhejian.ystr.utils.FrontendUtils;
import com.github.yu_zhejian.ystr.utils.LogUtils;
import com.github.yu_zhejian.ystr_demo.utils.DumbStatistics;
import com.github.yu_zhejian.ystr_demo.utils.SummaryStatisticsUtils;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public record GenomeIndexStatistics(
        AtomicLong numMinimizers,
        AtomicLong numAllKmers,
        AtomicLong numProcessedKmers,
        AtomicLong finalIndexSize,
        AtomicLong minimizerSingletonNumber,
        SummaryStatistics numPositionsPerMinimizer,
        SummaryStatistics minimizerDistances,
        SummaryStatistics shannonEntropy,
        SummaryStatistics contigLens) {

    @Contract(" -> new")
    public static @NotNull GenomeIndexStatistics create() {
        return new GenomeIndexStatistics(
                new AtomicLong(0),
                new AtomicLong(0),
                new AtomicLong(0),
                new AtomicLong(0),
                new AtomicLong(0),
                new SummaryStatistics(),
                new SummaryStatistics(),
                new SummaryStatistics(),
                new SummaryStatistics());
    }

    @Contract(" -> new")
    public static @NotNull GenomeIndexStatistics createDumb() {
        return new GenomeIndexStatistics(
                new AtomicLong(0),
                new AtomicLong(0),
                new AtomicLong(0),
                new AtomicLong(0),
                new AtomicLong(0),
                new DumbStatistics(),
                new DumbStatistics(),
                new DumbStatistics(),
                new DumbStatistics());
    }

    public void printStatistics() {
        final Logger lh = LoggerFactory.getLogger(GenomeIndexer.class.getSimpleName());
        lh.info(
                "FINAL contig number: {}, sizes: {}",
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(contigLens.getN(), "")),
                LogUtils.lazy(SummaryStatisticsUtils.summarizeStatisticsToHumanReadable(
                        contigLens, "bp")));
        lh.info(
                "FINAL k-mers: all {} ; processed {} ({})",
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(numAllKmers, "")),
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(numProcessedKmers, "")),
                LogUtils.lazy(LogUtils.calcPctLazy(numProcessedKmers, numAllKmers)));
        lh.info(
                "FINAL index size: {}",
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(finalIndexSize, "B")));
        lh.info(
                "FINAL NT entropy: {}",
                LogUtils.lazy(SummaryStatisticsUtils.summarizeStatisticsWithFormatStr(
                        shannonEntropy, "%.4f")));
        lh.info(
                "FINAL positions per minimizer: {}",
                LogUtils.lazy(SummaryStatisticsUtils.summarizeStatisticsToHumanReadable(
                        numPositionsPerMinimizer, "")));
        lh.info(
                "FINAL minimizer distances: {}",
                LogUtils.lazy(SummaryStatisticsUtils.summarizeStatisticsToHumanReadable(
                        minimizerDistances, "bp")));
        lh.info(
                "FINAL distinct minimizers: {}, singletons: {} ({})",
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(numMinimizers, "")),
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(minimizerSingletonNumber, "")),
                LogUtils.lazy(LogUtils.calcPctLazy(minimizerSingletonNumber, numMinimizers)));
    }
}
