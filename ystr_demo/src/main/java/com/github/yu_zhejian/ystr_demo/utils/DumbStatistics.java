package com.github.yu_zhejian.ystr_demo.utils;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A statistics that records and reports nothing. */
public final class DumbStatistics extends SummaryStatistics {
    @Override
    public double getMean() {
        return 0;
    }

    @Override
    public double getVariance() {
        return 0;
    }

    @Override
    public double getStandardDeviation() {
        return 0;
    }

    @Override
    public double getMax() {
        return 0;
    }

    @Override
    public double getMin() {
        return 0;
    }

    @Override
    public long getN() {
        return 0;
    }

    @Override
    public double getSum() {
        return 0;
    }

    @Contract(pure = true)
    @Override
    public @NotNull SummaryStatistics copy() {
        return new DumbStatistics();
    }

    @Override
    public void setVarianceImpl(StorelessUnivariateStatistic varianceImpl)
            throws MathIllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    @Override
    public @Nullable StorelessUnivariateStatistic getVarianceImpl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMeanImpl(StorelessUnivariateStatistic meanImpl)
            throws MathIllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    @Override
    public @Nullable StorelessUnivariateStatistic getMeanImpl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGeoMeanImpl(StorelessUnivariateStatistic geoMeanImpl)
            throws MathIllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    @Override
    public @Nullable StorelessUnivariateStatistic getGeoMeanImpl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSumLogImpl(StorelessUnivariateStatistic sumLogImpl)
            throws MathIllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    @Override
    public @Nullable StorelessUnivariateStatistic getSumLogImpl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxImpl(StorelessUnivariateStatistic maxImpl) throws MathIllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    @Override
    public @Nullable StorelessUnivariateStatistic getMaxImpl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMinImpl(StorelessUnivariateStatistic minImpl) throws MathIllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    @Override
    public @Nullable StorelessUnivariateStatistic getMinImpl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSumsqImpl(StorelessUnivariateStatistic sumsqImpl)
            throws MathIllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    @Override
    public @Nullable StorelessUnivariateStatistic getSumsqImpl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSumImpl(StorelessUnivariateStatistic sumImpl) throws MathIllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    @Override
    public @Nullable StorelessUnivariateStatistic getSumImpl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    @Override
    public void clear() {
        // Do nothing!
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public double getSecondMoment() {
        return 0;
    }

    @Override
    public double getSumOfLogs() {
        return 0;
    }

    @Override
    public double getGeometricMean() {
        return 0;
    }

    @Override
    public double getPopulationVariance() {
        return 0;
    }

    @Override
    public double getQuadraticMean() {
        return 0;
    }

    @Override
    public double getSumsq() {
        return 0;
    }

    @Override
    public void addValue(double value) {
        // Do nothing!
    }

    @Contract(pure = true)
    @Override
    public @NotNull StatisticalSummary getSummary() {
        return new DumbStatistics();
    }

    public DumbStatistics() {
        // Do nothing!
    }
}
