
import org.junit.jupiter.api.Test;
import registration.*;

import java.util.Map;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class NoufTests {

    private Student student(double gpa, int completedCredits) {
        return new Student(
                "S1",
                "Test Student",
                gpa,
                completedCredits,
                new LinkedHashMap<>(Map.of())
        );
    }

    // T-01: Regular student, 12 + 3 = 15 credits
    @Test
    void T01_regularStudent_15CreditsAccepted() {
        Student s = student(3.0, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(
                RegistrationRules.exceedsCreditLimit(12, 3, limit)
        );
    }

    // T-02: Regular student, 14 + 3 = 17 credits
    @Test   
    void T02_regularStudent_17CreditsAccepted() {
        Student s = student(3.0, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(
                RegistrationRules.exceedsCreditLimit(14, 3, limit)
        );
    }

    // T-03: Regular student, 15 + 3 = 18 credits
    @Test
    void T03_regularStudent_18CreditsAccepted() {
        Student s = student(3.0, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(
                RegistrationRules.exceedsCreditLimit(15, 3, limit)
        );
    }
    
    // T-04: 16 + 3 = 19, exceeds regular limit
    @Test
    void T04_regularStudent_19CreditsRejected() {
        Student s = student(3.00, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(16, 3, limit));
    }

    // T-05: 18 + 3 = 21, exceeds regular limit
    @Test
    void T05_regularStudent_21CreditsRejected() {
        Student s = student(3.00, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(18, 3, limit));
    }

    // T-06: 9 + 3 = 12, accepted
    @Test
    void T06_regularStudent_12CreditsAccepted() {
        Student s = student(2.50, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(9, 3, limit));
    }

    // T-07: GPA exactly 2.00, 15 + 3 = 18
    @Test
    void T07_gpaBoundary_18CreditsAccepted() {
        Student s = student(2.00, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(15, 3, limit));
    }

    // T-08: 12 + 4 = 16, accepted
    @Test
    void T08_regularStudent_16CreditsAccepted() {
        Student s = student(3.50, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(12, 4, limit));
    }

    // T-09: 15 + 4 = 19, rejected
    @Test
    void T09_regularStudent_19CreditsRejected() {
        Student s = student(3.20, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(15, 4, limit));
    }

    // T-10: 15 + 4 = 19, rejected
    @Test
    void T10_regularStudent_19CreditsRejected() {
        Student s = student(3.80, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(18, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(15, 4, limit));
    }
    
    @Test
    void T11_warningStudent_9CreditsAccepted() {
        Student s = student(1.50, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(6, 3, limit));
    }

    @Test
    void T12_warningStudent_12CreditsAccepted() {
        Student s = student(1.99, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(9, 3, limit));
    }

    @Test
    void T13_warningStudent_13CreditsRejected() {
        Student s = student(1.85, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(10, 3, limit));
    }

    @Test
    void T14_warningStudent_15CreditsRejected() {
        Student s = student(1.70, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(12, 3, limit));
    }

    @Test
    void T15_warningStudent_7CreditsAccepted() {
        Student s = student(1.20, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(3, 4, limit));
    }

    @Test
    void T16_warningStudent_12CreditsAccepted() {
        Student s = student(1.99, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(8, 4, limit));
    }

    @Test
    void T17_warningStudent_13CreditsRejected() {
        Student s = student(1.99, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(10, 3, limit));
    }

    @Test
    void T18_warningStudent_12CreditsAccepted() {
        Student s = student(1.75, 60);
        int limit = RegistrationRules.maxCreditsFor(s);

        assertEquals(12, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(8, 4, limit));
    }

    @Test
    void T19_graduatingStudent_15CreditsAccepted() {
        Student s = student(3.00, 113);
        int limit = RegistrationRules.maxCreditsFor(s);
        assertEquals(21, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(12, 3, limit));
    }

    @Test
    void T20_graduatingStudent_20CreditsAccepted() {
        Student s = student(3.20, 113);
        int limit = RegistrationRules.maxCreditsFor(s);
        assertEquals(21, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(17, 3, limit));
    }

    @Test
    void T21_graduatingStudent_21CreditsAccepted() {
        Student s = student(3.00, 113);
        int limit = RegistrationRules.maxCreditsFor(s);
        assertEquals(21, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(18, 3, limit));
    }

    @Test
    void T22_graduatingStudent_22CreditsRejected() {
        Student s = student(3.50, 113);
        int limit = RegistrationRules.maxCreditsFor(s);
        assertEquals(21, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(19, 3, limit));
    }

    @Test
    void T23_graduatingStudent_24CreditsRejected() {
        Student s = student(3.40, 113);
        int limit = RegistrationRules.maxCreditsFor(s);
        assertEquals(21, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(21, 3, limit));
    }

    @Test
    void T24_graduatingWarningStudent_21CreditsAccepted() {
        Student s = student(1.90, 113);
        int limit = RegistrationRules.maxCreditsFor(s);
        assertEquals(21, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(18, 3, limit));
    }

    @Test
void T25_graduatingWarningStudent_22CreditsRejected() {
    Student s = student(1.85, 113);
    int limit = RegistrationRules.maxCreditsFor(s);

    assertEquals(21, limit);
    assertTrue(RegistrationRules.exceedsCreditLimit(19, 3, limit));
}

    @Test
    void T26_graduatingStudent_19CreditsAccepted() {
        Student s = student(2.50, 113);
        int limit = RegistrationRules.maxCreditsFor(s);
        assertEquals(21, limit);
        assertFalse(RegistrationRules.exceedsCreditLimit(15, 4, limit));
    }

    @Test
    void T27_graduatingStudent_22CreditsRejected() {
        Student s = student(2.80, 113);
        int limit = RegistrationRules.maxCreditsFor(s);
        assertEquals(21, limit);
        assertTrue(RegistrationRules.exceedsCreditLimit(18, 4, limit));
    }

    @Test
    void T28_graduating_113CompletedCredits() {
        Student s = student(3.00, 113);

        assertTrue(s.isGraduating());
    }

    @Test
    void T29_graduating_116CompletedCredits() {
        Student s = student(3.00, 116);

        assertTrue(s.isGraduating());
    }

    @Test
    void T30_notGraduating_112CompletedCredits() {
        Student s = student(3.00, 112);

        assertFalse(s.isGraduating());
    }

    @Test
    void T31_notGraduating_104CompletedCredits() {
        Student s = student(3.00, 104);

        assertFalse(s.isGraduating());
    }

    @Test
    void T32_degreeAlreadyCompleted_134Credits() {
        Student s = student(3.00, 134);

        assertFalse(s.isGraduating());
    }

    @Test
    void T33_graduating_119CompletedCredits() {
        Student s = student(3.00, 119);

        assertTrue(s.isGraduating());
    }

 @Test
void T34_graduating_20CreditsRemaining() {
    Student s = student(3.00, 114);

    assertEquals(20, s.creditsRemaining());
    assertTrue(s.isGraduating());
}

    @Test
    void T35_graduating_115CompletedCredits() {
        Student s = student(3.00, 115);

        assertTrue(s.isGraduating());
    }


}
