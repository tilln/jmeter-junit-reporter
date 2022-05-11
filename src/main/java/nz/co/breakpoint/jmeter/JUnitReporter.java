package nz.co.breakpoint.jmeter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.visualizers.Visualizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JUnitReporter extends AbstractTestElement
        implements Serializable, SampleListener, TestStateListener, TestBean, Visualizer /* so it gets an entry in the Listener menu */ {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(JUnitReporter.class);

    /* These appear in JMeter jmx file, so don't change! */
    public static final String FILENAME = "filename";
    public static final String KPIS = "kpis";

    private String filename; // JUnit XML output file
    private List<KPI> kpis;

    protected transient List<TestCase> testCases;
    protected transient boolean testRunning = false;

    @Override
    public void sampleOccurred(SampleEvent sampleEvent) {
        testCases.forEach(tc -> tc.process(sampleEvent.getResult()));
    }

    @Override
    public void sampleStarted(SampleEvent sampleEvent) {
        // nothing to do
    }

    @Override
    public void sampleStopped(SampleEvent sampleEvent) {
        // nothing to do
    }

    @Override
    public void add(SampleResult sampleResult) {
        // nothing to do
    }

    @Override
    public boolean isStats() {
        return true; // do not save individual sample results (not actually used though)
    }

    @Override
    public void testStarted() {
        testStarted("");
    }

    @Override
    public void testStarted(String s) {
        testRunning = true;
    }

    @Override
    public void testEnded() {
        testEnded("");
    }

    @Override
    public void testEnded(String s) {
        testRunning = false;
        ReportBuilder.writeReportFile(
                ReportBuilder.generateReport(getName(), evaluateTestCases()),
                getFilename());
    }

    protected List<Outcome> evaluateTestCases() {
        return testCases.stream().map(testCase -> {
            try {
                return testCase.evaluate();
            } catch (Throwable e) {
                return Outcome.error(testCase.getName(), "", e.getLocalizedMessage(), ExceptionUtils.getStackTrace(e));
            }
        }).collect(Collectors.toList());
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<KPI> getKpis() {
        return kpis;
    }

    public void setKpis(List<KPI> kpis) {
        this.kpis = kpis;
        if (!testRunning) {
            log.debug("No test running. Initialising test cases from KPIs {}", kpis);
            testCases = kpis.stream().map(TestCase::fromKPI).collect(Collectors.toList());
        }
    }

    public Object clone() {
        JUnitReporter clone = (JUnitReporter) super.clone();
        clone.testRunning = this.testRunning;
        // All threads need to share accumulators, so copy test cases instead of cloning:
        clone.testCases = this.testCases;
        return clone;
    }
}
