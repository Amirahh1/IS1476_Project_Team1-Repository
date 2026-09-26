package registration;

/**
 * A weekly meeting slot for a section: one day, a start time and an end time.
 * Times are held as minutes since midnight so that comparisons are simple.
 */
public class MeetingTime {

    public final String day;          // SUN, MON, TUE, WED, THU
    public final int startMinute;
    public final int endMinute;

    public MeetingTime(String day, int startMinute, int endMinute) {
        this.day = day;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
    }

    /** Parses text of the form "MON 09:00-09:50". */
    public static MeetingTime parse(String text) {
        String[] parts = text.trim().split("\\s+");
        String day = parts[0].toUpperCase();
        String[] range = parts[1].split("-");
        return new MeetingTime(day, toMinutes(range[0]), toMinutes(range[1]));
    }

    private static int toMinutes(String hhmm) {
        String[] bits = hhmm.split(":");
        return Integer.parseInt(bits[0]) * 60 + Integer.parseInt(bits[1]);
    }

    /**
     * Two slots clash when they fall on the same day and their intervals overlap.
     * Sessions that merely touch - one ending exactly when the other begins - do not clash.
     */
    public boolean overlaps(MeetingTime other) {
        if (!this.day.equals(other.day)) {
            return false;
        }
        return this.startMinute <= other.endMinute && other.startMinute <= this.endMinute;
    }

    private static String format(int minutes) {
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
    }

    @Override
    public String toString() {
        return day + " " + format(startMinute) + "-" + format(endMinute);
    }
}
