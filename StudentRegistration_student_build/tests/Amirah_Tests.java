import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import registration.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

// Test Cases T-106 to T-140: JUnit validates backend Error Messages, Performance, Reliability, and offline logic.
// Excluded: Web UI display, session Security (IDOR), real-world load, and cross-OS Compatibility require manual/integration testing.

public class Amirah_Tests {

    private Registry registry;

    @BeforeEach
    void setUp() {
        registry = new Registry();
        
        registry.term = new Term("First Semester", LocalDate.of(2026, 8, 30), LocalDate.of(2026, 9, 3), LocalDate.of(2026, 11, 12));
        registry.today = LocalDate.of(2026, 9, 1);

        Course is1476 = new Course("IS1476", "Software Quality", 3, List.of("IS1241"));
        Course is1489 = new Course("IS1489", "Info Security", 3, List.of("IS1241"));
        Course is1330 = new Course("IS1330", "Data Networks", 3, List.of());
        Course is1220 = new Course("IS1220", "Databases", 3, List.of("IS1241", "IS1242")); 
        
        registry.courses.put("IS1476", is1476);
        registry.courses.put("IS1489", is1489);
        registry.courses.put("IS1330", is1330);
        registry.courses.put("IS1220", is1220);

        Section sec1476 = new Section("IS1476-01", "IS1476", 30, List.of(MeetingTime.parse("SUN 08:00-09:20")), 19);
        Section sec1489 = new Section("IS1489-01", "IS1489", 30, List.of(MeetingTime.parse("MON 10:00-11:20")), 20);
        Section sec1330 = new Section("IS1330-01", "IS1330", 30, List.of(MeetingTime.parse("THU 08:00-11:20")), 20);
        Section sec1220 = new Section("IS1220-01", "IS1220", 30, List.of(MeetingTime.parse("TUE 10:00-11:20")), 20);

        registry.sections.put("IS1476-01", sec1476);
        registry.sections.put("IS1489-01", sec1489);
        registry.sections.put("IS1330-01", sec1330);
        registry.sections.put("IS1220-01", sec1220);

        Map<String, String> nawafHistory = new LinkedHashMap<>();
        nawafHistory.put("IS1241", "B");
        registry.students.put("440101", new Student("440101", "Nawaf", 3.42, 119, nawafHistory));

        Map<String, String> saraHistory = new LinkedHashMap<>();
        saraHistory.put("IS1241", "A");
        registry.students.put("440102", new Student("440102", "Sara", 3.05, 92, saraHistory));

        registry.students.put("440103", new Student("440103", "Turki", 1.85, 78, new LinkedHashMap<>()));

        Map<String, String> lamaHistory = new LinkedHashMap<>();
        lamaHistory.put("IS1330", "C");
        registry.students.put("440106", new Student("440106", "Lama", 2.98, 110, lamaHistory));

        Map<String, String> abdullahHistory = new LinkedHashMap<>();
        abdullahHistory.put("IS1241", "B"); 
        registry.students.put("440105", new Student("440105", "Abdullah", 2.64, 71, abdullahHistory));
    }

    // T-106: Prerequisite failure
    @Test
    void T106_specificPrerequisiteErrorMessage() {
        RegistrationResult result = registry.register("440103", "IS1489-01");
        assertFalse(result.success, "Registration should fail due to missing prerequisite");
        assertTrue(result.message.contains("Prerequisite"), "Registry should identify missing prerequisite");
    }

    // T-106b: THE BUG TEST (Abdullah Al-Ghamdi case)
    @Test
    void T106b_incompletePrerequisitesAccepted_BUG() {
        RegistrationResult result = registry.register("440105", "IS1220-01");
        assertFalse(result.success, "Registration should fail because IS1242 is missing");
    }

    // T-107: Full section
    @Test
    void T107_fullSectionRejection() {
        Section fullSec = new Section("IS1476-02", "IS1476", 30, List.of(), 30);
        registry.sections.put("IS1476-02", fullSec);
        
        RegistrationResult result = registry.register("440102", "IS1476-02");
        assertFalse(result.success);
        assertTrue(result.message.contains("full") || result.message.contains("R3-CAPACITY"));
    }

