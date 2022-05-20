package nz.co.breakpoint.jmeter;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

/** Calculates the error rate of sample results as the average of values 1 and 0 for error and success, respectively.
 */
public class ErrorStatistic extends Statistic {
    public ErrorStatistic() {
        super(new Mean());
    }

    public static ErrorStatistic instance() {
        return new ErrorStatistic();
    }

    @Override
    public void increment(double value, boolean success) {
        aggregate.increment(success ? 0.0 : 1.0);
    }
}
