package registration;

import java.util.Collections;
import java.util.Map;

/** A student record: academic history, standing, and progress towards the degree. */
public class Student {

    public static final int DEGREE_CREDIT_HOURS = 134;

    public final String id;
    public final String name;
    public final double gpa;
    public final int creditsCompleted;
    public final Map<String, String> completedCourses;   // course code -> grade awarded

    public Student(String id, String name, double gpa, int creditsCompleted,
                   Map<String, String> completedCourses) {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
        this.creditsCompleted = creditsCompleted;
        this.completedCourses = Collections.unmodifiableMap(completedCourses);
    }

    public int creditsRemaining() {
        return DEGREE_CREDIT_HOURS - creditsCompleted;
    }

    /** A student is treated as graduating when the remaining credits fit in one final term. */
    public boolean isGraduating() {
        return creditsRemaining() <= RegistrationRules.GRADUATING_MAX_CREDITS;
    }

    public boolean isOnAcademicWarning() {
        return gpa < RegistrationRules.WARNING_GPA;
    }

    public String gradeIn(String courseCode) {
        return completedCourses.get(courseCode);
    }
}
