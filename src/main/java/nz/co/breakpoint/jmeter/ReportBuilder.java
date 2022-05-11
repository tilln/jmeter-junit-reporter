package nz.co.breakpoint.jmeter;

import org.apache.jmeter.services.FileServer;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class ReportBuilder implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ReportBuilder.class);

    /**
     * Generate an XML document in JUnit report format from the evaluated test cases.
     *
     * @param reportName GUI name of the JUnit Reporter
     * @param outcomes List of test case outcomes
     * @return JUnit report XML document
     */
    public static Document generateReport(String reportName, List<Outcome> outcomes) {
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error("Failed to create DocumentBuilder", e);
            return null;
        }
        Document doc = documentBuilder.newDocument();
        Element rootElement = doc.createElement("testsuite");
        doc.appendChild(rootElement);

        int failureCount = 0, errorCount = 0, skippedCount = 0;
        for (Outcome outcome : outcomes) {
            log.info("{}", outcome);
            final Element element = doc.createElement("testcase");
            rootElement.appendChild(element);
            element.setAttribute("classname", outcome.getTestCaseName());
            element.setAttribute("name", outcome.getTestCaseDescription());
            Element child = null;
            switch (outcome.getStatus()) {
                case FAILURE:
                    failureCount++;
                    child = doc.createElement("failure");
                    child.setAttribute("message", outcome.getMessage());
                    child.appendChild(doc.createTextNode(outcome.getDetails()));
                    break;
                case ERROR:
                    errorCount++;
                    child = doc.createElement("error");
                    child.setAttribute("message", outcome.getMessage());
                    child.appendChild(doc.createTextNode(outcome.getDetails()));
                    break;
                case SKIPPED:
                    skippedCount++;
                    child = doc.createElement("skipped");
                    child.setAttribute("message", outcome.getMessage());
                    break;
                default:
                    break;
            }
            if (child != null) element.appendChild(child);
        }
        rootElement.setAttribute("name", reportName);
        rootElement.setAttribute("tests", String.valueOf(outcomes.size()));
        rootElement.setAttribute("failures", String.valueOf(failureCount));
        rootElement.setAttribute("errors", String.valueOf(errorCount));
        rootElement.setAttribute("skipped", String.valueOf(skippedCount));
        return doc;
    }

    /**
     * Write XML document to a file. File gets overwritten if it already exists.
     *
     * @param report   XML document to write
     * @param filename name of XML output file (path relative to current directory)
     */
    protected static void writeReportFile(Document report, String filename) {
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
        } catch (TransformerConfigurationException e) {
            log.error("Failed to create XML Transformer", e);
            return;
        }
        log.info("Writing JUnit XML file {}", filename);
        try (FileOutputStream output = new FileOutputStream(filename)) {
            try {
                transformer.transform(new DOMSource(report), new StreamResult(output));
            } catch (TransformerException e) {
                log.error("Failed to generate XML output", e);
            }
        } catch (IOException e) {
            log.error("Failed to write output file", e);
        }
    }
}