package university.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private Student student;
    private Course course;
    private String status; // PENDING, APPROVED, DECLINED
    private LocalDateTime requestedAt;

    public Request() {}

    public Request(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.status = "PENDING";
        this.requestedAt = LocalDateTime.now();
    }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getRequestedAt() { return requestedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request r)) return false;
        return Objects.equals(student, r.student) && Objects.equals(course, r.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, course);
    }

    @Override
    public String toString() {
        return "Request{student=" + (student != null ? student.getStudentId() : "?") +
               ", course=" + (course != null ? course.getName() : "?") +
               ", status='" + status + "'}";
    }
}
