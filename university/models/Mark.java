package university.models;

import university.enums.AssessmentType;
import university.models.Student;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Mark implements Serializable, Comparable<Mark> {
    private static final long serialVersionUID = 1L;
    
    // === От ребят ===
    private Student student;
    private Course course;
    private double attestation1;   // 1st attestation (0–100)
    private double attestation2;   // 2nd attestation (0–100)
    private double finalExam;      // Final exam (0–100)
    
    // === От тебя ===
    private List<Assessment> assessments;     // для детализации оценок
    private transient List<GradeObserver> observers;  // transient, чтобы не сериализовать
    
    // === Веса (от ребят) ===
    private static final double WEIGHT_ATT1 = 0.30;
    private static final double WEIGHT_ATT2 = 0.30;
    private static final double WEIGHT_FINAL = 0.40;
    
    // === Конструкторы ===
    
    public Mark() {
        this.assessments = new ArrayList<>();
        this.observers = new ArrayList<>();
    }
    
    // Конструктор ребят
    public Mark(Student student, Course course,
                double attestation1, double attestation2, double finalExam) {
        this();
        this.student = student;
        this.course = course;
        this.attestation1 = attestation1;
        this.attestation2 = attestation2;
        this.finalExam = finalExam;
    }
    
    // Твой конструктор
    public Mark(Student student, Course course) {
        this();
        this.student = student;
        this.course = course;
        this.attestation1 = 0;
        this.attestation2 = 0;
        this.finalExam = 0;
    }
    
    // === Паттерн Observer (твой) ===
    
    public void addObserver(GradeObserver observer) {
        if (observers != null && observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    public void removeObserver(GradeObserver observer) {
        if (observers != null) {
            observers.remove(observer);
        }
    }
    
    private void notifyObservers() {
        if (observers == null) return;
        double total = getTotal();
        for (GradeObserver observer : observers) {
            observer.onGradeUpdated(student, course, total);
        }
    }
    
    // === Методы для работы с Assessment (твой) ===
    
    public void addAssessment(Assessment assessment) {
        if (assessment == null) return;
        
        assessments.add(assessment);
        
        // Автоматически обновляем соответствующие поля
        updateAttestationFromAssessments();
        
        notifyObservers();
    }
    
    private void updateAttestationFromAssessments() {
        if (assessments.isEmpty()) return;
        
        // Вычисляем средние по типам
        double midtermAvg = getAverageByType(AssessmentType.MIDTERM);
        double endtermAvg = getAverageByType(AssessmentType.ENDTERM);
        double finalAvg = getAverageByType(AssessmentType.FINAL);
        
        if (midtermAvg > 0) this.attestation1 = midtermAvg;
        if (endtermAvg > 0) this.attestation2 = endtermAvg;
        if (finalAvg > 0) this.finalExam = finalAvg;
    }
    
    private double getAverageByType(AssessmentType type) {
        List<Assessment> filtered = assessments.stream()
            .filter(a -> a.getAssessmentType() == type)
            .collect(Collectors.toList());
        
        if (filtered.isEmpty()) return 0;
        
        double total = 0;
        for (Assessment a : filtered) {
            total += a.getPercentage();
        }
        return total / filtered.size();
    }
    
    public List<Assessment> getByAssessmentType(AssessmentType type) {
        return assessments.stream()
            .filter(a -> a.getAssessmentType() == type)
            .collect(Collectors.toList());
    }
    
    // === Основные методы (объединённые) ===
    
    /**
     * Вычисляет общую оценку по формуле:
     * A1 * 0.3 + A2 * 0.3 + Final * 0.4
     */
    public double getTotal() {
        // Если есть оценки через Assessment, синхронизируем
        if (!assessments.isEmpty()) {
            updateAttestationFromAssessments();
        }
        return attestation1 * WEIGHT_ATT1 + 
               attestation2 * WEIGHT_ATT2 + 
               finalExam * WEIGHT_FINAL;
    }
    
    /**
     * Алиас для getTotal() (для совместимости с твоим кодом)
     */
    public double getFinalGrade() {
        return getTotal();
    }
    
    public boolean isPassed() {
        return getTotal() >= 50.0;
    }
    
    public String getLetterGrade() {
        double total = getTotal();
        if (total >= 95) return "A";
        if (total >= 90) return "A";
        if (total >= 85) return "B";
        if (total >= 80) return "B";
        if (total >= 75) return "B";
        if (total >= 70) return "B";
        if (total >= 65) return "C";
        if (total >= 60) return "C";
        if (total >= 55) return "D";
        if (total >= 50) return "D";
        return "F";
    }
    
    public double getGPA() {
        String letter = getLetterGrade();
        switch (letter) {
            case "A": return 4.0;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            default: return 0.0;
        }
    }
    
    // === Getters & Setters ===
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public double getAttestation1() { return attestation1; }
    public void setAttestation1(double attestation1) { 
        this.attestation1 = attestation1;
        notifyObservers();
    }
    
    public double getAttestation2() { return attestation2; }
    public void setAttestation2(double attestation2) { 
        this.attestation2 = attestation2;
        notifyObservers();
    }
    
    public double getFinalExam() { return finalExam; }
    public void setFinalExam(double finalExam) { 
        this.finalExam = finalExam;
        notifyObservers();
    }
    
    public List<Assessment> getAssessments() { 
        return new ArrayList<>(assessments); 
    }
    
    // === Comparable (твой) ===
    
    @Override
    public int compareTo(Mark other) {
        return Double.compare(this.getTotal(), other.getTotal());
    }
    
    // === Переопределённые методы ===
    
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
}