package registration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the state of the registration system and applies the rules in order.
 * Everything is kept in memory; restarting the server resets the data.
 */
public class Registry {

    public final Map<String, Course> courses = new LinkedHashMap<>();
    public final Map<String, Section> sections = new LinkedHashMap<>();
    public final Map<String, Student> students = new LinkedHashMap<>();

    /** student id -> the section ids that student is registered in */
    private final Map<String, List<String>> enrolments = new LinkedHashMap<>();

    public Term term;
    public LocalDate today;

    public List<String> sectionIdsFor(String studentId) {
        return enrolments.computeIfAbsent(studentId, key -> new ArrayList<>());
    }

    public List<Section> scheduleOf(String studentId) {
        List<Section> schedule = new ArrayList<>();
        for (String sectionId : sectionIdsFor(studentId)) {
            schedule.add(sections.get(sectionId));
        }
        return schedule;
    }

    public List<MeetingTime> scheduledMeetings(String studentId) {
        List<MeetingTime> meetings = new ArrayList<>();
        for (Section section : scheduleOf(studentId)) {
            meetings.addAll(section.meetings);
        }
        return meetings;
    }

    public int currentCredits(String studentId) {
        int total = 0;
        for (Section section : scheduleOf(studentId)) {
            Course course = courses.get(section.courseCode);
            if (course != null) {
                total = total + course.creditHours;
            }
        }
        return total;
    }

    /**
     * Attempts to register a student in a section. The rules are applied in a fixed
     * order and the first rule that rejects ends the attempt.
     */
    public RegistrationResult register(String studentId, String sectionId) {
        Student student = students.get(studentId);
        if (student == null) {
            return RegistrationResult.rejected("R0-UNKNOWN-STUDENT", "No such student: " + studentId);
        }

        Section section = sections.get(sectionId);
        if (section == null) {
            return RegistrationResult.rejected("R0-UNKNOWN-SECTION", "No such section: " + sectionId);
        }

        Course course = RegistrationRules.findCourse(courses, section.courseCode);
        if (course == null) {
            return RegistrationResult.rejected("R0-UNKNOWN-COURSE",
                    "Section " + sectionId + " refers to a course that is not in the catalogue.");
        }

        if (!term.isAddOpen(today)) {
            return RegistrationResult.rejected("R5-ADD-WINDOW",
                    "The add period for " + term.name + " closed on " + term.addDeadline + ".");
        }

        if (sectionIdsFor(studentId).contains(sectionId)) {
            return RegistrationResult.rejected("R0-DUPLICATE",
                    "You are already registered in section " + sectionId + ".");
        }

        for (Section booked : scheduleOf(studentId)) {
            if (booked.courseCode.equals(section.courseCode)) {
                return RegistrationResult.rejected("R0-DUPLICATE-COURSE",
                        "You are already registered in another section of " + course.code + ".");
            }
        }

        if (RegistrationRules.isRepeatBlocked(student, course.code)) {
            return RegistrationResult.rejected("R6-REPEAT",
                    "You have already passed " + course.code + " with a grade of "
                            + student.gradeIn(course.code) + ". It may not be repeated.");
        }

        if (!RegistrationRules.hasSatisfiedPrerequisites(student, course)) {
            return RegistrationResult.rejected("R2-PREREQUISITE",
                    "Prerequisite not satisfied for " + course.code + ". Missing: "
                            + RegistrationRules.missingPrerequisites(student, course) + ".");
        }

        if (section.isFull()) {
            return RegistrationResult.rejected("R3-CAPACITY",
                    "Section " + sectionId + " is full (" + section.capacity + " seats).");
        }

        section.reserveSeat();

        if (RegistrationRules.hasTimeConflict(scheduledMeetings(studentId), section.meetings)) {
            return RegistrationResult.rejected("R4-TIME-CONFLICT",
                    "Section " + sectionId + " (" + section.timetable()
                            + ") clashes with a section already on your schedule.");
        }

        int maxCredits = RegistrationRules.maxCreditsFor(student);
        if (RegistrationRules.exceedsCreditLimit(currentCredits(studentId), course.creditHours, maxCredits)) {
            return RegistrationResult.rejected("R1-CREDIT-LIMIT",
                    "Adding " + course.code + " would take you to "
                            + (currentCredits(studentId) + course.creditHours)
                            + " credit hours. Your limit this term is " + maxCredits + ".");
        }

        sectionIdsFor(studentId).add(sectionId);
        return RegistrationResult.ok("Registered in " + sectionId + " (" + course.code + ").");
    }

    /** Removes a section from the student's schedule and returns the seat. */
    public RegistrationResult drop(String studentId, String sectionId) {
        if (!students.containsKey(studentId)) {
            return RegistrationResult.rejected("R0-UNKNOWN-STUDENT", "No such student: " + studentId);
        }
        if (!sectionIdsFor(studentId).contains(sectionId)) {
            return RegistrationResult.rejected("R0-NOT-REGISTERED",
                    "You are not registered in section " + sectionId + ".");
        }
        if (!term.isDropOpen(today)) {
            return RegistrationResult.rejected("R5-DROP-WINDOW",
                    "The drop period for " + term.name + " closed on " + term.dropDeadline + ".");
        }

        sectionIdsFor(studentId).remove(sectionId);
        Section section = sections.get(sectionId);
        if (section != null) {
            section.releaseSeat();
        }
        return RegistrationResult.ok("Dropped section " + sectionId + ".");
    }
}
