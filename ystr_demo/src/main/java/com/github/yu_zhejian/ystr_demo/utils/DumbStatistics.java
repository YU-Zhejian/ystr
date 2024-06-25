package com.github.yu_zhejian.ystr_demo.utils;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

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

    @Override
    public SummaryStatistics copy() {
        return null;
    }

    @Override
    public void setVarianceImpl(StorelessUnivariateStatistic varianceImpl)
            throws MathIllegalStateException {
        // Do nothing!
    }

    @Override
    public StorelessUnivariateStatistic getVarianceImpl() {
        return null;
    }

    @Override
    public void setMeanImpl(StorelessUnivariateStatistic meanImpl)
            throws MathIllegalStateException {
        // Do nothing!
    }

    @Override
    public StorelessUnivariateStatistic getMeanImpl() {
        return null;
    }

    @Override
    public void setGeoMeanImpl(StorelessUnivariateStatistic geoMeanImpl)
            throws MathIllegalStateException {
        // Do nothing!
    }

    @Override
    public StorelessUnivariateStatistic getGeoMeanImpl() {
        return null;
    }

    @Override
    public void setSumLogImpl(StorelessUnivariateStatistic sumLogImpl)
            throws MathIllegalStateException {
        // Do nothing!
    }

    @Override
    public StorelessUnivariateStatistic getSumLogImpl() {
        return null;
    }

    @Override
    public void setMaxImpl(StorelessUnivariateStatistic maxImpl) throws MathIllegalStateException {
        // Do nothing!
    }

    @Override
    public StorelessUnivariateStatistic getMaxImpl() {
        return null;
    }

    @Override
    public void setMinImpl(StorelessUnivariateStatistic minImpl) throws MathIllegalStateException {
        // Do nothing!
    }

    @Override
    public StorelessUnivariateStatistic getMinImpl() {
        return null;
    }

    @Override
    public void setSumsqImpl(StorelessUnivariateStatistic sumsqImpl)
            throws MathIllegalStateException {
        // Do nothing!
    }

    @Override
    public StorelessUnivariateStatistic getSumsqImpl() {
        return null;
    }

    @Override
    public void setSumImpl(StorelessUnivariateStatistic sumImpl) throws MathIllegalStateException {
        // Do nothing!
    }

    @Override
    public StorelessUnivariateStatistic getSumImpl() {
        return null;
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

    @Override
    public StatisticalSummary getSummary() {
        return null;
    }

    public DumbStatistics(SummaryStatistics original) throws NullArgumentException {
        // Do nothing!
    }

    public DumbStatistics() {
        // Do nothing!
    }
}
