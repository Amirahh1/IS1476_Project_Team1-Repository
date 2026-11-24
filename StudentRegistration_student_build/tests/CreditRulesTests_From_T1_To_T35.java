
import org.junit.jupiter.api.Test;
import registration.*;

import java.util.Map;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class CreditRulesTests_From_T1_To_T35 {

    private Student student(double gpa, int completedCredits) {
        return new Student(
                "S1",
                "Test Student",
                gpa,
                completedCredits,
                new LinkedHashMap<>(Map.of())
        );
    }

    // Test Case: T-01
    // Verify that a regular student can register for 15 credits.
    @Test
    void T01_regularStudent_15CreditsAccepted() {
        Student s = student(3.0, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(12, 3, limit));
    }

    // Test Case: T-02
    // Verify that a regular student can increase registered credits from 14 to 17.
    @Test
    void T02_regularStudent_17CreditsAccepted() {
        Student s = student(3.0, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(14, 3, limit));
    }

    // Test Case: T-03
    // Verify that registration at the exact 18-credit limit is accepted.
    @Test
    void T03_regularStudent_18CreditsAccepted() {
        Student s = student(3.0, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(15, 3, limit));
    }

    // Test Case: T-04
    // Verify that registration is rejected when the total reaches 19 credits.
    @Test
    void T04_regularStudent_19CreditsRejected() {
        Student s = student(3.00, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(16, 3, limit));
    }

    // Test Case: T-05
    // Verify that a regular student cannot exceed 18 credits by adding 3 more credits.
    @Test
    void T05_regularStudent_21CreditsRejected() {
        Student s = student(3.00, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(18, 3, limit));
    }

    // Test Case: T-06
    // Verify that a student with GPA 2.50 can register for 12 credits.
    @Test
    void T06_regularStudent_12CreditsAccepted() {
        Student s = student(2.50, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(9, 3, limit));
    }

    // Test Case: T-07
    // Verify that a student with GPA exactly 2.00 has an 18-credit limit.
    @Test
    void T07_gpaBoundary_18CreditsAccepted() {
        Student s = student(2.00, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(15, 3, limit));
    }

    // Test Case: T-08
    // Verify that adding a 4-credit course to 12 credits is accepted.
    @Test
    void T08_regularStudent_16CreditsAccepted() {
        Student s = student(3.50, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(12, 4, limit));
    }

    // Test Case: T-09
    // Verify that a student with GPA 3.20 cannot register for 19 credits.
    @Test
    void T09_regularStudent_19CreditsRejected() {
        Student s = student(3.20, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(15, 4, limit));
    }

    // Test Case: T-10
    // Verify that a high GPA of 3.80 does not increase the regular credit limit.
    @Test
    void T10_regularStudent_19CreditsRejected() {
        Student s = student(3.80, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(15, 4, limit));
    }

    // Test Case: T-11
    // Verify that a student under academic warning can register for 9 credits.
    @Test
    void T11_warningStudent_9CreditsAccepted() {
        Student s = student(1.50, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(6, 3, limit));
    }

    // Test Case: T-12
    // Verify that a student with GPA 1.99 can reach the exact 12-credit limit.
    @Test
    void T12_warningStudent_12CreditsAccepted() {
        Student s = student(1.99, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(9, 3, limit));
    }

    // Test Case: T-13
    // Verify that a student with GPA 1.85 cannot register for 13 credits.
    @Test
    void T13_warningStudent_13CreditsRejected() {
        Student s = student(1.85, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(10, 3, limit));
    }

    // Test Case: T-14
    // Verify that adding 3 credits beyond the warning limit is rejected.
    @Test
    void T14_warningStudent_15CreditsRejected() {
        Student s = student(1.70, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(12, 3, limit));
    }

    // Test Case: T-15
    // Verify that a student with GPA 1.20 can register for 7 credits.
    @Test
    void T15_warningStudent_7CreditsAccepted() {
        Student s = student(1.20, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(3, 4, limit));
    }

    // Test Case: T-16
    // Verify that adding a 4-credit course to 8 credits is accepted at GPA 1.99.
    @Test
    void T16_warningStudent_12CreditsAccepted() {
        Student s = student(1.99, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(8, 4, limit));
    }

    // Test Case: T-17
    // Verify that a GPA just below 2.00 does not allow registration for 13 credits.
    @Test
    void T17_warningStudent_13CreditsRejected() {
        Student s = student(1.99, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(10, 3, limit));
    }

    // Test Case: T-18
    // Verify that a student with GPA 1.75 can reach 12 credits by adding 4 credits.
    @Test
    void T18_warningStudent_12CreditsAccepted() {
        Student s = student(1.75, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(8, 4, limit));
    }

    // Test Case: T-19
    // Verify that a graduating student can increase registered credits from 12 to 15.
    @Test
    void T19_graduatingStudent_15CreditsAccepted() {
        Student s = student(3.00, 113);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(21, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(12, 3, limit));
    }

    // Test Case: T-20
    // Verify that a graduating student can register for 20 credits.
    @Test
    void T20_graduatingStudent_20CreditsAccepted() {
        Student s = student(3.20, 113);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(21, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(17, 3, limit));
    }

    // Test Case: T-21
    // Verify that registration at the exact graduating limit of 21 credits is accepted.
    @Test
    void T21_graduatingStudent_21CreditsAccepted() {
        Student s = student(3.00, 113);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(21, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(18, 3, limit));
    }

    // Test Case: T-22
    // Verify that a graduating student cannot register for 22 credits.
    @Test
    void T22_graduatingStudent_22CreditsRejected() {
        Student s = student(3.50, 113);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(21, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(19, 3, limit));
    }

    // Test Case: T-23
    // Verify that adding 3 credits beyond the graduating limit is rejected.
    @Test
    void T23_graduatingStudent_24CreditsRejected() {
        Student s = student(3.40, 113);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(21, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(21, 3, limit));
    }

    // Test Case: T-24
    // Verify that a graduating student under academic warning can register for 21 credits.
    @Test
    void T24_graduatingWarningStudent_21CreditsAccepted() {
        Student s = student(1.90, 113);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(21, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(18, 3, limit));
    }

    // Test Case: T-25
    // Verify that a graduating student under academic warning cannot exceed 21 credits.
    @Test
    void T25_graduatingWarningStudent_22CreditsRejected() {
        Student s = student(1.85, 113);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(21, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(19, 3, limit));
    }

    // Test Case: T-26
    // Verify that adding a 4-credit course to 15 credits is accepted for a graduating student.
    @Test
    void T26_graduatingStudent_19CreditsAccepted() {
        Student s = student(2.50, 113);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(21, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(15, 4, limit));
    }

    // Test Case: T-27
    // Verify that adding a 4-credit course to 18 credits exceeds the graduating limit.
    @Test
    void T27_graduatingStudent_22CreditsRejected() {
        Student s = student(2.80, 113);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(21, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(18, 4, limit));
    }

    // Test Case: T-28
    // Verify graduating status for a student with 113 completed credits.
    @Test
    void T28_graduating_113CompletedCredits() {
        Student s = student(3.00, 113);

        assertTrue(s.isGraduating());
    }

    // Test Case: T-29
    // Verify graduating status for a student with 116 completed credits.
    @Test
    void T29_graduating_116CompletedCredits() {
        Student s = student(3.00, 116);

        assertTrue(s.isGraduating());
    }

    // Test Case: T-30
    // Verify that a student with 112 completed credits is not classified as graduating.
    @Test
    void T30_notGraduating_112CompletedCredits() {
        Student s = student(3.00, 112);

        assertFalse(s.isGraduating());
    }

    // Test Case: T-31
    // Verify non-graduating status for a student with only 104 completed credits.
    @Test
    void T31_notGraduating_104CompletedCredits() {
        Student s = student(3.00, 104);

        assertFalse(s.isGraduating());
    }

    // Test Case: T-32
    // Verify that a student who has completed all 134 degree credits is not classified as graduating.
    @Test
    void T32_degreeAlreadyCompleted_134Credits() {
        Student s = student(3.00, 134);

        assertFalse(s.isGraduating());
    }

    // Test Case: T-33
    // Verify graduating status for a student with 119 completed credits.
    @Test
    void T33_graduating_119CompletedCredits() {
        Student s = student(3.00, 119);

        assertTrue(s.isGraduating());
    }

    // Test Case: T-34
    // Verify that a student with 20 remaining credits is classified as graduating.
    @Test
    void T34_graduating_20CreditsRemaining() {
        Student s = student(3.00, 114);

        assertEquals(20, s.creditsRemaining());
        assertTrue(s.isGraduating());
    }

    // Test Case: T-35
    // Verify graduating status for a student with 115 completed credits.
    @Test
    void T35_graduating_115CompletedCredits() {
        Student s = student(3.00, 115);

        assertTrue(s.isGraduating());
    }
}
