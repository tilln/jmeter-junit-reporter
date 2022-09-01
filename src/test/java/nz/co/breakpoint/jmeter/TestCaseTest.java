package nz.co.breakpoint.jmeter;

import java.util.Arrays;

import org.apache.jmeter.samplers.SampleResult;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCaseTest {
    @ClassRule
    public static final JMeterPropertiesResource props = new JMeterPropertiesResource();

    TestCase instance;

    static final long[] timings = new long[]{ 10L, 20L, 20L, 100L };

    @Test
    public void shouldCalculateHits() {
        instance = TestCase.fromKPI("", "hits", "", "", "");
        assertEquals(0.0d, instance.getActual(), 0.0d);
        feedWithTimings(instance, timings);
        assertEquals(4.0d, instance.getActual(), 0.0d);
    }

    @Test
    public void shouldCalculateMean() {
        instance = TestCase.fromKPI("", "mean", "", "", "");
        feedWithTimings(instance, timings);
        assertEquals(37.5d, instance.getActual(), 0.0d);
    }

    @Test
    public void shouldCalculateStandardDeviation() {
        instance = TestCase.fromKPI("", "SD", "", "", "");
        feedWithTimings(instance, 10L, 10L, 10L, 10L, 11L);
        assertEquals(0.4d, instance.getActual(), 0.0d);
    }

    @Test
    public void shouldCalculateMedian() {
        instance = TestCase.fromKPI("", "P50", "", "", "");
        feedWithTimings(instance, timings);
        assertEquals(20.0d, instance.getActual(), 0.0d);
    }

    @Test
    public void shouldCalculateMaximum() {
        instance = TestCase.fromKPI("", "MAX", "", "", "");
        feedWithTimings(instance, timings);
        assertEquals(100.0d, instance.getActual(), 0.0d);
    }

    @Test
    public void shouldCalculateMinimum() {
        instance = TestCase.fromKPI("", "Min", "", "", "");
        feedWithTimings(instance, timings);
        assertEquals(10.0d, instance.getActual(), 0.0d);
    }

    @Test
    public void shouldCalculateHighPercentile() {
        instance = TestCase.fromKPI("", "P99.9", "", "", "");
        long[] timings = new long[1000];
        Arrays.setAll(timings, i -> (long)i);
        feedWithTimings(instance, timings);
        assertEquals(999.0d, instance.getActual(), 0.1d);
    }

    @Test
    public void shouldCalculateErrorRate() {
        instance = TestCase.fromKPI("", "Errors", "", "", "");
        feedWithErrors(instance, true, true, true, true, false);
        assertEquals(0.2d, instance.getActual(), 0.0d);
    }

    @Test
    public void shouldSkipUndefinedMetric() {
        instance = TestCase.fromKPI("Undefined Metric", "", ".*", ">", "100.0");
        feedWithTimings(instance, timings);
        assertEquals("Undefined metric should be skipped", Outcome.Status.SKIPPED, instance.evaluate().getStatus());
    }

    @Test
    public void shouldSkipUnknownMetric() {
        instance = TestCase.fromKPI("Unknown Metric", "UNKNOWN", ".*", ">", "100.0");
        feedWithTimings(instance, timings);
        assertEquals("Unknown metric should be skipped", Outcome.Status.SKIPPED, instance.evaluate().getStatus());
    }

    @Test
    public void shouldUseDefaultLabel() {
        instance = TestCase.fromKPI("Undefined Label", "MAX", "", ">", "100.0");
        feedWithTimings(instance, timings);
        assertEquals("Label should default to .*", Outcome.Status.FAILURE, instance.evaluate().getStatus());
    }

    @Test
    public void shouldUseDefaultComparator() {
        instance = TestCase.fromKPI("Undefined Comparator", "MAX", ".*", "", "101.0");
        feedWithTimings(instance, timings);
        assertEquals("Comparator should default to <", Outcome.Status.SUCCESS, instance.evaluate().getStatus());
    }

    @Test
    public void shouldUseDefaultThreshold() {
        instance = TestCase.fromKPI("Undefined Threshold", "MAX", ".*", ">", "");
        feedWithTimings(instance, timings);
        assertEquals("Threshold should default to 0", Outcome.Status.SUCCESS, instance.evaluate().getStatus());
    }

    @Test
    public void shouldLoadCustomMetricClass() {
        instance = TestCase.fromKPI("CustomMetric", ErrorStatistic.class.getName(), "", "<=", "0.0");
        feedWithErrors(instance, true, true); // all successful
        Outcome outcome = instance.evaluate();
        assertEquals(Outcome.Status.SUCCESS, outcome.getStatus());
        assertEquals("CustomMetric", outcome.getTestCaseName());
    }

    @Test
    public void shouldSupportDifferentComparators() {
        instance = TestCase.fromKPI("Less", "MAX", "", "<", "100.0");
        feedWithTimings(instance, 100);
        assertEquals(Outcome.Status.FAILURE, instance.evaluate().getStatus());

        instance = TestCase.fromKPI("LessOrEqual", "MAX", "", "<=", "100.0");
        feedWithTimings(instance, 100);
        assertEquals(Outcome.Status.SUCCESS, instance.evaluate().getStatus());

        instance = TestCase.fromKPI("Greater", "MAX", "", ">", "100.0");
        feedWithTimings(instance, 100);
        assertEquals(Outcome.Status.FAILURE, instance.evaluate().getStatus());

        instance = TestCase.fromKPI("GreaterOrEqual", "MAX", "", ">=", "100.0");
        feedWithTimings(instance, 100);
        assertEquals(Outcome.Status.SUCCESS, instance.evaluate().getStatus());
    }

    static void feedWithTimings(TestCase testCase, long... timings) {
        for (long elapsed : timings) {
            testCase.process(SampleResult.createTestSample(elapsed));
        }
    }

    static void feedWithErrors(TestCase testCase, boolean... successes) {
        for (boolean success : successes) {
            SampleResult result = SampleResult.createTestSample(0);
            result.setSuccessful(success);
            testCase.process(result);
        }
    }
}
