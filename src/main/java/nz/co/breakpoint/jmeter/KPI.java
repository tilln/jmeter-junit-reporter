package nz.co.breakpoint.jmeter;

import org.apache.jmeter.testelement.AbstractTestElement;

/** Represents an entry in the list of KPIs to evaluate.
 */
public class KPI extends AbstractTestElement {
    /* These appear in JMeter jmx file, so don't change! */
    public static final String
            METRIC = "metric",
            LABEL = "label",
            COMPARATOR = "comparator",
            THRESHOLD = "threshold";

    public String getMetric() { return getPropertyAsString(METRIC); }

    public void setMetric(String metric) { setProperty(METRIC, metric); }

    public String getLabel() { return getPropertyAsString(LABEL); }

    public void setLabel(String label) { setProperty(LABEL, label); }

    public String getComparator() { return getPropertyAsString(COMPARATOR); }

    public void setComparator(String comparator) { setProperty(COMPARATOR, comparator); }

    public String getThreshold() { return getPropertyAsString(THRESHOLD); }

    public void setThreshold(String threshold) { setProperty(THRESHOLD, threshold); }

    @Override
    public String toString() {
        return String.format("\"%s\": %s(%s) %s %s", getName(), getMetric(), getLabel(), getComparator(), getThreshold());
    }
}
