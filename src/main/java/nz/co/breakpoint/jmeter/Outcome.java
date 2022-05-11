package nz.co.breakpoint.jmeter;

/** Outcome of a TestCase evaluation and input for JUnit Report generation.
 *  Holds required data for the JUnit report element and attributes.
 *  description = name attribute
 *  message = message attribute
 *  details = inner text node
 */
public class Outcome {
    public enum Status { SUCCESS, FAILURE, ERROR, SKIPPED }

    private final Status status;
    private final String testCaseName;
    private final String description;
    private final String message;
    private final String details;

    public Outcome(Status status, String testCaseName, String description, String message, String details) {
        this.status = status;
        this.testCaseName = testCaseName;
        this.description = description;
        this.message = message;
        this.details = details;
    }

    public static Outcome success(String testCaseName, String description) {
        return new Outcome(Status.SUCCESS, testCaseName, description, "", "");
    }

    public static Outcome failure(String testCaseName, String description, String message, String details) {
        return new Outcome(Status.FAILURE, testCaseName, description, message, details);
    }

    public static Outcome error(String testCaseName, String description, String message, String details) {
        return new Outcome(Status.ERROR, testCaseName, description, message, details);
    }

    public static Outcome skipped(String testCaseName, String description, String message, String details) {
        return new Outcome(Status.SKIPPED, testCaseName, description, message, details);
    }

    public Status getStatus() { return status; }

    public String getTestCaseName() { return testCaseName; }

    public String getTestCaseDescription() { return description; }

    public String getMessage() { return message; }

    public String getDetails() { return details; }

    @Override
    public String toString() {
        return String.format("\"%s\" %s = %s", testCaseName, description, status);
    }
}
