package nz.co.breakpoint.jmeter;

import java.util.ResourceBundle;
import java.util.regex.Pattern;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A TestCase evaluates a KPI after processing a set of samples.
 */
public class TestCase {
    private static final Logger log = LoggerFactory.getLogger(TestCase.class);

    protected String name, description;
    protected Metric metric;
    protected Pattern labelPattern = Pattern.compile(".*");
    protected Comparator comparator = Comparator.LT;
    protected double threshold = Double.NaN;

    private static final String FAILURE_DETAILS_FORMAT, METRIC_UNDEFINED, NO_SAMPLES, ACTUAL_UNDEFINED, THRESHOLD_UNDEFINED;
    static {
        ResourceBundle rb = ResourceBundle.getBundle(JUnitReporter.class.getName()+"Resources", JMeterUtils.getLocale());
        FAILURE_DETAILS_FORMAT = getResourceBundleString(rb, "testCase.failureDetailsFormat", "Actual value %1$f exceeds threshold %2$f for samples matching \"%3$s\"");
        METRIC_UNDEFINED = getResourceBundleString(rb, "testCase.metricUndefined", "Metric undefined");
        NO_SAMPLES = getResourceBundleString(rb, "testCase.noSamples", "No samples to aggregate");
        ACTUAL_UNDEFINED = getResourceBundleString(rb, "testCase.actualUndefined", "Actual metric value undefined");
        THRESHOLD_UNDEFINED = getResourceBundleString(rb, "testCase.thresholdUndefined", "Threshold value undefined");
    }

    static String getResourceBundleString(ResourceBundle bundle, String key, String _default) {
        return (bundle != null && bundle.containsKey(key)) ? bundle.getString(key) : _default;
    }

    public TestCase(String name) {
        this.name = name;
    }

    public static TestCase fromKPI(KPI def) {
        return fromKPI(def.getName(), def.getMetric(), def.getLabel(), def.getComparator(), def.getThreshold());
    }

    public static TestCase fromKPI(String name, String metricName, String labelPattern, String comparator, String threshold) {
        TestCase tc = new TestCase(name);

        if (labelPattern == null || labelPattern.isEmpty()) {
            labelPattern = ".*";
        }
        if (comparator == null || comparator.isEmpty()) {
            comparator = "<";
        }
        if (threshold == null || threshold.isEmpty()) {
            threshold = "0.0";
        }

        tc.labelPattern = Pattern.compile(labelPattern);
        tc.comparator = Comparator.fromString(comparator);
        try {
            tc.threshold = Double.parseDouble(threshold);
        } catch (NumberFormatException e) {
            log.error("Threshold needs to be a number, got {}", threshold, e);
        }
        tc.description = String.format("%s(%s) %s %s", metricName, labelPattern, comparator, threshold);

        if (metricName == null || metricName.isEmpty()) {
            tc.metric = null;
        } else if (metricName.matches("[Pp][0-9]+(\\.[0-9]+)?")) {
            tc.metric = PercentileStatistic.forQuantile(Double.parseDouble(metricName.substring(1)));
        } else if (metricName.matches("(?i)mean|average|μ")) {
            tc.metric = Statistic.mean();
        } else if (metricName.matches("(?i)sd|σ")) {
            tc.metric = Statistic.sd();
        } else if (metricName.matches("(?i)max|maximum")) {
            tc.metric = Statistic.max();
        } else if (metricName.matches("(?i)min|minimum")) {
            tc.metric = Statistic.min();
        } else if (metricName.matches("(?i)hits|samples")) {
            tc.metric = CountStatistic.instance();
        } else if (metricName.matches("(?i)errors")) {
            tc.metric = ErrorStatistic.instance();
        } else {
            try {
                Class<?> clazz = Class.forName(metricName, false, TestCase.class.getClassLoader()); // load class without initialisation
                tc.metric = (Metric) clazz.newInstance();
            }
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
                log.error("Failed to instantiate metric class \"{}\"", metricName, e);
            }
        }
        return tc;
    }

    public String getName() { return name; }

    protected double getActual() {
        return metric.getResult();
    }

    public synchronized void process(SampleResult result) {
        if (metric != null) {
            final String label = result.getSampleLabel();
            if (labelPattern.matcher(label).matches()) {
                log.trace("\"{}\" including sample \"{}\"", name, label);
                metric.increment(result.getTime(), result.isSuccessful());
            }
        }
    }

    public Outcome evaluate() {
        log.debug("Evaluating {}", name);
        if (metric == null) {
            return Outcome.skipped(name, description, METRIC_UNDEFINED, "");
        }
        if (!metric.isValid()) {
            return Outcome.skipped(name, description, NO_SAMPLES, "");
        }
        double actual = getActual();
        if (Double.isNaN(actual)) {
            return Outcome.error(name, description, ACTUAL_UNDEFINED, "");
        }
        if (Double.isNaN(threshold)) {
            return Outcome.error(name, description, THRESHOLD_UNDEFINED, "");
        }

        log.debug("Checking {} against {}", actual, threshold);
        if (comparator.compare(actual, threshold)) {
            return Outcome.success(name, description);
        }
        return Outcome.failure(name, description, "",
                String.format(FAILURE_DETAILS_FORMAT, actual, threshold, labelPattern.pattern().trim()));
    }

}
