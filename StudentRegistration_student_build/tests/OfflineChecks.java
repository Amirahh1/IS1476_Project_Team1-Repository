import registration.MeetingTime;
import registration.RegistrationRules;

/**
 * A dependency-free way to run a few checks before JUnit is set up, so that you are
 * never blocked by a missing jar. Replace this with real JUnit tests as soon as you can.
 *
 * Compile:  javac -cp out -d out-tests tests/OfflineChecks.java
 * Run:      java -cp out:out-tests OfflineChecks
 */
public class OfflineChecks {

    private static int failures = 0;

    private static void expect(String description, boolean condition) {
        System.out.printf("  %-6s %s%n", condition ? "ok" : "FAILED", description);
        if (!condition) {
            failures++;
        }
    }

    public static void main(String[] args) {
        expect("sessions on different days do not clash",
                !MeetingTime.parse("SUN 09:00-09:50").overlaps(MeetingTime.parse("MON 09:00-09:50")));

        expect("sessions that overlap by ten minutes do clash",
                MeetingTime.parse("SUN 09:00-09:50").overlaps(MeetingTime.parse("SUN 09:40-10:30")));

        expect("a D grade is a pass", RegistrationRules.isPassingGrade("D"));

        expect("an F grade is not a pass", !RegistrationRules.isPassingGrade("F"));

        System.out.println();
        System.out.println(failures == 0 ? "All checks passed." : failures + " check(s) failed.");
    }
}
