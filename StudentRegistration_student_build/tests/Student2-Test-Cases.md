# Student 2 — Manual Test Cases

## Student Information
- **Student:** Student 2
- **Requirement Areas:** Prerequisites, Course Repetition, Duplicate Registration

## Test Conditions
- R2: Prerequisites
- R6: Course Repetition
- R7: Duplicate Registration

---

## R2 — Prerequisites

### TC-08: Student has passed all prerequisites

- **Preconditions:** The student has completed all required prerequisite courses with a grade of D or higher.
- **Test Steps:**
  1. Open the student registration system.
  2. Select a course whose prerequisites have all been completed.
  3. Attempt to register for the course.
- **Expected Result:** The system allows the student to register.

### TC-09: Student has not completed a prerequisite

- **Preconditions:** The student has not completed one of the required prerequisite courses.
- **Test Steps:**
  1. Open the student registration system.
  2. Select a course with an unmet prerequisite.
  3. Attempt to register for the course.
- **Expected Result:** The system rejects the registration.

### TC-10: Student completed a prerequisite with grade D

- **Preconditions:** The student completed the required prerequisite with grade D.
- **Test Steps:**
  1. Open the student registration system.
  2. Select a course that requires this prerequisite.
  3. Attempt to register for the course.
- **Expected Result:** The system allows the student to register because grade D satisfies the prerequisite.

### TC-11: Student completed a prerequisite with a grade below D

- **Preconditions:** The student received a grade below D in the required prerequisite.
- **Test Steps:**
  1. Open the student registration system.
  2. Select a course that requires this prerequisite.
  3. Attempt to register for the course.
- **Expected Result:** The system rejects the registration because the prerequisite does not meet the minimum grade requirement.

---

## R6 — Course Repetition

### TC-21: Student tries to repeat a course passed with grade C

- **Preconditions:** The student previously passed the course with grade C.
- **Test Steps:**
  1. Open the student registration system.
  2. Select the previously completed course.
  3. Attempt to register for it again.
- **Expected Result:** The system rejects the registration.

### TC-22: Student tries to repeat a course passed with grade D

- **Preconditions:** The student previously passed the course with grade D.
- **Test Steps:**
  1. Open the student registration system.
  2. Select the previously completed course.
  3. Attempt to register for it again.
- **Expected Result:** The system allows the student to repeat the course to improve the grade.

---

## R7 — Duplicate Registration

### TC-23: Student registers for two sections of the same course

- **Preconditions:** The student is already registered in one section of a course, and another section of the same course is available.
- **Test Steps:**
  1. Register for the first section.
  2. Select another section of the same course.
  3. Attempt to register.
- **Expected Result:** The system rejects the second registration.

### TC-24: Student registers for the same section twice

- **Preconditions:** The student is already registered in a section.
- **Test Steps:**
  1. Open the student registration system.
  2. Select the same section again.
  3. Attempt to register.
- **Expected Result:** The system rejects the duplicate registration.

---

## Additional Test Cases (T-36 to T-70)

Add the 35 additional test cases (T-36 through T-70) in this section. Keep their test IDs unchanged and ensure they cover R2, R6, and R7.

---

## Actual Test Results

The tests were executed using JUnit. A total of 35 tests were run: 33 passed and 2 failed.

| Test Case | Actual Result | Status | Defect ID | Notes |
|---|---|---|---|---|
| T-36 | As expected | Pass | N/A | All prerequisites completed |
| T-37 | As expected | Pass | N/A | One prerequisite missing |
| T-38 | As expected | Pass | N/A | Grade D accepted |
| T-39 | As expected | Pass | N/A | Grade D+ accepted |
| T-40 | As expected | Pass | N/A | Grade C accepted |
| T-41 | As expected | Pass | N/A | Grade B accepted |
| T-42 | As expected | Pass | N/A | Grade A accepted |
| T-43 | As expected | Pass | N/A | Grade F rejected |
| T-44 | As expected | Pass | N/A | Null grade rejected |
| T-45 | As expected | Pass | N/A | No prerequisites |
| T-46 | As expected | Pass | N/A | Multiple prerequisites completed |
| T-47 | Unexpected result | Fail | D-01 | Second missing prerequisite not detected |
| T-48 | As expected | Pass | N/A | First prerequisite missing |
| T-49 | As expected | Pass | N/A | Lowercase grade |
| T-50 | As expected | Pass | N/A | Grade with surrounding spaces |
| T-51 | As expected | Pass | N/A | Grade C blocks repetition |
| T-52 | Unexpected result | Fail | D-02 | Grade C+ does not block repetition |
| T-53 | As expected | Pass | N/A | Grade B blocks repetition |
| T-54 | As expected | Pass | N/A | Grade B+ blocks repetition |
| T-55 | As expected | Pass | N/A | Grade A blocks repetition |
| T-56 | As expected | Pass | N/A | Grade D allows repetition |
| T-57 | As expected | Pass | N/A | Grade D+ allows repetition |
| T-58 | As expected | Pass | N/A | Grade F does not block repetition |
| T-59 | As expected | Pass | N/A | New course can be registered |
| T-60 | As expected | Pass | N/A | Lowercase grade C |
| T-61 | As expected | Pass | N/A | First registration succeeds |
| T-62 | As expected | Pass | N/A | Duplicate section rejected |
| T-63 | As expected | Pass | N/A | Second section of same course rejected |
| T-64 | As expected | Pass | N/A | Different course allowed |
| T-65 | As expected | Pass | N/A | Duplicate enrollment not added |
| T-66 | As expected | Pass | N/A | Helpful duplicate-section error |
| T-67 | As expected | Pass | N/A | Helpful duplicate-course error |
| T-68 | As expected | Pass | N/A | Another course allowed |
| T-69 | As expected | Pass | N/A | Unknown section rejected |
| T-70 | As expected | Pass | N/A | Unknown student rejected |

**Note:** T-47 and T-52 failed because the actual behavior did not match the expected result. The remaining tests passed.
