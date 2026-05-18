package university.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import university.enums.School;
import university.exceptions.CreditLimitExceededException;
import university.exceptions.InsufficientHIndexException;
import university.exceptions.MaxFailAttemptsException;
import university.research.Researcher;

public class Student extends User {

    private static final long serialVersionUID = 1L;

    public static final int MAX_CREDITS   = 21;
    public static final int MAX_FAIL_COUNT = 3;

    private String studentId;
    private int    year;      
    private double gpa;
    private int    credits;   // current semester credits
    private int    failCount;
    private School school;

    // Only valid for 4th-year students; throws InsufficientHIndexException if h-index < 3
    private Researcher supervisor;

    private final List<Course>  registeredCourses = new ArrayList<>();
    private final List<Mark>    marks             = new ArrayList<>();
    private final List<Integer> teacherRatings    = new ArrayList<>();

    public Student() {}

    public Student(Long id, String login, String password,
                   String email, String firstName, String lastName,
                   String studentId, int year, School school) {
        super(id, login, password, email, firstName, lastName);
        this.studentId = studentId;
        this.year      = year;
        this.school    = school;
    }

    //  actions

    public void registerCourse(Course c) {
        if (c == null) return;
        if (failCount >= MAX_FAIL_COUNT) {
            throw new MaxFailAttemptsException(MAX_FAIL_COUNT);
        }
        int newTotal = credits + c.getCredits();
        if (newTotal > MAX_CREDITS) {
            throw new CreditLimitExceededException(MAX_CREDITS, newTotal);
        }
        if (!registeredCourses.contains(c)) {
            registeredCourses.add(c);
            credits += c.getCredits();
            System.out.println("[REGISTER] " + getFirstName() + " registered for: " + c.getName());
        }
    }

    public void dropCourse(Course c) {
        if (registeredCourses.remove(c)) {
            credits -= c.getCredits();
            c.removeStudent(this);
            System.out.println("[DROP] " + getFirstName() + " dropped: " + c.getName());
        }
    }

    public List<Mark> viewMarks() {
        return new ArrayList<>(marks);
    }

    public Transcript getTranscript() {
        return new Transcript(this, new ArrayList<>(marks));
    }

    public void rateTeacher(Teacher t, int rating) {
        if (t == null || rating < 1 || rating > 5) return;
        t.addRating(rating);
        teacherRatings.add(rating);
        System.out.println("[RATE] " + getFirstName() + " rated " + t.getFirstName() + ": " + rating + "/5");
    }

    public List<Lesson> viewSchedule() {
        List<Lesson> schedule = new ArrayList<>();
        for (Course c : registeredCourses) {
            schedule.addAll(c.getLessons());
        }
        return schedule;
    }

    // Called by Teacher.putMark
    public void addMark(Mark m) {
        if (m == null) return;
        // Remove old mark for same course if exists
        marks.removeIf(existing -> Objects.equals(existing.getCourse(), m.getCourse()));
        marks.add(m);
        if (!m.isPassed()) {
            failCount++;
        }
        recalculateGpa();
    }

    private void recalculateGpa() {
        if (marks.isEmpty()) { gpa = 0; return; }
        gpa = marks.stream()
                .mapToDouble(Mark::getTotal)
                .average()
                .orElse(0.0) / 25.0;
    }

    //  supervisor

    public void setSupervisor(Researcher supervisor) {
        if (year < 4) {
            System.out.println("[WARN] Supervisor is only for 4th-year students.");
            return;
        }
        if (supervisor.getHIndex() < 3) {
            throw new InsufficientHIndexException(3, supervisor.getHIndex());
        }
        this.supervisor = supervisor;
    }

    public Researcher getSupervisor() { return supervisor; }

    //  getters/setters

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public int getFailCount() { return failCount; }
    public void setFailCount(int failCount) { this.failCount = failCount; }

    public School getSchool() { return school; }
    public void setSchool(School school) { this.school = school; }

    public String getName(){
        return getFirstName() + " " + getLastName();
    }

    public List<Course> getRegisteredCourses() { return new ArrayList<>(registeredCourses); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student s)) return false;
        return Objects.equals(studentId, s.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + studentId + '\'' +
                ", name='" + getFirstName() + " " + getLastName() + '\'' +
                ", year=" + year +
                ", gpa=" + String.format("%.2f", gpa) +
                ", credits=" + credits +
                ", school=" + school +
                '}';
    }
}
