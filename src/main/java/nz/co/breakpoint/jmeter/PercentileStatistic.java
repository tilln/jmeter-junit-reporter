package nz.co.breakpoint.jmeter;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.descriptive.rank.PSquarePercentile;
import org.apache.jmeter.util.JMeterUtils;

public class PercentileStatistic extends Statistic {
    Percentile percentile;
    double[] values;
    int stored = 0;

    public PercentileStatistic(double quantile) {
        super(new PSquarePercentile(quantile));
        final int valuesStoreLimit = JMeterUtils.getPropDefault("jmeter.junit.valuesStoreLimit", 2*1024*1024/8); // 2 MB filled with 8 byte doubles
        values = new double[valuesStoreLimit];
        percentile = new Percentile(quantile)
                .withEstimationType(Percentile.EstimationType.LEGACY); // TODO perhaps make this configurable?
    }

    public static PercentileStatistic forQuantile(double quantile) {
        return new PercentileStatistic(quantile);
    }

    @Override
    public double getResult() {
        percentile.setData(values, 0, stored);
        return stored <= values.length ? percentile.evaluate() : super.getResult();
    }

    @Override
    public void increment(double value, boolean success) {
        super.increment(value, success);
        if (stored < values.length) {
            values[stored++] = value;
        }
    }
}
