package university.models;

import java.io.Serializable;
import java.util.Objects;

public class Mark implements Serializable {

    private static final long serialVersionUID = 1L;

    private Student student;
    private Course course;
    private double attestation1;   // 1st attestation (0–100)
    private double attestation2;   // 2nd attestation (0–100)
    private double finalExam;      // Final exam (0–100)

    public Mark() {}

    public Mark(Student student, Course course,
                double attestation1, double attestation2, double finalExam) {
        this.student = student;
        this.course = course;
        this.attestation1 = attestation1;
        this.attestation2 = attestation2;
        this.finalExam = finalExam;
    }

    /** GPA-style total: A1*0.3 + A2*0.3 + Final*0.4 */
    public double getTotal() {
        return attestation1 * 0.3 + attestation2 * 0.3 + finalExam * 0.4;
    }

    public boolean isPassed() {
        return getTotal() >= 50.0;
    }

    public String getLetterGrade() {
        double total = getTotal();
        if (total >= 90) return "A";
        if (total >= 80) return "B";
        if (total >= 70) return "C";
        if (total >= 60) return "D";
        return "F";
    }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public double getAttestation1() { return attestation1; }
    public void setAttestation1(double attestation1) { this.attestation1 = attestation1; }

    public double getAttestation2() { return attestation2; }
    public void setAttestation2(double attestation2) { this.attestation2 = attestation2; }

    public double getFinalExam() { return finalExam; }
    public void setFinalExam(double finalExam) { this.finalExam = finalExam; }

    @Override
    public String toString() {
        return "Mark{" +
                "course=" + (course != null ? course.getName() : "?") +
                ", A1=" + attestation1 +
                ", A2=" + attestation2 +
                ", Final=" + finalExam +
                ", Total=" + String.format("%.1f", getTotal()) +
                ", Grade=" + getLetterGrade() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mark m)) return false;
        return Objects.equals(student, m.student) &&
               Objects.equals(course, m.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, course);
    }
}
