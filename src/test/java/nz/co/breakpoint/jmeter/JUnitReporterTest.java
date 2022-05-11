package nz.co.breakpoint.jmeter;

import java.util.Collections;
import java.util.List;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JUnitReporterTest {
    @ClassRule
    public static final JMeterPropertiesResource props = new JMeterPropertiesResource();

    JUnitReporter instance = new JUnitReporter();

    @Test
    public void shouldReportTestCaseExceptions() {
        instance.testCases = Collections.singletonList(new TestCase("Error") {
            @Override
            public Outcome evaluate() {
                throw new RuntimeException("TestCaseException");
            }
        });
        List<Outcome> outcomes = instance.evaluateTestCases();
        assertEquals(1, outcomes.size());
        assertEquals(Outcome.Status.ERROR, outcomes.get(0).getStatus());
        assertEquals("TestCaseException", outcomes.get(0).getMessage());
        assertTrue(outcomes.get(0).getDetails().startsWith("java.lang.RuntimeException: TestCaseException"));
    }
}
