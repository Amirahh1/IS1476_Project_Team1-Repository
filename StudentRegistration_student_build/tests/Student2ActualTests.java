import org.junit.jupiter.api.Test;
import registration.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class Student2ActualTests {

    private Student student(Map<String, String> grades) {
        return new Student("S1", "Test Student", 3.0, 60,
                new LinkedHashMap<>(grades));
    }

    private Course course(String code, String... prereqs) {
        return new Course(code, code + " course", 3,
                Arrays.asList(prereqs));
    }

    // T-36 to T-50: R2 Prerequisites

    @Test
    void T36_allPrerequisitesCompleted_allowsRegistrationRule() {
        Student s = student(Map.of("P1", "B"));
        assertTrue(RegistrationRules.hasSatisfiedPrerequisites(
                s, course("C1", "P1")));
    }

    @Test
    void T37_missingPrerequisite_rejectsRegistrationRule() {
        assertFalse(RegistrationRules.hasSatisfiedPrerequisites(
                student(Map.of()), course("C1", "P1")));
    }

    @Test
    void T38_prerequisiteGradeD_isPassing() {
        assertTrue(RegistrationRules.isPassingGrade("D"));
    }

    @Test
    void T39_prerequisiteGradeDPlus_isPassing() {
        assertTrue(RegistrationRules.isPassingGrade("D+"));
    }

    @Test
    void T40_prerequisiteGradeC_isPassing() {
        assertTrue(RegistrationRules.isPassingGrade("C"));
    }

    @Test
    void T41_prerequisiteGradeB_isPassing() {
        assertTrue(RegistrationRules.isPassingGrade("B"));
    }

    @Test
    void T42_prerequisiteGradeA_isPassing() {
        assertTrue(RegistrationRules.isPassingGrade("A"));
    }

    @Test
    void T43_prerequisiteGradeF_isNotPassing() {
        assertFalse(RegistrationRules.isPassingGrade("F"));
    }

    @Test
    void T44_nullPrerequisiteGrade_isNotPassing() {
        assertFalse(RegistrationRules.isPassingGrade(null));
    }

    @Test
    void T45_noPrerequisites_areSatisfied() {
        assertTrue(RegistrationRules.hasSatisfiedPrerequisites(
                student(Map.of()), course("C1")));
    }

    @Test
    void T46_multiplePrerequisites_allCompleted_areSatisfied() {
        Student s = student(Map.of("P1", "B", "P2", "D"));
        assertTrue(RegistrationRules.hasSatisfiedPrerequisites(
                s, course("C1", "P1", "P2")));
    }

    @Test
    void T47_multiplePrerequisites_secondMissing_rejects() {
        Student s = student(Map.of("P1", "B"));
        assertFalse(RegistrationRules.hasSatisfiedPrerequisites(
                s, course("C1", "P1", "P2")));
    }

    @Test
    void T48_multiplePrerequisites_firstMissing_rejects() {
        Student s = student(Map.of("P2", "B"));
        assertFalse(RegistrationRules.hasSatisfiedPrerequisites(
                s, course("C1", "P1", "P2")));
    }

    @Test
    void T49_prerequisiteGradeLowercaseD_isAccepted() {
        assertTrue(RegistrationRules.isPassingGrade("d"));
    }

    @Test
    void T50_prerequisiteGradeWithSpaces_isAccepted() {
        assertTrue(RegistrationRules.isPassingGrade(" D "));
    }

    // T-51 to T-60: R6 Course Repetition

    @Test
    void T51_gradeC_blocksRepeat() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "C")), "C1"));
    }

    @Test
    void T52_gradeCPlus_blocksRepeat() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "C+")), "C1"));
    }

    @Test
    void T53_gradeB_blocksRepeat() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "B")), "C1"));
    }

    @Test
    void T54_gradeBPlus_blocksRepeat() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "B+")), "C1"));
    }

    @Test
    void T55_gradeA_blocksRepeat() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "A")), "C1"));
    }

    @Test
    void T56_gradeD_allowsRepeat() {
        assertFalse(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "D")), "C1"));
    }

    @Test
    void T57_gradeDPlus_allowsRepeat() {
        assertFalse(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "D+")), "C1"));
    }

    @Test
    void T58_gradeF_doesNotBlockRepeat() {
        assertFalse(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "F")), "C1"));
    }

    @Test
    void T59_courseNotPreviouslyTaken_doesNotBlockRepeat() {
        assertFalse(RegistrationRules.isRepeatBlocked(
                student(Map.of()), "C1"));
    }

    @Test
    void T60_lowercaseGradeC_blocksRepeat() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "c")), "C1"));
    }

    // Registry setup for T-61 to T-70: R7 Duplicate Registration

    private Registry registry() {
        Registry r = new Registry();

        r.term = new Term("Test Term",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2027, 1, 31));

        r.today = LocalDate.of(2026, 6, 1);

        r.courses.put("C1", course("C1"));
        r.courses.put("C2", course("C2"));

        r.sections.put("C1-01",
                new Section("C1-01", "C1", 20,
                        List.of(MeetingTime.parse("SUN 08:00-09:00")), 0));

        r.sections.put("C1-02",
                new Section("C1-02", "C1", 20,
                        List.of(MeetingTime.parse("MON 08:00-09:00")), 0));

        r.sections.put("C2-01",
                new Section("C2-01", "C2", 20,
                        List.of(MeetingTime.parse("TUE 08:00-09:00")), 0));

        r.students.put("S1", student(Map.of()));

        return r;
    }

    @Test
    void T61_firstSectionRegistration_succeeds() {
        assertTrue(registry().register("S1", "C1-01").success);
    }

    @Test
    void T62_sameSectionTwice_isRejected() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);

        RegistrationResult second = r.register("S1", "C1-01");

        assertFalse(second.success);
        assertEquals("R0-DUPLICATE", second.ruleCode);
    }

    @Test
    void T63_twoSectionsOfSameCourse_areRejected() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);

        RegistrationResult second = r.register("S1", "C1-02");

        assertFalse(second.success);
        assertEquals("R0-DUPLICATE-COURSE", second.ruleCode);
    }

    @Test
    void T64_differentCourseCanBeRegistered() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);
        assertTrue(r.register("S1", "C2-01").success);
    }

    @Test
    void T65_sameCourseDifferentSectionDoesNotAddSecondEnrollment() {
        Registry r = registry();

        r.register("S1", "C1-01");
        r.register("S1", "C1-02");

        assertEquals(1, r.sectionIdsFor("S1").size());
    }

    @Test
    void T66_duplicateSameSectionReturnsHelpfulRuleCode() {
        Registry r = registry();

        r.register("S1", "C1-01");

        assertEquals("R0-DUPLICATE",
                r.register("S1", "C1-01").ruleCode);
    }

    @Test
    void T67_duplicateCourseReturnsHelpfulRuleCode() {
        Registry r = registry();

        r.register("S1", "C1-01");

        assertEquals("R0-DUPLICATE-COURSE",
                r.register("S1", "C1-02").ruleCode);
    }

    @Test
    void T68_registrationInAnotherCourseAfterFirstIsAllowed() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);
        assertTrue(r.register("S1", "C2-01").success);
    }

    @Test
    void T69_unknownSectionIsRejected() {
        RegistrationResult result =
                registry().register("S1", "NO-SECTION");

        assertFalse(result.success);
        assertEquals("R0-UNKNOWN-SECTION", result.ruleCode);
    }

    @Test
    void T70_unknownStudentIsRejected() {
        RegistrationResult result =
                registry().register("NO-STUDENT", "C1-01");

        assertFalse(result.success);
        assertEquals("R0-UNKNOWN-STUDENT", result.ruleCode);
    }
}
