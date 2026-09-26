# Al-Manara University — Student Registration System
## Requirements Specification

**Document version:** 1.0
**Status:** Approved for development
**Client:** Deanship of Admission and Registration, Al-Manara University
**Prepared for:** IS 1476 — Software Quality and Experience

> Al-Manara University is a fictional institution. The course catalogue used in the seed
> data mirrors a real Information Systems study plan so that the prerequisite structure
> is realistic; the students, sections and enrolment figures are invented.

---

### 1. Purpose and scope

The Student Registration System allows an enrolled student to view the sections offered in
the current term, add sections to their schedule, and drop sections from it. The system
enforces the university's registration regulations at the moment a student attempts to add
or drop a section.

The system is used by students directly. There is no administrative interface in this
release, and no payment, transcript or grading functionality.

### 2. Definitions

**Term** — an academic semester, with a start date, an add deadline and a drop deadline.
A term does not necessarily fall within a single calendar year; the second semester
customarily begins in one year and ends in the next.

**Course** — a catalogue entry with a code, a title, a credit-hour value and zero or more
prerequisite courses.

**Section** — one offered instance of a course, with a seat capacity and a weekly timetable
of one or more meeting slots.

**Schedule** — the set of sections a student is registered in for the current term.

**Registered credit hours** — the sum of the credit hours of the courses on a student's
schedule.

### 3. Functional requirements

#### R1 — Credit-hour load

R1.1 A student may not register for more than **18 credit hours** in a term.

R1.2 A student whose cumulative GPA is below 2.00 is on academic warning and may not
register for more than **12 credit hours** in a term.

R1.3 A graduating student may register for up to **21 credit hours**. A student is
graduating when the credits remaining for the degree can be completed in one further term.

R1.4 The degree requires 134 credit hours in total.

#### R2 — Prerequisites

R2.1 A student may register in a section only if they have **completed** every prerequisite
of that course.

R2.2 A prerequisite is satisfied by a grade of D or better.

#### R3 — Section capacity

R3.1 A student may not register in a section that is full.

R3.2 A section is full when the number of registered students has reached its capacity.

R3.3 Dropping a section returns the seat to the section immediately.

#### R4 — Timetable conflicts

R4.1 A student may not register in a section whose meeting times conflict with a section
already on their schedule.

R4.2 Two meeting slots conflict when they fall on the same day and overlap in time. A
session that ends at the moment another begins does not conflict with it.

#### R5 — Registration windows

R5.1 Sections may be added from the first day of term up to and including the add deadline.

R5.2 Sections may be dropped from the first day of term up to and including the drop
deadline.

R5.3 The drop deadline is later in the term than the add deadline.

#### R6 — Repeating a course

R6.1 A student may not register in a course they have already passed with a grade of
**C or better**.

R6.2 A student who has passed a course with D or D+ may register in it again to improve
the grade.

#### R7 — Duplicate registration

R7.1 A student may not register in two sections of the same course.

R7.2 A student may not register twice in the same section.

### 4. Non-functional requirements

N1 Every rejected registration must tell the student **which requirement was not met**,
in language the student can act on.

N2 The system must respond to any registration attempt within two seconds.

N3 The system must operate without an internet connection.

### 5. Notes for the development team

The registration rules are applied in the order given above. The first rule that rejects an
attempt ends it; the remaining rules are not evaluated.

Students with a GPA of more than 3.00 have priority for waiting lists. Waiting lists are
out of scope for this release, but the GPA threshold is recorded here for reference; the
registration regulations state that the priority band is 3.00 and above.

A student in the final term of study may register for up to 21 credit hours regardless of
any other limit, so that graduation is not delayed.

---

*End of specification.*
