package university.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Transcript implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Student student;
    private List<Mark> marks;
    private List<AttendanceRecord> attendanceRecords;
    
    // === Конструкторы ===
    
    public Transcript(Student student) {
        this.student = student;
        this.marks = new ArrayList<>();
        this.attendanceRecords = new ArrayList<>();
    }
    
    public Transcript(Student student, List<Mark> marks) {
        this.student = student;
        this.marks = marks != null ? marks : new ArrayList<>();
        this.attendanceRecords = new ArrayList<>();
    }
    
    // === Методы для оценок ===
    
    public void addMark(Mark mark) {
        if (mark != null && !marks.contains(mark)) {
            marks.add(mark);
        }
    }
    
    public void removeMark(Mark mark) {
        marks.remove(mark);
    }
    
    public Mark getMarkByCourse(Course course) {
        if (course == null) return null;
        return marks.stream()
                .filter(m -> m.getCourse() != null && 
                             course.getCourseId().equals(m.getCourse().getCourseId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Рассчитывает GPA (Grade Point Average).
     * Формула: сумма GPA всех курсов / количество курсов
     */
    public double calculateGpa() {
        if (marks == null || marks.isEmpty()) return 0.0;
        return marks.stream()
                .mapToDouble(Mark::getGPA)
                .average()
                .orElse(0.0);
    }
    
    // === Методы для посещаемости ===
    
    public void addAttendanceRecord(Lesson lesson, boolean present) {
        if (lesson != null) {
            attendanceRecords.add(new AttendanceRecord(lesson, present));
        }
    }
    
    public List<AttendanceRecord> getAttendanceRecords() {
        return new ArrayList<>(attendanceRecords);
    }
    
    public double getAttendancePercentage() {
        if (attendanceRecords.isEmpty()) return 0.0;
        long present = attendanceRecords.stream()
                .filter(AttendanceRecord::isPresent)
                .count();
        return (present * 100.0) / attendanceRecords.size();
    }
    
    // === Статистика ===
    
    public int getTotalCredits() {
        return marks.stream()
                .mapToInt(m -> m.getCourse() != null ? m.getCourse().getCredits() : 0)
                .sum();
    }
    
    public int getPassedCoursesCount() {
        return (int) marks.stream()
                .filter(Mark::isPassed)
                .count();
    }
    
    public int getFailedCoursesCount() {
        return (int) marks.stream()
                .filter(m -> !m.isPassed())
                .count();
    }
    
    // === Печать ===
    
    public void print() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        TRANSCRIPT                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        System.out.println("\n  Student: " + getStudentFullName());
        System.out.println("ID: " + getStudentId());
        System.out.printf("📊 GPA: %.2f%n", calculateGpa());
        
        System.out.println("\nCOURSES:");
        if (marks.isEmpty()) {
            System.out.println("   No courses yet");
        } else {
            for (Mark mark : marks) {
                System.out.println("   " + mark);
            }
        }
        
        System.out.println("\n ATTENDANCE:");
        if (attendanceRecords.isEmpty()) {
            System.out.println("   No attendance records");
        } else {
            for (AttendanceRecord record : attendanceRecords) {
                System.out.println("   " + record);
            }
            System.out.printf("\n   Overall attendance: %.1f%%%n", getAttendancePercentage());
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
    }
    
    private String getStudentFullName() {
        if (student == null) return "Unknown";
        try {
            return student.getFirstName() + " " + student.getLastName();
        } catch (Exception e) {
            return student.toString();
        }
    }
    
    private String getStudentId() {
        if (student == null) return "?";
        try {
            return String.valueOf(student.getStudentId());
        } catch (Exception e) {
            return student.toString();
        }
    }
    
    // === Getters & Setters ===
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public List<Mark> getMarks() { return new ArrayList<>(marks); }
    public void setMarks(List<Mark> marks) { this.marks = marks != null ? marks : new ArrayList<>(); }
    
    @Override
    public String toString() {
        return "Transcript{student=" + getStudentId() +
               ", courses=" + marks.size() +
               ", gpa=" + String.format("%.2f", calculateGpa()) +
               ", attendance=" + String.format("%.1f%%", getAttendancePercentage()) + '}';
    }
}