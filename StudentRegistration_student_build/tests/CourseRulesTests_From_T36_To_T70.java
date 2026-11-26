
import org.junit.jupiter.api.Test;
import registration.Student;
import registration.Course;
import registration.RegistrationRules;
import registration.Registry;
import registration.Section;
import registration.MeetingTime;
import registration.Term;
import registration.RegistrationResult;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CourseRulesTests_From_T36_To_T70 {

    private Student student(Map<String, String> grades) {
        return new Student("S1", "Test Student", 3.0, 60,
                new LinkedHashMap<>(grades));
    }

    private Course course(String code, String... prereqs) {
        return new Course(code, code + " course", 3,
                Arrays.asList(prereqs));
    }

    // Helper method for registration tests
    private Registry registry() {
        Registry r = new Registry();

        r.term = new Term("Test Term",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2027, 1, 31));

        r.today = LocalDate.of(2026, 6, 1);

        r.courses.put("C1", course("C1"));
        r.courses.put("C2", course("C2"));
        r.courses.put("P1", course("P1"));

        r.sections.put("C1-01",
                new Section("C1-01", "C1", 20,
                        List.of(MeetingTime.parse("SUN 08:00-09:00")), 0));

        r.sections.put("C1-02",
                new Section("C1-02", "C1", 20,
                        List.of(MeetingTime.parse("MON 08:00-09:00")), 0));

        r.sections.put("C2-01",
                new Section("C2-01", "C2", 20,
                        List.of(MeetingTime.parse("TUE 08:00-09:00")), 0));

        r.sections.put("P1-01",
                new Section("P1-01", "P1", 20,
                        List.of(MeetingTime.parse("WED 08:00-09:00")), 0));

        r.students.put("S1", student(Map.of()));

        return r;
    }

    // =====================================================
    // R2: PREREQUISITES (T-36 to T-49)
    // =====================================================

    // Test Case: T-36
    // Verify registration is allowed when all prerequisites are completed.
    @Test
    void T36_allPrerequisitesCompleted_allowsRegistration() {
        Student s = student(Map.of("P1", "B"));

        assertTrue(RegistrationRules.hasSatisfiedPrerequisites(
                s, course("C1", "P1")));
    }

    // Test Case: T-37
    // Verify registration is rejected when one required prerequisite is missing.
    @Test
    void T37_oneRequiredPrerequisiteMissing_rejectsRegistration() {
        assertFalse(RegistrationRules.hasSatisfiedPrerequisites(
                student(Map.of()), course("C1", "P1")));
    }

    // Test Case: T-38
    // Verify registration is accepted when two required prerequisites are completed.
    @Test
    void T38_twoPrerequisitesCompleted_allowsRegistration() {
        Student s = student(Map.of("P1", "B", "P2", "D"));

        assertTrue(RegistrationRules.hasSatisfiedPrerequisites(
                s, course("C1", "P1", "P2")));
    }

    // Test Case: T-39
    // Verify registration is rejected when one of two prerequisites is incomplete.
    @Test
    void T39_oneOfTwoPrerequisitesMissing_rejectsRegistration() {
        Student s = student(Map.of("P1", "B"));

        assertFalse(RegistrationRules.hasSatisfiedPrerequisites(
                s, course("C1", "P1", "P2")));
    }

    // Test Case: T-40
    // Verify registration is allowed when the course has no prerequisites.
    @Test
    void T40_noPrerequisites_allowsRegistration() {
        assertTrue(RegistrationRules.hasSatisfiedPrerequisites(
                student(Map.of()), course("C1")));
    }

    // Test Case: T-41
    // Verify registration is rejected when the prerequisite has not been completed.
    @Test
    void T41_prerequisiteNotCompleted_rejectsRegistration() {
        assertFalse(RegistrationRules.hasSatisfiedPrerequisites(
                student(Map.of()), course("C1", "P1")));
    }

    // Test Case: T-42
    // Verify registration is allowed when prerequisites were completed before the dependent course.
    @Test
    void T42_prerequisitesCompletedBeforeDependentCourse_allowsRegistration() {
        Student s = student(Map.of("P1", "B"));

        assertTrue(RegistrationRules.hasSatisfiedPrerequisites(
                s, course("C1", "P1")));
    }

    // Test Case: T-43
    // Verify registration is rejected when the prerequisite is being taken in the same term.
    @Test
    void T43_prerequisiteTakenInSameTerm_rejectsRegistration() {
        Registry r = registry();

        assertTrue(r.register("S1", "P1-01").success);

        r.courses.put("C1", course("C1", "P1"));
        r.sections.put("C1-01",
                new Section("C1-01", "C1", 20,
                        List.of(MeetingTime.parse("THU 08:00-09:00")), 0));

        RegistrationResult result = r.register("S1", "C1-01");

        assertFalse(result.success);
    }

    // Test Case: T-44
    // Verify that prerequisite grade D is accepted.
    @Test
    void T44_prerequisiteGradeD_isAccepted() {
        assertTrue(RegistrationRules.isPassingGrade("D"));
    }

    // Test Case: T-45
    // Verify that prerequisite grade D+ is accepted.
    @Test
    void T45_prerequisiteGradeDPlus_isAccepted() {
        assertTrue(RegistrationRules.isPassingGrade("D+"));
    }

    // Test Case: T-46
    // Verify that prerequisite grade C is accepted.
    @Test
    void T46_prerequisiteGradeC_isAccepted() {
        assertTrue(RegistrationRules.isPassingGrade("C"));
    }

    // Test Case: T-47
    // Verify that prerequisite grade B is accepted.
    @Test
    void T47_prerequisiteGradeB_isAccepted() {
        assertTrue(RegistrationRules.isPassingGrade("B"));
    }

    // Test Case: T-48
    // Verify that prerequisite grade A is accepted.
    @Test
    void T48_prerequisiteGradeA_isAccepted() {
        assertTrue(RegistrationRules.isPassingGrade("A"));
    }

    // Test Case: T-49
    // Verify that prerequisite grade F is rejected.
    @Test
    void T49_prerequisiteGradeF_isRejected() {
        assertFalse(RegistrationRules.isPassingGrade("F"));
    }

    // =====================================================
    // R6: COURSE REPETITION (T-50 to T-59)
    // =====================================================

    // Test Case: T-50
    // Verify that a course previously passed with grade C cannot be repeated.
    @Test
    void T50_gradeC_blocksCourseRepetition() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "C")), "C1"));
    }

    // Test Case: T-51
    // Verify that a course previously passed with grade C+ cannot be repeated.
    @Test
    void T51_gradeCPlus_blocksCourseRepetition() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "C+")), "C1"));
    }

    // Test Case: T-52
    // Verify that a course previously passed with grade B cannot be repeated.
    @Test
    void T52_gradeB_blocksCourseRepetition() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "B")), "C1"));
    }

    // Test Case: T-53
    // Verify that a course previously passed with grade B+ cannot be repeated.
    @Test
    void T53_gradeBPlus_blocksCourseRepetition() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "B+")), "C1"));
    }

    // Test Case: T-54
    // Verify that a course previously passed with grade A cannot be repeated.
    @Test
    void T54_gradeA_blocksCourseRepetition() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "A")), "C1"));
    }

    // Test Case: T-55
    // Verify that a course previously passed with grade D can be repeated.
    @Test
    void T55_gradeD_allowsCourseRepetition() {
        assertFalse(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "D")), "C1"));
    }

    // Test Case: T-56
    // Verify that a course previously passed with grade D+ can be repeated.
    @Test
    void T56_gradeDPlus_allowsCourseRepetition() {
        assertFalse(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "D+")), "C1"));
    }

    // Test Case: T-57
    // Verify that a student with grade D can register to improve the grade.
    @Test
    void T57_gradeD_allowsGradeImprovement() {
        assertFalse(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "D")), "C1"));
    }

    // Test Case: T-58
    // Verify that a student with grade D+ can register to improve the grade.
    @Test
    void T58_gradeDPlus_allowsGradeImprovement() {
        assertFalse(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "D+")), "C1"));
    }

    // Test Case: T-59
    // Verify that a course previously passed with grade A cannot be repeated.
    @Test
    void T59_gradeA_repeatIsRejected() {
        assertTrue(RegistrationRules.isRepeatBlocked(
                student(Map.of("C1", "A")), "C1"));
    }

    // =====================================================
    // R7: DUPLICATE REGISTRATION (T-60 to T-70)
    // =====================================================

    // Test Case: T-60
    // Verify registration is accepted when the student is not registered in the course.
    @Test
    void T60_notPreviouslyRegistered_allowsRegistration() {
        assertTrue(registry().register("S1", "C1-01").success);
    }

    // Test Case: T-61
    // Verify registration in another section of the same course is rejected.
    @Test
    void T61_secondSectionOfSameCourse_isRejected() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);

        assertFalse(r.register("S1", "C1-02").success);
    }

    // Test Case: T-62
    // Verify registration in another section of an already registered course is rejected.
    @Test
    void T62_anotherSectionOfRegisteredCourse_isRejected() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);

        RegistrationResult result = r.register("S1", "C1-02");

        assertFalse(result.success);
        assertEquals("R0-DUPLICATE-COURSE", result.ruleCode);
    }

    // Test Case: T-63
    // Verify registration in a different course is accepted.
    @Test
    void T63_differentCourseRegistration_isAccepted() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);
        assertTrue(r.register("S1", "C2-01").success);
    }

    // Test Case: T-64
    // Verify that after registering in one section, a second section of the same course is rejected.
    @Test
    void T64_secondEnrollmentInSameCourse_isRejected() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);

        RegistrationResult result = r.register("S1", "C1-02");

        assertFalse(result.success);
    }

    // Test Case: T-65
    // Verify registration is accepted when the student is not registered in IS1476-01.
    @Test
    void T65_firstRegistration_isAccepted() {
        assertTrue(registry().register("S1", "C1-01").success);
    }

    // Test Case: T-66
    // Verify registering in the same section again is rejected.
    @Test
    void T66_sameSectionRegistration_isRejected() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);

        RegistrationResult result = r.register("S1", "C1-01");

        assertFalse(result.success);
        assertEquals("R0-DUPLICATE", result.ruleCode);
    }

    // Test Case: T-67
    // Verify submitting the same section registration twice is rejected.
    @Test
    void T67_duplicateSectionSubmission_isRejected() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);
        assertFalse(r.register("S1", "C1-01").success);
    }

    // Test Case: T-68
    // Verify registering again in an already registered section is rejected.
    @Test
    void T68_alreadyRegisteredSection_isRejected() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);
        assertFalse(r.register("S1", "C1-01").success);
    }

    // Test Case: T-69
    // Verify registration in IS1474-01 is accepted when the student is not registered in it.
    @Test
    void T69_notRegisteredInDifferentCourse_allowsRegistration() {
        assertTrue(registry().register("S1", "C2-01").success);
    }

    // Test Case: T-70
    // Verify both attempts to register in another section and the same section are rejected.
    @Test
    void T70_bothDuplicateRegistrationAttempts_areRejected() {
        Registry r = registry();

        assertTrue(r.register("S1", "C1-01").success);

        RegistrationResult secondSection = r.register("S1", "C1-02");
        RegistrationResult sameSection = r.register("S1", "C1-01");

        assertFalse(secondSection.success);
        assertFalse(sameSection.success);
    }
}
