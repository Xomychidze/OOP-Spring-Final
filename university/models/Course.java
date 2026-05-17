package university.models;

import university.enums.School;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private String courseId;
    private String name;
    private int credits;
    private String description;
    private School targetSchool;
    private int targetYear;
    private List<Teacher> teachers;
    private List<Student> students;
    private List<Lesson> lessons;

    public Course() {
        this.teachers = new ArrayList<>();
        this.students = new ArrayList<>();
        this.lessons  = new ArrayList<>();
    }

    public Course(String courseId, String name, int credits,
                  String description, School targetSchool, int targetYear) {
        this();
        this.courseId    = courseId;
        this.name        = name;
        this.credits     = credits;
        this.description = description;
        this.targetSchool = targetSchool;
        this.targetYear  = targetYear;
    }

    public void addTeacher(Teacher t) {
        if (t != null && !teachers.contains(t)) {
            teachers.add(t);
        }
    }

    public void enrollStudent(Student s) {
        if (s != null && !students.contains(s)) {
            students.add(s);
        }
    }

    public void removeStudent(Student s) {
        students.remove(s);
    }

    public void addLesson(Lesson l) {
        if (l != null && !lessons.contains(l)) {
            lessons.add(l);
        }
    }

    // Getters & Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public School getTargetSchool() { return targetSchool; }
    public void setTargetSchool(School targetSchool) { this.targetSchool = targetSchool; }

    public int getTargetYear() { return targetYear; }
    public void setTargetYear(int targetYear) { this.targetYear = targetYear; }

    public List<Teacher> getTeachers() { return new ArrayList<>(teachers); }
    public List<Student> getStudents() { return new ArrayList<>(students); }
    public List<Lesson>  getLessons()  { return new ArrayList<>(lessons); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course c)) return false;
        return Objects.equals(courseId, c.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + courseId + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", school=" + targetSchool +
                ", year=" + targetYear +
                '}';
    }
}
