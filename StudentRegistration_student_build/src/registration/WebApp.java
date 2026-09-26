package registration;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** A minimal HTML front end built on the HTTP server that ships with the JDK. */
public class WebApp {

    private final Registry registry;

    public WebApp(Registry registry) {
        this.registry = registry;
    }

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handleHome);
        server.createContext("/student", this::handleStudent);
        server.createContext("/add", this::handleAdd);
        server.createContext("/drop", this::handleDrop);
        server.setExecutor(null);
        server.start();
        System.out.println("Registration system running at http://localhost:" + port);
        System.out.println("Term: " + registry.term.name + "   today: " + registry.today);
    }

    // ---------------------------------------------------------------- routes

    private void handleHome(HttpExchange exchange) throws IOException {
        StringBuilder body = new StringBuilder();
        body.append("<h1>Course Registration</h1>");
        body.append("<p class='muted'>").append(registry.term.name)
            .append(" &middot; today is ").append(registry.today)
            .append(" &middot; add closes ").append(registry.term.addDeadline)
            .append(" &middot; drop closes ").append(registry.term.dropDeadline).append("</p>");
        body.append("<h2>Sign in as</h2><ul class='plain'>");
        for (Student student : registry.students.values()) {
            body.append("<li><a href='/student?id=").append(student.id).append("'>")
                .append(escape(student.name)).append("</a> <span class='muted'>(")
                .append(student.id).append(" &middot; GPA ").append(student.gpa)
                .append(" &middot; ").append(student.creditsCompleted).append(" credits completed)</span></li>");
        }
        body.append("</ul>");
        send(exchange, page("Course Registration", body.toString()));
    }

    private void handleStudent(HttpExchange exchange) throws IOException {
        Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
        String studentId = query.get("id");
        Student student = registry.students.get(studentId);
        if (student == null) {
            send(exchange, page("Not found", "<h1>Unknown student</h1><p><a href='/'>Back</a></p>"));
            return;
        }

        StringBuilder body = new StringBuilder();
        String notice = query.get("msg");
        String status = query.get("status");
        if (notice != null) {
            body.append("<div class='notice ").append("error".equals(status) ? "error" : "ok").append("'>")
                .append(escape(notice)).append("</div>");
        }

        body.append("<h1>").append(escape(student.name)).append("</h1>");
        body.append("<p class='muted'>").append(student.id)
            .append(" &middot; GPA ").append(student.gpa)
            .append(" &middot; ").append(student.creditsCompleted).append(" of ")
            .append(Student.DEGREE_CREDIT_HOURS).append(" credits completed")
            .append(student.isGraduating() ? " &middot; graduating" : "")
            .append(student.isOnAcademicWarning() ? " &middot; academic warning" : "")
            .append("</p>");

        body.append("<h2>My schedule</h2>");
        if (registry.scheduleOf(studentId).isEmpty()) {
            body.append("<p class='muted'>Nothing registered yet.</p>");
        } else {
            body.append("<table><tr><th>Section</th><th>Course</th><th>Credits</th><th>Times</th><th></th></tr>");
            for (Section section : registry.scheduleOf(studentId)) {
                Course course = registry.courses.get(section.courseCode);
                body.append("<tr><td>").append(section.id).append("</td><td>")
                    .append(course.code).append(" &mdash; ").append(escape(course.title))
                    .append("</td><td>").append(course.creditHours).append("</td><td>")
                    .append(section.timetable()).append("</td><td>")
                    .append("<form method='post' action='/drop'>")
                    .append("<input type='hidden' name='id' value='").append(studentId).append("'>")
                    .append("<input type='hidden' name='section' value='").append(section.id).append("'>")
                    .append("<button class='secondary'>Drop</button></form></td></tr>");
            }
            body.append("</table>");
        }
        body.append("<p><strong>Registered credit hours: ").append(registry.currentCredits(studentId))
            .append("</strong> &middot; limit this term: ")
            .append(RegistrationRules.maxCreditsFor(student)).append("</p>");

        body.append("<h2>Offered sections</h2>");
        body.append("<table><tr><th>Section</th><th>Course</th><th>Cr</th><th>Prerequisites</th>")
            .append("<th>Times</th><th>Seats</th><th></th></tr>");
        for (Section section : registry.sections.values()) {
            Course course = registry.courses.get(section.courseCode);
            body.append("<tr><td>").append(section.id).append("</td><td>")
                .append(course.code).append(" &mdash; ").append(escape(course.title))
                .append("</td><td>").append(course.creditHours).append("</td><td class='muted'>")
                .append(course.prerequisites.isEmpty() ? "none" : String.join(", ", course.prerequisites))
                .append("</td><td>").append(section.timetable()).append("</td><td>")
                .append(section.seatsAvailable()).append(" of ").append(section.capacity)
                .append("</td><td><form method='post' action='/add'>")
                .append("<input type='hidden' name='id' value='").append(studentId).append("'>")
                .append("<input type='hidden' name='section' value='").append(section.id).append("'>")
                .append("<button>Add</button></form></td></tr>");
        }
        body.append("</table><p><a href='/'>Sign out</a></p>");
        send(exchange, page(student.name, body.toString()));
    }

    private void handleAdd(HttpExchange exchange) throws IOException {
        Map<String, String> form = parseQuery(readBody(exchange));
        RegistrationResult result = registry.register(form.get("id"), form.get("section"));
        redirectToStudent(exchange, form.get("id"), result);
    }

    private void handleDrop(HttpExchange exchange) throws IOException {
        Map<String, String> form = parseQuery(readBody(exchange));
        RegistrationResult result = registry.drop(form.get("id"), form.get("section"));
        redirectToStudent(exchange, form.get("id"), result);
    }

    // ---------------------------------------------------------------- plumbing

    private void redirectToStudent(HttpExchange exchange, String studentId, RegistrationResult result)
            throws IOException {
        String location = "/student?id=" + encode(studentId)
                + "&status=" + (result.success ? "ok" : "error")
                + "&msg=" + encode(result.success ? result.message : "Registration failed.");
        exchange.getResponseHeaders().add("Location", location);
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static Map<String, String> parseQuery(String raw) {
        Map<String, String> values = new LinkedHashMap<>();
        if (raw == null || raw.isBlank()) {
            return values;
        }
        for (String pair : raw.split("&")) {
            int equals = pair.indexOf('=');
            if (equals > 0) {
                values.put(URLDecoder.decode(pair.substring(0, equals), StandardCharsets.UTF_8),
                        URLDecoder.decode(pair.substring(equals + 1), StandardCharsets.UTF_8));
            }
        }
        return values;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static void send(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static String page(String title, String body) {
        return "<!doctype html><html><head><meta charset='utf-8'><title>" + escape(title) + "</title>"
                + "<style>"
                + "body{font-family:system-ui,Segoe UI,Arial,sans-serif;margin:2rem auto;max-width:60rem;color:#1b2430;line-height:1.5}"
                + "h1{font-size:1.5rem;margin-bottom:.2rem}h2{font-size:1.1rem;margin-top:1.8rem}"
                + "table{border-collapse:collapse;width:100%;margin-top:.5rem;font-size:.92rem}"
                + "th,td{border:1px solid #d5dbe3;padding:.45rem .6rem;text-align:left}"
                + "th{background:#eef1f6}.muted{color:#667;font-size:.9rem}"
                + "ul.plain{list-style:none;padding:0}ul.plain li{padding:.3rem 0}"
                + "button{padding:.3rem .7rem;border:1px solid #35506e;background:#35506e;color:#fff;border-radius:4px;cursor:pointer}"
                + "button.secondary{background:#fff;color:#35506e}"
                + ".notice{padding:.6rem .8rem;border-radius:4px;margin-bottom:1rem}"
                + ".notice.ok{background:#e6f4ea;border:1px solid #99c9a9}"
                + ".notice.error{background:#fdecea;border:1px solid #e0a9a3}"
                + "form{display:inline}a{color:#35506e}"
                + "</style></head><body>" + body + "</body></html>";
    }
}
