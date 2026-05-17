package university.services;

import university.models.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GamificationService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static GamificationService instance;
    private Map<Student, StudentProgress> progressMap;
    private Leaderboard leaderboard;
    
    private GamificationService() {
        this.progressMap = new HashMap<>();
        this.leaderboard = new Leaderboard();
    }
    
    public static GamificationService getInstance() {
        if (instance == null) {
            instance = new GamificationService();
        }
        return instance;
    }
    
    private StudentProgress getOrCreateProgress(Student student) {
        return progressMap.computeIfAbsent(student, k -> {
            StudentProgress p = new StudentProgress(student);
            leaderboard.addOrUpdateProgress(p);
            return p;
        });
    }
    
    public void awardForAttendance(Student student, boolean present) {
        if (!present) return;
        
        StudentProgress progress = getOrCreateProgress(student);
        progress.addCoins(5);
        progress.addExp(10);
        System.out.println("🎉 " + student.getFirstName() + " +5 coins, +10 EXP for attendance!");
        leaderboard.addOrUpdateProgress(progress);
    }
    
    public void awardForGrade(Student student, double percentage) {
        StudentProgress progress = getOrCreateProgress(student);
        
        if (percentage >= 90) {
            progress.addDiamonds(1);
            progress.addExp(50);
            System.out.println("💎 " + student.getFirstName() + " +1 diamond, +50 EXP (Excellent!)");
        } else if (percentage >= 75) {
            progress.addCoins(10);
            progress.addExp(25);
            System.out.println("🪙 " + student.getFirstName() + " +10 coins, +25 EXP (Good!)");
        } else if (percentage >= 60) {
            progress.addCoins(5);
            progress.addExp(10);
            System.out.println("🪙 " + student.getFirstName() + " +5 coins, +10 EXP (Pass)");
        }
        
        leaderboard.addOrUpdateProgress(progress);
    }
    
    public StudentProgress getProgress(Student student) {
        return progressMap.get(student);
    }
    
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }
}