package nz.co.breakpoint.jmeter;

/** Super-interface for any metric value to be calculated.
 */
public interface Metric {
    /**
     * @return overall result of metric calculation
     */
    double getResult();

    /** Process a (successful) sample's measurement
     * @param value numeric value to process
     */
    default void increment(double value) {
        increment(value, true);
    }

    /** Process a (successful or failed) sample's measurement
     * @param value numeric value to process
     * @param success whether the sample was successful
     */
    void increment(double value, boolean success);

    /**
     * @return total number of samples processed
     */
    long getN();
}
