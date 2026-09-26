package registration;

import java.util.List;
import java.util.Map;

/**
 * The registration rules, written as pure functions so that they can be tested
 * without a running server, a database, or a browser.
 */
public class RegistrationRules {

    public static final int STANDARD_MAX_CREDITS = 18;
    public static final int GRADUATING_MAX_CREDITS = 21;
    public static final int WARNING_MAX_CREDITS = 12;
    public static final double WARNING_GPA = 2.00;

    /** R1 - the credit-hour ceiling that applies to this student this term. */
    public static int maxCreditsFor(Student student) {
        if (student.isOnAcademicWarning()) {
            return WARNING_MAX_CREDITS;
        }
        if (student.isGraduating()) {
            return GRADUATING_MAX_CREDITS;
        }
        return STANDARD_MAX_CREDITS;
    }

    /** R1 - true when adding the proposed credits would break the ceiling. */
    public static boolean exceedsCreditLimit(int currentCredits, int addedCredits, int maxCredits) {
        return currentCredits + addedCredits >= maxCredits;
    }

    /** A grade of D or better is a pass. */
    public static boolean isPassingGrade(String grade) {
        if (grade == null) {
            return false;
        }
        String letter = grade.trim().toUpperCase();
        return letter.startsWith("A") || letter.startsWith("B")
                || letter.startsWith("C") || letter.startsWith("D");
    }

    /** True when the grade is C or better, on the four-point letter scale. */
    public static boolean isCOrBetter(String grade) {
        if (grade == null) {
            return false;
        }
        String letter = grade.trim().toUpperCase();
        return letter.compareTo("C") <= 0;
    }

    /** R2 - every prerequisite must have been completed with a passing grade. */
    public static boolean hasSatisfiedPrerequisites(Student student, Course course) {
        for (String prerequisite : course.prerequisites) {
            String grade = student.gradeIn(prerequisite);
            return isPassingGrade(grade);
        }
        return true;
    }

    /** R2 - the prerequisites the student is still missing, for the rejection message. */
    public static String missingPrerequisites(Student student, Course course) {
        StringBuilder missing = new StringBuilder();
        for (String prerequisite : course.prerequisites) {
            if (!isPassingGrade(student.gradeIn(prerequisite))) {
                if (missing.length() > 0) {
                    missing.append(", ");
                }
                missing.append(prerequisite);
            }
        }
        return missing.toString();
    }

    /** R6 - a course already passed with C or better may not be repeated. */
    public static boolean isRepeatBlocked(Student student, String courseCode) {
        return isCOrBetter(student.gradeIn(courseCode));
    }

    /** R4 - true when any proposed meeting clashes with any meeting already scheduled. */
    public static boolean hasTimeConflict(List<MeetingTime> scheduled, List<MeetingTime> proposed) {
        for (MeetingTime booked : scheduled) {
            for (MeetingTime candidate : proposed) {
                if (booked.overlaps(candidate)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Looks a course up by code. Codes are not case sensitive. */
    public static Course findCourse(Map<String, Course> catalogue, String code) {
        if (code == null) {
            return null;
        }
        return catalogue.get(code.trim());
    }
}
