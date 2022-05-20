package nz.co.breakpoint.jmeter;

/** Simply counts the number of samples/hits.
 */
public class CountStatistic implements Metric {
    protected long n = 0;

    public static CountStatistic instance() {
        return new CountStatistic();
    }

    @Override
    public double getResult() {
        return n;
    }

    @Override
    public void increment(double value, boolean success) {
        ++n;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
