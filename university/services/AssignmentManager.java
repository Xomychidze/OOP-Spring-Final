package university.services;

import university.models.*;
import university.models.Student;
import university.models.Teacher;

import java.io.Serializable;
import java.util.*;

public class AssignmentManager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Map<Student, List<Assessment>> assignmentsByStudent;
    private Map<Student, List<Assessment>> gradedAssignmentsByStudent;
    private Map<Student, Transcript> transcripts;
    private GamificationService gamification;
    
    public AssignmentManager(GamificationService gamification, Map<Student, Transcript> transcripts) {
        this.assignmentsByStudent = new HashMap<>();
        this.gradedAssignmentsByStudent = new HashMap<>();
        this.gamification = gamification;
        this.transcripts = transcripts;
    }

    public void createAssignment(Course course, Assessment assessment, Teacher teacher) {
        // Проверка, что учитель ведёт этот курс
        if (!teacher.getCourses().contains(course)) {
            System.out.println("[ERROR] Teacher " + teacher.getFirstName() + 
                               " is not assigned to course " + course.getName());
            return;
        }
        
        for (Student student : course.getStudents()) {
            assignmentsByStudent.computeIfAbsent(student, k -> new ArrayList<>()).add(assessment);
        }
        System.out.println("Assignment '" + assessment.getDescription() + 
                           "' sent to students of " + course.getName());
    }

    public void submitAssignment(Student student, Assessment assessment, String submission) {
        List<Assessment> pending = assignmentsByStudent.get(student);
        if (pending == null || !pending.contains(assessment)) {
            System.out.println("[ERROR] Assignment not found for student " + student.getFirstName());
            return;
        }
        
        assessment.submitAssignment(submission);
        System.out.println(student.getFirstName() + 
                           " submitted solution for: " + assessment.getDescription());
    }

    public void gradeAssignment(Student student, Assessment assessment, double score, 
                                Teacher teacher, Course course) {
        // Проверка, что учитель ведёт этот курс
        if (!teacher.getCourses().contains(course)) {
            System.out.println("[ERROR] Teacher not assigned to this course");
            return;
        }
        
        assessment.setScore(score);
        
        // Перемещаем из pending в graded
        List<Assessment> pending = assignmentsByStudent.get(student);
        if (pending != null) {
            pending.remove(assessment);
        }
        gradedAssignmentsByStudent.computeIfAbsent(student, k -> new ArrayList<>()).add(assessment);
        
        // Добавляем в транскрипт через Mark
        Mark mark = findOrCreateMark(student, course);
        updateMarkFromAssessment(mark, assessment);
        
        // Геймификация
        if (gamification != null) {
            gamification.awardForGrade(student, assessment.getPercentage());
        }
        
        System.out.println("Teacher " + teacher.getFirstName() + " graded " + 
                           student.getFirstName() + "'s assignment: " + score + 
                           "/" + assessment.getMaxScore() + 
                           " (" + String.format("%.1f", assessment.getPercentage()) + "%)");
    }
    
    private Mark findOrCreateMark(Student student, Course course) {
        // Ищем существующую оценку
        for (Mark m : student.viewMarks()) {
            if (m.getCourse() != null && m.getCourse().equals(course)) {
                return m;
            }
        }
        
        // Создаём новую
        Mark newMark = new Mark(student, course, 0, 0, 0);
        student.addMark(newMark);
        return newMark;
    }
    
    private void updateMarkFromAssessment(Mark mark, Assessment assessment) {
        double currentTotal = mark.getTotal();
        double newPercentage = assessment.getPercentage();
        
        // Обновляем соответствующую часть оценки
        switch (assessment.getAssessmentType()) {
            case MIDTERM:
                mark.setAttestation1(newPercentage);
                break;
            case ENDTERM:
                mark.setAttestation2(newPercentage);
                break;
            case FINAL:
                mark.setFinalExam(newPercentage);
                break;
            default:
                // Для QUIZ, LAB и т.д. — можно усреднить или добавить логику
                System.out.println("Assessment type " + assessment.getAssessmentType() + 
                                   " is not directly mapped to mark components");
        }
    }
    
    public List<Assessment> getPendingAssignments(Student student) {
        return assignmentsByStudent.getOrDefault(student, Collections.emptyList());
    }
    
    public List<Assessment> getGradedAssignments(Student student) {
        return gradedAssignmentsByStudent.getOrDefault(student, Collections.emptyList());
    }
}