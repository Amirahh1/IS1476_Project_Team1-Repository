package registration;

import java.util.Collections;
import java.util.List;

/** One offered section of a course, with a seat count and a weekly timetable. */
public class Section {

    public final String id;               // e.g. IS1476-01
    public final String courseCode;
    public final int capacity;
    public final List<MeetingTime> meetings;

    private int enrolled;

    public Section(String id, String courseCode, int capacity, List<MeetingTime> meetings, int enrolled) {
        this.id = id;
        this.courseCode = courseCode;
        this.capacity = capacity;
        this.meetings = Collections.unmodifiableList(meetings);
        this.enrolled = enrolled;
    }

    public int enrolled() {
        return enrolled;
    }

    public int seatsAvailable() {
        return capacity - enrolled;
    }

    public boolean isFull() {
        return enrolled >= capacity;
    }

    public void reserveSeat() {
        enrolled = enrolled + 1;
    }

    public void releaseSeat() {
        if (enrolled > 0) {
            enrolled = enrolled - 1;
        }
    }

    public String timetable() {
        StringBuilder out = new StringBuilder();
        for (MeetingTime m : meetings) {
            if (out.length() > 0) {
                out.append(", ");
            }
            out.append(m.toString());
        }
        return out.toString();
    }
}
