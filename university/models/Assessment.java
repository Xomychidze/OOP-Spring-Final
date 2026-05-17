package university.models;

import java.io.Serializable;
import java.time.LocalDate;
import university.enums.AssessmentType;

public class Assessment implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private AssessmentType assessmentType;
    private double score;
    private double maxScore;
    private LocalDate date;
    private String description;
    private String studentSubmission;
    private boolean isGraded;
    
    public Assessment(AssessmentType assessmentType, double maxScore, 
                      LocalDate date, String description) {
        this.assessmentType = assessmentType;
        this.maxScore = maxScore;
        this.date = date;
        this.description = description;
        this.score = 0;
        this.studentSubmission = null;
        this.isGraded = false;
    }
    
    public double getPercentage() {
        if (maxScore == 0) return 0;
        return (score / maxScore) * 100;
    }
    
    public boolean isPassed() {
        return getPercentage() >= 60;
    }
    
    public void setScore(double score) {
        this.score = score;
        this.isGraded = true;
    }
    
    public void submitAssignment(String submission) {
        this.studentSubmission = submission;
    }
    
    // Getters
    public AssessmentType getAssessmentType() { return assessmentType; }
    public double getScore() { return score; }
    public double getMaxScore() { return maxScore; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public String getStudentSubmission() { return studentSubmission; }
    public boolean isGraded() { return isGraded; }
    
    @Override
    public String toString() {
        return description + " (" + assessmentType + "): " + score + "/" + maxScore;
    }
}
