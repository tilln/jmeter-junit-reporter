package nz.co.breakpoint.jmeter;

import java.beans.PropertyDescriptor;
import java.util.ResourceBundle;
import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.FileEditor;
import org.apache.jmeter.testbeans.gui.TableEditor;

/** Defines the GUI fields with values as defined in JUnitReporterResources.properties or localized variants.
 */
public class JUnitReporterBeanInfo extends BeanInfoSupport {
    public JUnitReporterBeanInfo() {
        super(JUnitReporter.class);

        createPropertyGroup("Filename", new String[] {JUnitReporter.FILENAME});

        PropertyDescriptor p = property(JUnitReporter.FILENAME);
        p.setPropertyEditorClass(FileEditor.class);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        createPropertyGroup("KPIs", new String[] {JUnitReporter.KPIS});

        p = property(JUnitReporter.KPIS);
        p.setPropertyEditorClass(TableEditor.class);
        p.setValue(TableEditor.CLASSNAME, KPI.class.getName());
        p.setValue(TableEditor.OBJECT_PROPERTIES,
                /* name is a standard property of TestElement, all must match KPI TestElement properties */
                new String[]{"name", KPI.METRIC, KPI.LABEL, KPI.COMPARATOR, KPI.THRESHOLD});

        ResourceBundle rb = (ResourceBundle)getBeanDescriptor().getValue(RESOURCE_BUNDLE);
        p.setValue(TableEditor.HEADERS, (rb != null && rb.containsKey("kpis.tableHeaders"))?
                rb.getString("kpis.tableHeaders").split("\\|") :
                new String[]{"Name", "Metric", "Sample Label (regex)", "Comparator", "Threshold"});
    }
}
