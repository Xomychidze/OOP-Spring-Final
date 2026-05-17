package university.models;

import java.io.Serializable;
import java.util.List;

public class Transcript implements Serializable {

    private static final long serialVersionUID = 1L;

    private Student student;
    private List<Mark> marks;

    public Transcript(Student student, List<Mark> marks) {
        this.student = student;
        this.marks = marks;
    }

    public double calculateGpa() {
        if (marks == null || marks.isEmpty()) return 0.0;
        return marks.stream()
                .mapToDouble(Mark::getTotal)
                .average()
                .orElse(0.0) / 25.0; 
    }

    public void print() {
        System.out.println("===== TRANSCRIPT =====");
        System.out.println("Student: " + student.getFirstName() + " " + student.getLastName());
        System.out.println("ID: " + student.getStudentId());
        System.out.printf("GPA: %.2f%n", calculateGpa());
        System.out.println("Courses:");
        for (Mark m : marks) {
            System.out.println("  " + m);
        }
        System.out.println("======================");
    }

    public Student getStudent() { return student; }
    public List<Mark> getMarks() { return marks; }

    @Override
    public String toString() {
        return "Transcript{student=" + (student != null ? student.getStudentId() : "?") +
               ", courses=" + marks.size() +
               ", gpa=" + String.format("%.2f", calculateGpa()) + '}';
    }
}
