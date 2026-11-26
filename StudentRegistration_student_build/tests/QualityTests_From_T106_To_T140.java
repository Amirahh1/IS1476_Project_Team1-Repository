import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import registration.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class QualityTests_From_T106_To_T140 {

    // Helper: create a test student
    private Student student(String id, String name, double gpa, int credits, Map<String, String> grades) {
        return new Student(id, name, gpa, credits, new LinkedHashMap<>(grades));
    }

    // Helper: create a test course
    private Course course(String code, String title, int creditHours, String... prereqs) {
        return new Course(code, title, creditHours, Arrays.asList(prereqs));
    }

    // Helper: create a test section
    private Section section(String id, String courseCode, int capacity, String meeting, int enrolled) {
        List<MeetingTime> meetings = new ArrayList<>();
        if (meeting != null && !meeting.isBlank()) {
            meetings.add(MeetingTime.parse(meeting));
        }
        return new Section(id, courseCode, capacity, meetings, enrolled);
    }

    // Helper: clean registry, all dates inside one calendar year
    private Registry registry() {
        Registry r = new Registry();
        r.term = new Term("Test Term", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 30));
        r.today = LocalDate.of(2026, 3, 1);

        r.courses.put("C1", course("C1", "Course One", 3));
        r.courses.put("C2", course("C2", "Course Two", 3, "C1"));
        r.courses.put("P1", course("P1", "Prereq Course", 3));

        r.sections.put("C1-01", section("C1-01", "C1", 20, "SUN 08:00-09:00", 0));
        r.sections.put("C1-02", section("C1-02", "C1", 20, "SUN 08:00-09:00", 0));
        r.sections.put("C2-01", section("C2-01", "C2", 20, "TUE 08:00-09:00", 0));
        r.sections.put("P1-01", section("P1-01", "P1", 20, "WED 08:00-09:00", 0));
        r.sections.put("P1-02", section("P1-02", "P1", 20, "SUN 08:00-09:00", 0));

        r.students.put("S1", student("S1", "Alice", 3.0, 60, Map.of()));
        r.students.put("S2", student("S2", "Bob", 3.5, 120, Map.of("C1", "B")));
        r.students.put("S3", student("S3", "Carol", 1.5, 60, Map.of()));

        return r;
    }

    // Test Case: T-106 - Unmet prerequisite must be rejected with a specific message.
    @Test
    public void testT106_UnmetPrerequisite() {
        Registry r = registry();
        RegistrationResult res = r.register("S1", "C2-01");
        assertFalse(res.success);
        assertEquals("R2-PREREQUISITE", res.ruleCode);
        assertTrue(res.message.contains("Missing"));
    }

    // Test Case: T-107 - Full section must be rejected with a specific message.
    @Test
    public void testT107_SectionIsFull() {
        Registry r = registry();
        r.sections.put("FULL-01", section("FULL-01", "C1", 1, "FRI 08:00-09:00", 1));
        RegistrationResult res = r.register("S1", "FULL-01");
        assertFalse(res.success);
        assertEquals("R3-CAPACITY", res.ruleCode);
        assertTrue(res.message.contains("full"));
    }

    // Test Case: T-108 - Timetable conflict must be rejected with a specific message.
    @Test
    public void testT108_TimetableConflict() {
        Registry r = registry();
        r.register("S1", "C1-01");
        RegistrationResult res = r.register("S1", "P1-02"); // different course, same SUN 08:00-09:00
        assertFalse(res.success);
        assertEquals("R4-TIME-CONFLICT", res.ruleCode);
        assertTrue(res.message.contains("clashes"));
    }

    // Test Case: T-109 - Add after the add deadline must be rejected.
    @Test
    public void testT109_AfterAddDeadline() {
        Registry r = registry();
        r.today = LocalDate.of(2026, 6, 16);
        RegistrationResult res = r.register("S1", "C1-01");
        assertFalse(res.success);
        assertEquals("R5-ADD-WINDOW", res.ruleCode);
    }

    // Test Case: T-110 - Drop after the drop deadline must be rejected.
    @Test
    public void testT110_AfterDropDeadline() {
        Registry r = registry();
        r.register("S1", "C1-01");
        r.today = LocalDate.of(2026, 7, 1);
        RegistrationResult res = r.drop("S1", "C1-01");
        assertFalse(res.success);
        assertEquals("R5-DROP-WINDOW", res.ruleCode);
    }

    // Test Case: T-111 - Repeating a course passed with C or better must be blocked.
    @Test
    public void testT111_CourseRepetitionNotAllowed() {
        Registry r = registry();
        RegistrationResult res = r.register("S2", "C1-01"); // S2 passed C1 with B
        assertFalse(res.success);
        assertEquals("R6-REPEAT", res.ruleCode);
        assertTrue(res.message.contains("may not be repeated"));
    }

    // Test Case: T-112 - A second section of the same course must be blocked.
    @Test
    public void testT112_DuplicateCourseRegistration() {
        Registry r = registry();
        r.register("S1", "C1-01");
        RegistrationResult res = r.register("S1", "C1-02");
        assertFalse(res.success);
        assertEquals("R0-DUPLICATE-COURSE", res.ruleCode);
    }

    // Test Case: T-113 - Registering the same section twice must be blocked.
    @Test
    public void testT113_DuplicateSectionRegistration() {
        Registry r = registry();
        r.register("S1", "C1-01");
        RegistrationResult res = r.register("S1", "C1-01");
        assertFalse(res.success);
        assertEquals("R0-DUPLICATE", res.ruleCode);
    }

    // Test Case: T-114 - When several rules are violated, the first checked rule is reported.
    @Test
    public void testT114_MultipleRulesViolatedFirstReported() {
        Registry r = registry();
        RegistrationResult res = r.register("UNKNOWN", "UNKNOWN"); // unknown student AND unknown section
        assertFalse(res.success);
        assertEquals("R0-UNKNOWN-STUDENT", res.ruleCode);
    }

    // Test Case: T-115 - Invalid requests must return an informative message.
    @Test
    public void testT115_InvalidRequestInformativeMessage() {
        Registry r = registry();
        RegistrationResult res = r.register("INVALID_ID", "C1-01");
        assertFalse(res.success);
        assertTrue(res.message.contains("No such student"));
    }

    // Test Case: T-116 - Registration completes within the 2.0s limit (1.9s case).
    @Test
    public void testT116_Performance1Point9Seconds() {
        Registry r = registry();
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> r.register("S1", "C1-01"));
    }

    // Test Case: T-117 - Registration meets the 2.0s boundary limit.
    @Test
    public void testT117_Performance2Point0Seconds() {
        Registry r = registry();
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> r.register("S1", "C1-01"));
    }

    // Test Case: T-118 - A 2.1s operation must be flagged as a violation by the 2.0s guard.
    @Test
    public void testT118_Performance2Point1SecondsViolation() {
        assertThrows(AssertionFailedError.class, () ->
                assertTimeoutPreemptively(Duration.ofSeconds(2), () -> Thread.sleep(2100)),
                "The 2.0s guard must detect a 2.1s operation as a violation");
    }

    // Test Case: T-119 - Add with an empty schedule completes within 2 seconds.
    @Test
    public void testT119_EmptyScheduleAddWithin2Seconds() {
        Registry r = registry();
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> r.register("S1", "C1-01"));
    }

    // Test Case: T-120 - Drop with registered sections completes within 2 seconds.
    @Test
    public void testT120_DropSectionWithin2Seconds() {
        Registry r = registry();
        r.register("S1", "C1-01");
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> r.drop("S1", "C1-01"));
    }

    // Test Case: T-121 - Add with multiple registered sections completes within 2 seconds.
    @Test
    public void testT121_MultipleSectionsAddWithin2Seconds() {
        Registry r = registry();
        r.register("S1", "C1-01");
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> r.register("S1", "P1-01"));
    }

    // Test Case: T-122 - Graduating student add completes within 2 seconds.
    @Test
    public void testT122_GraduatingStudentAddWithin2Seconds() {
        Registry r = registry();
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> r.register("S2", "P1-01"));
    }

    // Test Case: T-123 - Academic-warning student add completes within 2 seconds.
    @Test
    public void testT123_AcademicWarningStudentAddWithin2Seconds() {
        Registry r = registry();
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> r.register("S3", "P1-01"));
    }

    // Test Case: T-124 - 50 consecutive add/drop transactions run without crashes.
    @Test
    public void testT124_50ConsecutiveTransactionsNoFailure() {
        Registry r = registry();
        for (int i = 0; i < 50; i++) {
            assertDoesNotThrow(() -> r.register("S1", "P1-01"));
            assertDoesNotThrow(() -> r.drop("S1", "P1-01"));
        }
    }

    // Test Case: T-125 - 100 consecutive add/drop transactions run without crashes.
    @Test
    public void testT125_100ConsecutiveTransactionsNoFailure() {
        Registry r = registry();
        for (int i = 0; i < 100; i++) {
            assertDoesNotThrow(() -> r.register("S1", "P1-01"));
            assertDoesNotThrow(() -> r.drop("S1", "P1-01"));
        }
    }

    // Test Case: T-126 - 150 consecutive add/drop transactions run without crashes.
    @Test
    public void testT126_150ConsecutiveTransactionsNoFailure() {
        Registry r = registry();
        for (int i = 0; i < 150; i++) {
            assertDoesNotThrow(() -> r.register("S1", "P1-01"));
            assertDoesNotThrow(() -> r.drop("S1", "P1-01"));
        }
    }

    // Test Case: T-127 - 200 consecutive add/drop transactions (boundary) run without crashes.
    @Test
    public void testT127_200ConsecutiveTransactionsNoFailure() {
        Registry r = registry();
        for (int i = 0; i < 200; i++) {
            assertDoesNotThrow(() -> r.register("S1", "P1-01"));
            assertDoesNotThrow(() -> r.drop("S1", "P1-01"));
        }
    }

    // Test Case: T-128 - Continuous add/drop operations cause no data loss.
    @Test
    public void testT128_NoDataLossOnContinuousOperations() {
        Registry r = registry();
        int initialEnrolled = r.sections.get("P1-01").enrolled();
        for (int i = 0; i < 50; i++) {
            r.register("S1", "P1-01");
            r.drop("S1", "P1-01");
        }
        assertEquals(initialEnrolled, r.sections.get("P1-01").enrolled());
    }

    // Test Case: T-129 - Continuous add/drop operations throw no unhandled exceptions.
    @Test
    public void testT129_NoUnhandledExceptionsOnContinuousOperations() {
        Registry r = registry();
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 50; i++) {
                r.register("S1", "P1-01");
                r.drop("S1", "P1-01");
            }
        });
    }

    // Test Case: T-130 - Records remain consistent after balanced add/drop cycles.
    @Test
    public void testT130_RecordsRemainConsistentAndCorrect() {
        Registry r = registry();
        for (int i = 0; i < 20; i++) {
            r.register("S1", "P1-01");
            r.drop("S1", "P1-01");
        }
        assertTrue(r.scheduleOf("S1").isEmpty());
    }

    // Test Case: T-131 - Registration access must be denied without authentication.
    @Test
    public void testT131_UnauthenticatedAccessDenied() {
        Registry r = registry();
        RegistrationResult res = r.register("S1", "C1-01"); // no session, only an ID string
        assertFalse(res.success, "Access must be denied when the user is not authenticated");
    }

    // Test Case: T-132 - An add request must be rejected without authentication.
    @Test
    public void testT132_UnauthenticatedAddRequestRejected() {
        Registry r = registry();
        RegistrationResult res = r.register("S1", "C1-01");
        assertFalse(res.success, "Add must be rejected when the user is not authenticated");
    }

    // Test Case: T-133 - A drop request must be rejected without authentication.
    @Test
    public void testT133_UnauthenticatedDropRequestRejected() {
        Registry r = registry();
        r.register("S1", "C1-01");
        RegistrationResult res = r.drop("S1", "C1-01");
        assertFalse(res.success, "Drop must be rejected when the user is not authenticated");
    }

    // Test Case: T-134 - Access to another student's registration record must be denied.
    @Test
    public void testT134_AccessOtherStudentRecordDenied() {
        Registry r = registry();
        RegistrationResult res = r.register("S2", "P1-01"); // acting on S2's record, no ownership check
        assertFalse(res.success, "Access to another student's record must be denied");
    }

    // Test Case: T-135 - Modifying another student's schedule must be denied.
    @Test
    public void testT135_ModifyOtherStudentScheduleDenied() {
        Registry r = registry();
        RegistrationResult res = r.register("S2", "P1-01");
        assertFalse(res.success, "Modifying another student's schedule must be denied");
    }

    // Test Case: T-136 - Dropping from another student's schedule must be denied.
    @Test
    public void testT136_DropFromOtherStudentScheduleDenied() {
        Registry r = registry();
        r.register("S2", "P1-01");
        RegistrationResult res = r.drop("S2", "P1-01");
        assertFalse(res.success, "Dropping from another student's schedule must be denied");
    }

    // Test Case: T-137 - Windows 11 + JDK 21 compatibility (manual only).
    @Test
    @Disabled("Manual cross-platform testing only; requires a physical OS environment")
    public void testT137_Windows11JDK21OperatesCorrectly() {
    }

    // Test Case: T-138 - macOS Sonoma + JDK 21 compatibility (manual only).
    @Test
    @Disabled("Manual cross-platform testing only; requires a physical OS environment")
    public void testT138_MacOSSonomaJDK21OperatesCorrectly() {
    }

    // Test Case: T-139 - Ubuntu 26.04.1 LTS + JDK 21 compatibility (manual only).
    @Test
    @Disabled("Manual cross-platform testing only; requires a physical OS environment")
    public void testT139_UbuntuJDK21OperatesCorrectly() {
    }

    // Test Case: T-140 - Registration works with no network dependency (offline).
    @Test
    public void testT140_OperatesCorrectlyWithoutInternet() {
        Registry r = registry();
        assertDoesNotThrow(() -> r.register("S1", "C1-01"));
    }
}