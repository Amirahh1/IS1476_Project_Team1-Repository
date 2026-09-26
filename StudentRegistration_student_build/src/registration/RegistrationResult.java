package registration;

/** The outcome of a registration or drop attempt. */
public class RegistrationResult {

    public final boolean success;
    public final String ruleCode;     // which rule rejected the request, or "OK"
    public final String message;      // wording shown to the student

    private RegistrationResult(boolean success, String ruleCode, String message) {
        this.success = success;
        this.ruleCode = ruleCode;
        this.message = message;
    }

    public static RegistrationResult ok(String message) {
        return new RegistrationResult(true, "OK", message);
    }

    public static RegistrationResult rejected(String ruleCode, String message) {
        return new RegistrationResult(false, ruleCode, message);
    }

    @Override
    public String toString() {
        return (success ? "OK" : "REJECTED " + ruleCode) + ": " + message;
    }
}
