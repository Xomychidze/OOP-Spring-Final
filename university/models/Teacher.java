package university.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import university.enums.TeacherTitle;

public class Teacher extends Employee {

    private static final long serialVersionUID = 1L;

    private TeacherTitle title;
    private List<Course> courses;
    private double rating;
    private int ratingCount;

    public Teacher() {
        this.courses = new ArrayList<>();
    }

    public Teacher(Long id, String login, String password,
                   String email, String firstName, String lastName,
                   String employeeId, String department, double salary,
                   TeacherTitle title) {
        super(id, login, password, email, firstName, lastName,
              employeeId, department, salary);
        this.title   = title;
        this.courses = new ArrayList<>();
    }

    //  actions

    public void putMark(Student s, Course c, double a1, double a2, double fin) {
        if (s == null || c == null) return;
        if (!courses.contains(c)) {
            System.out.println("[ERROR] Teacher is not assigned to course: " + c.getName());
            return;
        }
        Mark mark = new Mark(s, c, a1, a2, fin);
        s.addMark(mark);
        System.out.printf("[MARK] %s %s → %s: A1=%.1f A2=%.1f Final=%.1f Total=%.1f (%s)%n",
                getFirstName(), getLastName(), c.getName(), a1, a2, fin, mark.getTotal(), mark.getLetterGrade());
    }

    public List<Course> viewCourses() {
        return new ArrayList<>(courses);
    }

    public List<Student> viewStudents(Course c) {
        if (c == null) return new ArrayList<>();
        return c.getStudents();
    }

    public Report generateMarkReport(Course c) {
        if (c == null) return new Report("Empty", "No course provided.");
        List<Student> students = c.getStudents();
        long passed = students.stream()
                .flatMap(s -> s.viewMarks().stream())
                .filter(m -> Objects.equals(m.getCourse(), c) && m.isPassed())
                .count();
        double avg = students.stream()
                .flatMap(s -> s.viewMarks().stream())
                .filter(m -> Objects.equals(m.getCourse(), c))
                .mapToDouble(Mark::getTotal)
                .average()
                .orElse(0.0);

        String content = "Course: " + c.getName() + "\n" +
                "Total students: " + students.size() + "\n" +
                "Passed: " + passed + "\n" +
                "Average score: " + String.format("%.1f", avg);
        return new Report("Mark Report — " + c.getName(), content);
    }

    // Called by Course/Manager when assigning teacher
    public void addCourse(Course c) {
        if (c != null && !courses.contains(c)) {
            courses.add(c);
        }
    }

    // Called by Student.rateTeacher
    public void addRating(int r) {
        ratingCount++;
        rating = ((rating * (ratingCount - 1)) + r) / ratingCount;
    }

    //  getters/setters

    public TeacherTitle getTitle() { return title; }
    public void setTitle(TeacherTitle title) { this.title = title; }

    public double getRating() { return rating; }
    public int getRatingCount() { return ratingCount; }

    public List<Course> getCourses() { return new ArrayList<>(courses); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teacher t)) return false;
        return Objects.equals(getEmployeeId(), t.getEmployeeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployeeId());
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + getEmployeeId() + '\'' +
                ", name='" + getFirstName() + " " + getLastName() + '\'' +
                ", title=" + title +
                ", rating=" + String.format("%.1f", rating) +
                '}';
    }
}