    // T-108: Timetable conflict
    @Test
    void T108_timetableConflict() {
        registry.register("440101", "IS1476-01");
        
        Section conflictSec = new Section("IS1489-02", "IS1489", 30, List.of(MeetingTime.parse("SUN 08:00-09:20")), 10);
        registry.courses.put("IS1489", new Course("IS1489", "Info Security", 3, List.of()));
        registry.sections.put("IS1489-02", conflictSec);
        
        RegistrationResult conflictReg = registry.register("440101", "IS1489-02");
        assertFalse(conflictReg.success, "Registration should fail due to conflict");
        assertTrue(conflictReg.message.contains("clashes") || conflictReg.ruleCode.equals("R4-TIME-CONFLICT"));
    }

    // T-109: Add Deadline
    @Test
    void T109_addDeadlineViolation() {
        registry.today = LocalDate.of(2026, 9, 5);
        
        RegistrationResult result = registry.register("440101", "IS1476-01");
        assertFalse(result.success, "Should reject add after deadline");
        assertTrue(result.ruleCode.equals("R5-ADD-WINDOW"));
    }

    // T-110: Drop Deadline
    @Test
    void T110_dropDeadlineViolation() {
        registry.register("440101", "IS1476-01");
        registry.today = LocalDate.of(2026, 11, 14);
        
        RegistrationResult result = registry.drop("440101", "IS1476-01");
        assertFalse(result.success, "Should reject drop after deadline");
        assertTrue(result.ruleCode.equals("R5-DROP-WINDOW"));
    }

    // T-111: Course Repetition
    @Test
    void T111_courseRepetitionBlocked() {
        RegistrationResult result = registry.register("440106", "IS1330-01");
        assertFalse(result.success, "Should block repetition of course passed with C");
        assertTrue(result.message.contains("R6-REPEAT") || result.message.contains("passed"));
    }

    // T-112 & T-113: Duplicate Course/Section
    @Test
    void T112_duplicateCourseBlocked() {
        registry.register("440101", "IS1476-01");
        
        Section sec2 = new Section("IS1476-02", "IS1476", 30, List.of(MeetingTime.parse("MON 10:00-11:20")), 10);
        registry.sections.put("IS1476-02", sec2);
        
        RegistrationResult result = registry.register("440101", "IS1476-02");
        assertFalse(result.success, "Should block duplicate course registration");
    }

    // T-116: Performance check
    @Test
    void T116_performanceCheck() {
        assertTimeout(Duration.ofSeconds(2), () -> {
            registry.register("440101", "IS1476-01");
        });
    }

    // T-127: 200 consecutive transactions
    @Test
    void T127_twoHundredConsecutiveTransactions() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 200; i++) {
                Student temp = new Student("TEMP" + i, "Temp", 3.0, 0, new LinkedHashMap<>());
                registry.students.put("TEMP" + i, temp);
                RegistrationRules.hasSatisfiedPrerequisites(temp, registry.courses.get("IS1476"));
            }
        });
    }

    // T-128: No data loss
    @Test
    void T128_noDataLoss() {
        Student nawaf = registry.students.get("440101");
        int originalCredits = nawaf.creditsCompleted;
        
        for (int i = 0; i < 100; i++) {
            RegistrationRules.hasSatisfiedPrerequisites(nawaf, registry.courses.get("IS1476"));
        }
        
        assertEquals(originalCredits, nawaf.creditsCompleted, "Credits should not change during read-only checks");
    }

    // T-131: No authentication required
    @Test
    void T131_noAuthenticationRequired() {
        RegistrationResult result = registry.register("440101", "IS1476-01");
        assertNotNull(result, "Registry processes request without any auth check");
    }

    // T-134: IDOR vulnerability
    @Test
    void T134_idorVulnerability() {
        Student sara = registry.students.get("440102");
        assertNotNull(sara, "Can access Sara's object directly without being Sara");
        
        registry.sectionIdsFor("440102").add("IS1476-01");
        assertTrue(registry.sectionIdsFor("440102").contains("IS1476-01"), "Successfully modified Sara's schedule directly");
    }

    // T-140: Offline operation
    @Test
    void T140_offlineOperation() {
        assertDoesNotThrow(() -> {
            registry.register("440101", "IS1476-01");
        });
    }
}