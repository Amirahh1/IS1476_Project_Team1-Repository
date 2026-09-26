# Student Registration System — IS 1476 working codebase

This is the application you will test, measure and evaluate for the whole semester.
It runs on any machine with a Java Development Kit installed, needs no internet
connection, no database and no build tool.

## Requirements

JDK 17 or later. Check with:

    java -version
    javac -version

If `javac` is missing you have a runtime only; install a JDK.

## Compile and run

From this folder:

    javac -d out src/registration/*.java
    java -cp out registration.Main data 8080

Then open <http://localhost:8080> in a browser. Stop the server with Ctrl-C.

To use a different port:

    java -cp out registration.Main data 9000

## What is here

    src/registration/    the application source, about 700 lines
    data/                the seed data: term, courses, sections, students
    tests/               where your tests go; two starter files are provided
    REQUIREMENTS.md      the requirements specification from the client

The data files are plain text and you are expected to edit them. Adding a student,
a section or a term of your own is often the fastest way to reach a case you want
to test.

## Running the starter checks

Before JUnit is set up:

    javac -cp out -d out-tests tests/OfflineChecks.java
    java -cp out:out-tests OfflineChecks

With JUnit 5 (download `junit-platform-console-standalone.jar` once, from home):

    javac -cp out:junit-platform-console-standalone.jar -d out-tests tests/*.java
    java -jar junit-platform-console-standalone.jar --class-path out:out-tests --scan-class-path

## Where the logic lives

`RegistrationRules` holds the rules as pure functions — no server, no state, no
input or output. That is where most of your unit tests belong. `Registry` holds the
state and applies the rules in order. `WebApp` is only a thin interface over both.

## A standing warning

This code contains defects. Some are functional, some are not; some are visible
through the interface and at least one is not. Your job across the term is to find
them, classify them, and produce the evidence. Do not assume the application is
correct because it starts and appears to work.
