import university.enums.LessonType;
import university.enums.School;
import university.enums.TeacherTitle;
import university.exceptions.CreditLimitExceededException;
import university.exceptions.InsufficientHIndexException;
import university.models.*;
import university.research.Researcher;
import university.research.ResearchPaper;
import university.research.ResearchProject;
import university.research.ResearcherDecorator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   WSP University System — Demo");
        System.out.println("========================================\n");

        // ── 1. Create a Teacher ──────────────────────────────────────────
        Teacher teacher = new Teacher(
                1L, "asmith", "pass123", "asmith@uni.edu",
                "Alice", "Smith", "EMP-001", "FIT", 250_000,
                TeacherTitle.SENIOR_LECTOR
        );
        System.out.println("Created: " + teacher);

        // ── 2. Create a Professor (always a Researcher) ──────────────────
        Teacher professor = new Teacher(
                2L, "jdoe", "prof456", "jdoe@uni.edu",
                "John", "Doe", "EMP-002", "FIT", 400_000,
                TeacherTitle.PROFESSOR
        );
        // Wrap professor as Researcher via Decorator pattern
        ResearcherDecorator profResearcher = new ResearcherDecorator(professor, 7);

        // Add some research papers
        ResearchPaper paper1 = new ResearchPaper(
                "Deep Learning in NLP", List.of("John Doe", "Alice Smith"),
                "IEEE Transactions", "10.1109/NLP.2023",
                142, 14, LocalDate.of(2023, 5, 20),
                "Abstract about NLP..."
        );
        ResearchPaper paper2 = new ResearchPaper(
                "Graph Neural Networks", List.of("John Doe"),
                "Nature ML", "10.1038/GNN.2022",
                89, 10, LocalDate.of(2022, 11, 1),
                "Abstract about GNNs..."
        );
        profResearcher.addPaper(paper1);
        profResearcher.addPaper(paper2);

        System.out.println("\n--- Professor's papers (sorted by citations) ---");
        profResearcher.printPapers(
                (a, b) -> Integer.compare(b.getCitations(), a.getCitations())
        );

        // ── 3. Create a Course ───────────────────────────────────────────
        Course oop = new Course(
                "CS101", "Object-Oriented Programming", 6,
                "Core OOP principles in Java", School.FIT, 2
        );
        oop.addTeacher(teacher);
        teacher.addCourse(oop);
        System.out.println("\nCreated: " + oop);

        // Add a lesson to the course
        Lesson lecture = new Lesson(
                "L001", oop, teacher, LessonType.LECTURE,
                DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30), "Room 201"
        );
        oop.addLesson(lecture);

        // ── 4. Create Students ───────────────────────────────────────────
        Student s1 = new Student(
                10L, "sayat", "s123", "sayat@uni.edu",
                "Sayat", "Bekzhanov", "STU-001", 2, School.FIT
        );
        Student s2 = new Student(
                11L, "aizat", "a456", "aizat@uni.edu",
                "Aizat", "Nurlan", "STU-002", 4, School.FIT
        );

        // ── 5. Authentication demo ───────────────────────────────────────
        System.out.println("\n--- Authentication ---");
        System.out.println("Sayat login valid: " + s1.authenticate("s123"));
        System.out.println("Wrong password:    " + s1.authenticate("wrong"));

        // ── 6. Register for course ───────────────────────────────────────
        System.out.println("\n--- Course Registration ---");
        s1.registerCourse(oop);
        oop.enrollStudent(s1);

        // Test credit limit
        Course bigCourse = new Course("CS999", "Mega Course", 20,
                "Too many credits", School.FIT, 2);
        try {
            s1.registerCourse(bigCourse);
        } catch (CreditLimitExceededException e) {
            System.out.println("[EXCEPTION] " + e.getMessage());
        }

        // ── 7. Put marks ─────────────────────────────────────────────────
        System.out.println("\n--- Marks ---");
        teacher.putMark(s1, oop, 75, 80, 85);

        System.out.println("Marks for Sayat: " + s1.viewMarks());

        // ── 8. Transcript ────────────────────────────────────────────────
        System.out.println("\n--- Transcript ---");
        s1.getTranscript().print();

        // ── 9. Rate teacher ──────────────────────────────────────────────
        System.out.println("\n--- Teacher Rating ---");
        s1.rateTeacher(teacher, 5);
        System.out.println("Teacher rating: " + String.format("%.1f", teacher.getRating()));

        // ── 10. Schedule ─────────────────────────────────────────────────
        System.out.println("\n--- Schedule ---");
        s1.viewSchedule().forEach(System.out::println);

        // ── 11. Supervisor assignment (4th-year only) ────────────────────
        System.out.println("\n--- Research Supervisor ---");
        // Valid supervisor (h-index = 7 >= 3)
        s2.setSupervisor(profResearcher);
        System.out.println("Supervisor set: " + profResearcher.getWrapped().getFirstName());

        // Attempt with low h-index
        ResearcherDecorator lowHIndex = new ResearcherDecorator(teacher, 1);
        try {
            s2.setSupervisor(lowHIndex);
        } catch (InsufficientHIndexException e) {
            System.out.println("[EXCEPTION] " + e.getMessage());
        }

        // ── 12. ResearchProject ──────────────────────────────────────────
        System.out.println("\n--- Research Project ---");
        ResearchProject project = new ResearchProject("AI in Education",
                LocalDate.of(2024, 9, 1));
        profResearcher.joinProject(project);
        project.addPaper(paper1);
        System.out.println(project);
        System.out.println("Participants: " + project.getParticipants().size());

        // ── 13. Employee messaging ───────────────────────────────────────
        System.out.println("\n--- Messaging ---");
        teacher.sendMessage(professor, "Hi Prof, can we meet tomorrow?");
        teacher.sendComplaint("Classroom too small for the course.");

        // ── 14. Mark report by teacher ───────────────────────────────────
        System.out.println("\n--- Mark Report ---");
        Report report = teacher.generateMarkReport(oop);
        System.out.println(report.getTitle());
        System.out.println(report.getContent());

        System.out.println("\n========================================");
        System.out.println("   Demo complete.");
        System.out.println("========================================");
    }
}
