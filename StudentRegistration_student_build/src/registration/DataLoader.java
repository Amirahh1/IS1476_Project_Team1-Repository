package registration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reads the pipe-delimited seed files in the data directory into a Registry. */
public class DataLoader {

    public static Registry load(Path dataDirectory) throws IOException {
        Registry registry = new Registry();
        loadTerm(registry, dataDirectory.resolve("term.txt"));
        loadCourses(registry, dataDirectory.resolve("courses.txt"));
        loadSections(registry, dataDirectory.resolve("sections.txt"));
        loadStudents(registry, dataDirectory.resolve("students.txt"));
        return registry;
    }

    private static List<String> records(Path file) throws IOException {
        List<String> out = new ArrayList<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                out.add(trimmed);
            }
        }
        return out;
    }

    private static void loadTerm(Registry registry, Path file) throws IOException {
        String[] f = records(file).get(0).split("\\|");
        registry.term = new Term(f[0], LocalDate.parse(f[1]), LocalDate.parse(f[2]), LocalDate.parse(f[3]));
        registry.today = LocalDate.parse(f[4]);
    }

    private static void loadCourses(Registry registry, Path file) throws IOException {
        for (String record : records(file)) {
            String[] f = record.split("\\|");
            List<String> prerequisites = new ArrayList<>();
            if (f.length > 3 && !f[3].equals("-") && !f[3].isBlank()) {
                for (String code : f[3].split(",")) {
                    prerequisites.add(code.trim().toUpperCase());
                }
            }
            Course course = new Course(f[0].trim().toUpperCase(), f[1].trim(),
                    Integer.parseInt(f[2].trim()), prerequisites);
            registry.courses.put(course.code, course);
        }
    }

    private static void loadSections(Registry registry, Path file) throws IOException {
        for (String record : records(file)) {
            String[] f = record.split("\\|");
            List<MeetingTime> meetings = new ArrayList<>();
            for (String slot : f[3].split(";")) {
                meetings.add(MeetingTime.parse(slot));
            }
            int alreadyTaken = f.length > 4 ? Integer.parseInt(f[4].trim()) : 0;
            Section section = new Section(f[0].trim(), f[1].trim().toUpperCase(),
                    Integer.parseInt(f[2].trim()), meetings, alreadyTaken);
            registry.sections.put(section.id, section);
        }
    }

    private static void loadStudents(Registry registry, Path file) throws IOException {
        for (String record : records(file)) {
            String[] f = record.split("\\|");
            Map<String, String> completed = new LinkedHashMap<>();
            if (f.length > 4 && !f[4].equals("-") && !f[4].isBlank()) {
                for (String entry : f[4].split(",")) {
                    String[] pair = entry.split(":");
                    completed.put(pair[0].trim().toUpperCase(), pair[1].trim().toUpperCase());
                }
            }
            Student student = new Student(f[0].trim(), f[1].trim(),
                    Double.parseDouble(f[2].trim()), Integer.parseInt(f[3].trim()), completed);
            registry.students.put(student.id, student);
        }
    }
}
