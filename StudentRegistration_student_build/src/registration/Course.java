package registration;

import java.util.Collections;
import java.util.List;

/** A course in the catalogue. */
public class Course {

    public final String code;
    public final String title;
    public final int creditHours;
    public final List<String> prerequisites;

    public Course(String code, String title, int creditHours, List<String> prerequisites) {
        this.code = code;
        this.title = title;
        this.creditHours = creditHours;
        this.prerequisites = Collections.unmodifiableList(prerequisites);
    }

    @Override
    public String toString() {
        return code + " " + title + " (" + creditHours + " cr)";
    }
}
