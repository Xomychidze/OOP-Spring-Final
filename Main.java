import university.enums.*;
import university.models.*;
import university.services.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Main demo class for the University System (OOP Final Project).
 *
 * Demonstrates:
 *  1. Creating Students and Teachers (inheritance: User → Student / Employee → Teacher)
 *  2. Course creation and enrollment (RegistrationService + exception handling)
 *  3. Schedule / Lesson management (AttendanceManager)
 *  4. Attendance tracking — manual (teacher) and online (student)
 *  5. Assignments — create, submit, grade (AssignmentManager)
 *  6. Marks and grade calculation (Mark + Observer pattern via GradeObserver)
 *  7. Transcript view (Transcript)
 *  8. Gamification — coins, diamonds, EXP (GamificationService + Leaderboard)
 *  9. Exception handling (CreditLimitExceededException, MaxFailAttemptsException)
 */
public class Main {

    public static void main(String[] args) {

        printHeader("UNIVERSITY SYSTEM — OOP FINAL PROJECT DEMO");

        // ─────────────────────────────────────────────
        // 1. CREATE USERS
        // ─────────────────────────────────────────────
        printSection("1. CREATING USERS");

        // Teacher
        Teacher teacher = new Teacher(
                1L, "pak_shamoi", "pass123",
                "shamoi@kbtu.kz", "Pakizar", "Shamoi",
                "EMP-001", "Computer Science", 350_000,
                TeacherTitle.ASSOCIATE_PROFESSOR
        );

        // Students
        Student assel = new Student(
                2L, "assel_b", "pass123",
                "assel@kbtu.kz", "Assel", "Batyrbek",
                "2022001", 2, School.FIT
        );

        Student dariga = new Student(
                3L, "dariga_k", "pass123",
                "dariga@kbtu.kz", "Dariga", "Kassenova",
                "2022002", 2, School.FIT
        );

        Student nazerke = new Student(
                4L, "nazerke_b", "pass123",
                "nazerke@kbtu.kz", "Nazerke", "Bayandina",
                "2022003", 2, School.FIT
        );

        System.out.println("✅ Created teacher:  " + teacher);
        System.out.println("✅ Created student:  " + assel);
        System.out.println("✅ Created student:  " + dariga);
        System.out.println("✅ Created student:  " + nazerke);

        // ─────────────────────────────────────────────
        // 2. CREATE COURSES
        // ─────────────────────────────────────────────
        printSection("2. CREATING COURSES");

        Course oop = new Course(
                "CS101", "Object-Oriented Programming", 5,
                "Core OOP concepts in Java", School.FIT, 2
        );

        Course ds = new Course(
                "CS102", "Data Structures", 4,
                "Algorithms and data structures", School.FIT, 2
        );

        // Assign teacher to courses
        oop.addTeacher(teacher);
        ds.addTeacher(teacher);
        teacher.addCourse(oop);
        teacher.addCourse(ds);

        System.out.println("📘 Course created: " + oop);
        System.out.println("📘 Course created: " + ds);

        // ─────────────────────────────────────────────
        // 3. STUDENT REGISTRATION (RegistrationService)
        // ─────────────────────────────────────────────
        printSection("3. STUDENT REGISTRATION");

        RegistrationService registration = new RegistrationService();

        registration.registerForCourse(assel, oop);
        registration.registerForCourse(assel, ds);
        registration.registerForCourse(dariga, oop);
        registration.registerForCourse(nazerke, oop);

        // Demonstrate CreditLimitExceededException
        System.out.println("\n--- Trying to exceed credit limit ---");
        Course extra1 = new Course("CS103", "Extra Course 1", 6, "", School.FIT, 2);
        Course extra2 = new Course("CS104", "Extra Course 2", 6, "", School.FIT, 2);
        Course extra3 = new Course("CS105", "Extra Course 3", 6, "", School.FIT, 2);
        registration.registerForCourse(assel, extra1);
        registration.registerForCourse(assel, extra2);
        registration.registerForCourse(assel, extra3); // ← should fail: too many credits

        // ─────────────────────────────────────────────
        // 4. LESSONS AND SCHEDULE (AttendanceManager)
        // ─────────────────────────────────────────────
        printSection("4. CREATING LESSONS & SCHEDULE");

        GamificationService gamification = GamificationService.getInstance();
        AttendanceManager attendanceManager = new AttendanceManager(gamification);

        // OOP lessons
        Lesson oopLecture = new Lesson(
                "L-OOP-1", oop, teacher,
                LessonType.LECTURE, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 30),
                "Room 305", LocalDate.of(2025, 5, 5)
        );

