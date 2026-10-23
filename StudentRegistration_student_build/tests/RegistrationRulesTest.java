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
    //// ========================================================
    // Student 3 Scope: TC-71 to TC-105
    // Requirements: R6.1, R6.2, R7.1, R7.2, N1, N2
    // ========================================================

    // TC-71: Block registration for IS1476 passed with grade A
    @Test
    void testCourseRepetition_IS1476_GradeA() {
        // Add the implementation
    }

    // TC-72: Block registration for IS1476 passed with grade B+
    @Test
    void testCourseRepetition_IS1476_GradeBPlus() {
        // Add the implementation
    }

    // TC-73: Block registration for IS1476 passed with grade B
    @Test
    void testCourseRepetition_IS1476_GradeB() {
        // Add the implementation
    }

    // TC-74: Block registration for IS1476 passed with grade C+
    @Test
    void testCourseRepetition_IS1476_GradeCPlus() {
        // Add the implementation
    }

    // TC-75: Block registration for IS1476 passed with grade C
    @Test
    void testCourseRepetition_IS1476_GradeC() {
        // Add the implementation
    }

    // TC-76: Allow registration for IS1476 passed with grade D+ to improve
    @Test
    void testCourseRepetition_IS1476_GradeDPlus() {
        // Add the implementation
    }

    // TC-77: Allow registration for IS1476 passed with grade D to improve
    @Test
    void testCourseRepetition_IS1476_GradeD() {
        // Add the implementation
    }

    // TC-78: Allow registration for IS1476 failed with grade F
    @Test
    void testCourseRepetition_IS1476_GradeF() {
        // Add the implementation
    }

    // TC-79: Block registration for IS1474 passed with grade A
    @Test
    void testCourseRepetition_IS1474_GradeA() {
        // Add the implementation
    }

    // TC-80: Block registration for IS1474 passed with grade B
    @Test
    void testCourseRepetition_IS1474_GradeB() {
        // Add the implementation
    }

    // TC-81: Block registration for IS1474 passed with grade C
    @Test
    void testCourseRepetition_IS1474_GradeC() {
        // Add the implementation
    }

    // TC-82: Allow registration for IS1474 passed with grade D
    @Test
    void testCourseRepetition_IS1474_GradeD() {
        // Add the implementation
    }

    // TC-83: Block registration for IS1470 passed with grade A
    @Test
    void testCourseRepetition_IS1470_GradeA() {
        // Add the implementation
    }

    // TC-84: Allow registration for IS1470 passed with grade D+
    @Test
    void testCourseRepetition_IS1470_GradeDPlus() {
        // Add the implementation
    }

    // TC-85: Block registering in two sections of IS1476 (sec 01 & sec 02)
    @Test
    void testDuplicateRegistration_IS1476_Sec01_Sec02() {
        // Add the implementation
    }

    // TC-86: Block registering in two sections of IS1476 (sec 02 & sec 01)
    @Test
    void testDuplicateRegistration_IS1476_Sec02_Sec01() {
        // Add the implementation
    }

    // TC-87: Block registering in two sections of IS1476 (sec 03 & sec 01)
    @Test
    void testDuplicateRegistration_IS1476_Sec03_Sec01() {
        // Add the implementation
    }

    // TC-88: Block submitting section 01 again for IS1476
    @Test
    void testDuplicateSectionSubmit_IS1476_Sec01() {
        // Add the implementation
    }

    // TC-89: Block submitting section 02 again for IS1476
    @Test
    void testDuplicateSectionSubmit_IS1476_Sec02() {
        // Add the implementation
    }

    // TC-90: Block submitting section 01 again for IS1474
    @Test
    void testDuplicateSectionSubmit_IS1474_Sec01() {
        // Add the implementation
    }

    // TC-91: Block registering in two sections of IS1474 (sec 01 & sec 02)
    @Test
    void testDuplicateRegistration_IS1474_Sec01_Sec02() {
        // Add the implementation
    }

    // TC-92: Block registering in two sections of IS1474 (sec 02 & sec 03)
    @Test
    void testDuplicateRegistration_IS1474_Sec02_Sec03() {
        // Add the implementation
    }

    // TC-93: Allow initial registration for IS1476 section 01
    @Test
    void testInitialRegistration_IS1476() {
        // Add the implementation
    }

    // TC-94: Allow initial registration for IS1474 section 01
    @Test
    void testInitialRegistration_IS1474() {
        // Add the implementation
    }

    // TC-95: Allow initial registration for IS1470 section 01
    @Test
    void testInitialRegistration_IS1470() {
        // Add the implementation
    }

    // TC-96: Show actionable error message when prerequisite is missing
    @Test
    void testErrorMessage_PrerequisiteMissing() {
        // Add the implementation
    }

    // TC-97: Show actionable error message when course is full
    @Test
    void testErrorMessage_CourseFull() {
        // Add the implementation
    }

    // TC-98: Show actionable error message on schedule conflict
    @Test
    void testErrorMessage_ScheduleConflict() {
        // Add the implementation
    }

    // TC-99: Show actionable error message when credit limit exceeded
    @Test
    void testErrorMessage_CreditLimitExceeded() {
        // Add the implementation
    }

    // TC-100: Show actionable error message on duplicate section attempt
    @Test
    void testErrorMessage_DuplicateSection() {
        // Add the implementation
    }

    // TC-101: Verify system responds within 2 seconds under standard load
    @Test
    void testPerformance_StandardLoad() {
        // Add the implementation
    }

    // TC-102: Verify system responds within 2 seconds under high load
    @Test
    void testPerformance_HighLoad() {
        // Add the implementation
    }

    // TC-103: Verify drop course response time is within 2 seconds
    @Test
    void testPerformance_DropRequest() {
        // Add the implementation
    }

    // TC-104: Verify search sections response time is within 2 seconds
    @Test
    void testPerformance_SearchSections() {
        // Add the implementation
    }

    // TC-105: Verify system responds within 2 seconds under maximum users
    @Test
    void testPerformance_MaxUsers() {
        // Add the implementation
    }
    // Before writing any more, derive your test conditions from the requirements
    // specification, not from this file. Ask of each rule: what are the equivalence
    // classes, and where exactly are the boundaries?
}
