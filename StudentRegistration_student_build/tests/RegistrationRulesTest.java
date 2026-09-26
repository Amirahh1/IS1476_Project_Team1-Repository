import org.junit.jupiter.api.Test;
import registration.Course;
import registration.MeetingTime;
import registration.RegistrationRules;
import registration.Student;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A starting point, not a finished suite. Three tests are written for you so that
 * you can see the shape of one; the rest are yours to design.
 *
 * Compile:  javac -cp out:junit-platform-console-standalone.jar -d out-tests tests/*.java
 * Run:      java -jar junit-platform-console-standalone.jar --class-path out:out-tests --scan-class-path
 */
public class RegistrationRulesTest {

    private Student studentWith(Map<String, String> completed, double gpa, int creditsCompleted) {
        return new Student("999999", "Test Student", gpa, creditsCompleted, completed);
    }

    @Test
    void aStudentWhoHasPassedThePrerequisiteMayRegister() {
        Map<String, String> history = new LinkedHashMap<>();
        history.put("IS1235", "B");
        Course is1241 = new Course("IS1241", "Principles of Project Management", 3, List.of("IS1235"));

        assertTrue(RegistrationRules.hasSatisfiedPrerequisites(studentWith(history, 3.0, 60), is1241));
    }

    @Test
    void aStudentWhoHasNotTakenThePrerequisiteIsRefused() {
        Course is1241 = new Course("IS1241", "Principles of Project Management", 3, List.of("IS1235"));

        assertFalse(RegistrationRules.hasSatisfiedPrerequisites(
                studentWith(new LinkedHashMap<>(), 3.0, 60), is1241));
    }

    @Test
    void sectionsOnDifferentDaysDoNotClash() {
        MeetingTime sunday = MeetingTime.parse("SUN 09:00-09:50");
        MeetingTime monday = MeetingTime.parse("MON 09:00-09:50");

        assertFalse(sunday.overlaps(monday));
    }

    // Your tests start here.
    //
    // Before writing any more, derive your test conditions from the requirements
    // specification, not from this file. Ask of each rule: what are the equivalence
    // classes, and where exactly are the boundaries?
}