        Lesson oopPractice = new Lesson(
                "L-OOP-2", oop, teacher,
                LessonType.PRACTICE, DayOfWeek.WEDNESDAY,
                LocalTime.of(11, 0), LocalTime.of(12, 30),
                "Lab 202", LocalDate.of(2025, 5, 7)
        );

        // Data Structures lesson
        Lesson dsLecture = new Lesson(
                "L-DS-1", ds, teacher,
                LessonType.LECTURE, DayOfWeek.TUESDAY,
                LocalTime.of(14, 0), LocalTime.of(15, 30),
                "Room 401", LocalDate.of(2025, 5, 6)
        );

        oop.addLesson(oopLecture);
        oop.addLesson(oopPractice);
        ds.addLesson(dsLecture);

        attendanceManager.addLesson(oopLecture);
        attendanceManager.addLesson(oopPractice);
        attendanceManager.addLesson(dsLecture);

        attendanceManager.printSchedule();

        // ─────────────────────────────────────────────
        // 5. ATTENDANCE TRACKING
        // ─────────────────────────────────────────────
        printSection("5. ATTENDANCE TRACKING");

        // Teacher manually marks attendance
        attendanceManager.markAttendanceManual(oopLecture, assel,   true,  teacher);
        attendanceManager.markAttendanceManual(oopLecture, dariga,  true,  teacher);
        attendanceManager.markAttendanceManual(oopLecture, nazerke, false, teacher);

        attendanceManager.markAttendanceManual(oopPractice, assel,  true,  teacher);
        attendanceManager.markAttendanceManual(oopPractice, dariga, false, teacher);

        System.out.printf("%nAssel's attendance:   %.0f%%%n",
                attendanceManager.getAttendancePercentage(assel));
        System.out.printf("Dariga's attendance:  %.0f%%%n",
                attendanceManager.getAttendancePercentage(dariga));
        System.out.printf("Nazerke's attendance: %.0f%%%n",
                attendanceManager.getAttendancePercentage(nazerke));

        // ─────────────────────────────────────────────
        // 6. OBSERVER PATTERN — GradeObserver
        // ─────────────────────────────────────────────
        printSection("6. OBSERVER PATTERN (GradeObserver)");

        // Anonymous observer that prints a notification when grade changes
        GradeObserver notifier = (student, course, grade) ->
                System.out.println("🔔 [Observer] " + student.getFirstName()
                        + "'s grade in " + course.getName()
                        + " updated → " + String.format("%.1f", grade));

        // Create a mark and attach observer
        Mark asselOopMark = new Mark(assel, oop);
        asselOopMark.addObserver(notifier);

        // Setting attestations triggers the observer
        asselOopMark.setAttestation1(85);
        asselOopMark.setAttestation2(90);
        asselOopMark.setFinalExam(88);

        System.out.println("Final grade: " + asselOopMark.getLetterGrade()
                + " (" + String.format("%.1f", asselOopMark.getTotal()) + ")");

        // ─────────────────────────────────────────────
        // 7. ASSIGNMENTS (AssignmentManager)
        // ─────────────────────────────────────────────
        printSection("7. ASSIGNMENTS");

        // We need transcripts map for AssignmentManager
        java.util.Map<Student, Transcript> transcripts = new java.util.HashMap<>();

        AssignmentManager assignmentManager =
                new AssignmentManager(gamification, transcripts);

        // Teacher creates a midterm assignment for OOP course
        Assessment midterm = new Assessment(
                AssessmentType.MIDTERM, 100,
                LocalDate.of(2025, 5, 10),
                "Midterm: OOP Design Patterns"
        );

