package university.models;

import java.io.Serializable;
import university.models.Student;

public class StudentProgress implements Serializable, Comparable<StudentProgress> {
    private static final long serialVersionUID = 1L;
    
    private Student student;
    private int coins;
    private int diamonds;
    private int exp;
    
    public StudentProgress(Student student) {
        this.student = student;
        this.coins = 0;
        this.diamonds = 0;
        this.exp = 0;
    }
    
    public void addCoins(int amount) { this.coins += amount; }
    public void addDiamonds(int amount) { this.diamonds += amount; }
    public void addExp(int amount) { this.exp += amount; }
    
    public int getTotalScore() {
        return coins + diamonds * 10 + exp;
    }
    
    @Override
    public int compareTo(StudentProgress other) {
        return Integer.compare(other.getTotalScore(), this.getTotalScore());
    }
    
    // Getters
    public Student getStudent() { return student; }
    public int getCoins() { return coins; }
    public int getDiamonds() { return diamonds; }
    public int getExp() { return exp; }
    
    @Override
    public String toString() {
        return student.getName() + " | Coins: " + coins + " | Diamonds: " + diamonds + " | EXP: " + exp;
    }
}
