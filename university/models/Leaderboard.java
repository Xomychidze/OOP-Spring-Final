package university.models;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Leaderboard implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<StudentProgress> students;
    
    public Leaderboard() {
        this.students = new ArrayList<>();
    }
    
    public void addOrUpdateProgress(StudentProgress progress) {
        students.removeIf(p -> p.getStudent().getId() == progress.getStudent().getId());
        students.add(progress);
    }
    
    public List<StudentProgress> getTopByExp() {
        return students.stream()
            .sorted((a, b) -> Integer.compare(b.getExp(), a.getExp()))
            .collect(Collectors.toList());
    }
    
    public List<StudentProgress> getTopN(int n, Comparator<StudentProgress> comparator) {
        return students.stream()
            .sorted(comparator)
            .limit(n)
            .collect(Collectors.toList());
    }
    
    public static final Comparator<StudentProgress> COMPARE_BY_EXP = 
        (a, b) -> Integer.compare(b.getExp(), a.getExp());
    
    public static final Comparator<StudentProgress> COMPARE_BY_COINS = 
        (a, b) -> Integer.compare(b.getCoins(), a.getCoins());
    
    public static final Comparator<StudentProgress> COMPARE_BY_DIAMONDS = 
        (a, b) -> Integer.compare(b.getDiamonds(), a.getDiamonds());
    
    public static final Comparator<StudentProgress> COMPARE_BY_TOTAL = 
        (a, b) -> Integer.compare(b.getTotalScore(), a.getTotalScore());
    
    public void printLeaderboard(Comparator<StudentProgress> comparator) {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("           LEADERBOARD");
        System.out.println("═══════════════════════════════════════");
        
        List<StudentProgress> sorted = getTopN(10, comparator);
        int rank = 1;
        for (StudentProgress p : sorted) {
            System.out.println(rank++ + ". " + p);
        }
    }
    
    public void printLeaderboard() {
        printLeaderboard(COMPARE_BY_TOTAL);
    }
}