        assignmentManager.createAssignment(oop, midterm, teacher);

        // Students submit their work
        assignmentManager.submitAssignment(assel,   midterm, "My midterm solution for Assel");
        assignmentManager.submitAssignment(dariga,  midterm, "My midterm solution for Dariga");
        assignmentManager.submitAssignment(nazerke, midterm, "My midterm solution for Nazerke");

        // Teacher grades
        assignmentManager.gradeAssignment(assel,   midterm, 92, teacher, oop);
        assignmentManager.gradeAssignment(dariga,  midterm, 78, teacher, oop);
        assignmentManager.gradeAssignment(nazerke, midterm, 55, teacher, oop);

        // ─────────────────────────────────────────────
        // 8. TEACHER PUTS MARKS (Mark)
        // ─────────────────────────────────────────────
        printSection("8. TEACHER PUTS MARKS");

        teacher.putMark(dariga,  oop, 78, 80, 75);
        teacher.putMark(nazerke, oop, 55, 60, 50);
        teacher.putMark(assel,   ds,  90, 88, 92);

        // Student rates the teacher
        assel.rateTeacher(teacher, 5);
        dariga.rateTeacher(teacher, 4);
        System.out.println("Teacher rating: " + String.format("%.1f", teacher.getRating()) + "/5");

        // ─────────────────────────────────────────────
        // 9. TRANSCRIPTS
        // ─────────────────────────────────────────────
        printSection("9. TRANSCRIPTS");

        // Build transcripts from AttendanceManager (includes attendance records)
        Transcript asselTranscript   = attendanceManager.getTranscript(assel);
        Transcript darigaTranscript  = attendanceManager.getTranscript(dariga);
        Transcript nazerkeTranscript = attendanceManager.getTranscript(nazerke);

        // Add marks to transcripts
        for (Mark m : assel.viewMarks()) {
            asselTranscript.addMark(m);
        }
        for (Mark m : dariga.viewMarks()) {
            darigaTranscript.addMark(m);
        }
        for (Mark m : nazerke.viewMarks()) {
            nazerkeTranscript.addMark(m);
        }

        // Also add the OOP mark we created manually with observer
        asselTranscript.addMark(asselOopMark);

        asselTranscript.print();
        darigaTranscript.print();

        // ─────────────────────────────────────────────
        // 10. GAMIFICATION & LEADERBOARD
        // ─────────────────────────────────────────────
        printSection("10. GAMIFICATION & LEADERBOARD");

        // Extra attendance rewards
        gamification.awardForAttendance(assel,   true);
        gamification.awardForAttendance(dariga,  true);
        gamification.awardForAttendance(nazerke, false); // absent — no reward

        // Extra grade rewards (to fill leaderboard)
        gamification.awardForGrade(assel,   95);
        gamification.awardForGrade(dariga,  78);
        gamification.awardForGrade(nazerke, 55);

        // Print leaderboard by total score
        gamification.getLeaderboard().printLeaderboard(Leaderboard.COMPARE_BY_TOTAL);

        // Print leaderboard by EXP
        gamification.getLeaderboard().printLeaderboard(Leaderboard.COMPARE_BY_EXP);

        // Show individual progress
        System.out.println("\n📊 Individual Progress:");
        System.out.println(gamification.getProgress(assel));
        System.out.println(gamification.getProgress(dariga));
        System.out.println(gamification.getProgress(nazerke));

        // ─────────────────────────────────────────────
        // 11. TEACHER REPORT
        // ─────────────────────────────────────────────
        printSection("11. TEACHER MARK REPORT");

        Report report = teacher.generateMarkReport(oop);
        System.out.println("📋 " + report);

        printHeader("DEMO COMPLETE");
    }

    // ── Utility print helpers ──────────────────────────────────────────────────

    private static void printHeader(String title) {
        String line = "═".repeat(60);
        System.out.println("\n" + line);
        System.out.println("  " + title);
        System.out.println(line + "\n");
    }

    private static void printSection(String title) {
        System.out.println("\n──────────────────────────────────────────────────────────");
        System.out.println("  " + title);
        System.out.println("──────────────────────────────────────────────────────────");
    }
}