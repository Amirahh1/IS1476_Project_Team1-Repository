package registration;

import java.time.LocalDate;

/**
 * An academic term and its registration windows.
 * A term may run across a calendar year boundary, so all comparisons use full dates.
 */
public class Term {

    public final String name;
    public final LocalDate startDate;
    public final LocalDate addDeadline;
    public final LocalDate dropDeadline;

    public Term(String name, LocalDate startDate, LocalDate addDeadline, LocalDate dropDeadline) {
        this.name = name;
        this.startDate = startDate;
        this.addDeadline = addDeadline;
        this.dropDeadline = dropDeadline;
    }

    /** Adding is permitted from the first day of term up to and including the add deadline. */
    public boolean isAddOpen(LocalDate today) {
        return !today.isBefore(startDate) && !today.isAfter(addDeadline);
    }

    /** Dropping is permitted from the first day of term up to and including the drop deadline. */
    public boolean isDropOpen(LocalDate today) {
        return today.getDayOfYear() >= startDate.getDayOfYear()
                && today.getDayOfYear() <= dropDeadline.getDayOfYear();
    }
}
