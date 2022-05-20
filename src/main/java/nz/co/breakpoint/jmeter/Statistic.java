package nz.co.breakpoint.jmeter;

import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.rank.PSquarePercentile;

/** Calculates a statistic aggregate that does not require storing lots of sample results,
 * but uses Apache Commons Math's StorelessUnivariateStatistic.
 * Typically used for response times aggregation.
 * Provides factory methods for creating the most commonly used ones.
 */
public class Statistic implements Metric {
    StorelessUnivariateStatistic aggregate;

    public Statistic(StorelessUnivariateStatistic aggregate) {
        this.aggregate = aggregate;
    }

    public static Metric percentile(double p) {
        return new Statistic(new PSquarePercentile(p));
    }

    public static Metric mean() {
        return new Statistic(new Mean());
    }

    public static Metric sd() {
        return new Statistic(new StandardDeviation(false));
    }

    public static Metric max() {
        return new Statistic(new Max());
    }

    public static Metric min() {
        return new Statistic(new Min());
    }

    @Override
    public double getResult() { return aggregate.getResult(); }

    @Override
    public void increment(double value, boolean success) { aggregate.increment(value); }

    @Override
    public boolean isValid() { return aggregate.getN() > 0; }
}