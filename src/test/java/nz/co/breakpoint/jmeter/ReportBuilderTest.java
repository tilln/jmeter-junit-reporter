package nz.co.breakpoint.jmeter;

import java.util.Arrays;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xmlunit.builder.Input;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

public class ReportBuilderTest {
    @Test
    public void shouldGenerateReport() {
        Document doc = ReportBuilder.generateReport("JMETER", Arrays.asList(
                Outcome.success("OK", "good"),
                Outcome.failure("FAIL", "bad", "oh no", "wrong"),
                Outcome.error("ERROR", "very bad", "uh oh", "whoops"),
                Outcome.skipped("SKIP", "so what", "never mind", "meh")
        ));
        assertThat(Input.from(doc),
                isIdenticalTo(Input.from(getClass().getClassLoader().getResource("TEST.xml"))).ignoreWhitespace());
    }
}
