package university.services;

import university.models.*;

import java.io.Serializable;
import java.time.DayOfWeek;  // ← используем стандартный java.time.DayOfWeek
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class AttendanceManager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Lesson> lessons;
    private transient GamificationService gamification;
    private Map<Student, Transcript> transcripts;
    
    public AttendanceManager(GamificationService gamification) {
        this.lessons = new ArrayList<>();
        this.gamification = gamification;
        this.transcripts = new HashMap<>();
    }
    
    public void setGamificationService(GamificationService service) {
        this.gamification = service;
    }
    
    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }
    
    public Transcript getTranscript(Student student) {
        return transcripts.computeIfAbsent(student, s -> new Transcript(s, s.viewMarks()));
    }
    
    /**
     * Ручная отметка учителем
     */
    public void markAttendanceManual(Lesson lesson, Student student, boolean present, Teacher teacher) {
        if (lesson.getTeacher() == null || !lesson.getTeacher().equals(teacher)) {
            System.out.println("[ERROR] Only assigned teacher can mark attendance");
            return;
        }
        
        lesson.markAttendance(student, present);
        
        Transcript transcript = getTranscript(student);
        transcript.addAttendanceRecord(lesson, present);
        
        if (gamification != null && present) {
            gamification.awardForAttendance(student, true);
        }
        
        System.out.println("👨‍🏫 Teacher " + teacher.getFirstName() + " marked " + 
                           student.getFirstName() + " as " + (present ? "PRESENT" : "ABSENT"));
    }
    
    /**
     * Онлайн-отметка (студент сам отмечается)
     */
    public boolean markAttendanceOnline(Lesson lesson, Student student) {
        LocalTime now = LocalTime.now();
        LocalTime windowStart = lesson.getStartTime();
        LocalTime windowEnd = windowStart.plusMinutes(10);
        
        if (now.isBefore(windowEnd) && now.isAfter(windowStart.minusMinutes(5))) {
            lesson.markAttendance(student, true);
            
            Transcript transcript = getTranscript(student);
            transcript.addAttendanceRecord(lesson, true);
            
            if (gamification != null) {
                gamification.awardForAttendance(student, true);
            }
            
            System.out.println("✅ " + student.getFirstName() + " checked in online!");
            return true;
        } else {
            System.out.println("⏰ Online attendance closed (available from " + 
                               windowStart + " to " + windowEnd + ")");
            return false;
        }
    }
    
    /**
     * Получить расписание по дням недели (используем стандартный DayOfWeek)
     */
    public Map<DayOfWeek, List<Lesson>> getScheduleByDayOfWeek() {
        return lessons.stream().collect(Collectors.groupingBy(Lesson::getDayOfWeek));
    }
    
    public List<Lesson> getLessonsForStudent(Student student) {
        List<Course> studentCourses = student.getRegisteredCourses();
        return lessons.stream()
            .filter(l -> studentCourses.contains(l.getCourse()))
            .collect(Collectors.toList());
    }
    
    public double getAttendancePercentage(Student student) {
        List<Lesson> studentLessons = getLessonsForStudent(student);
        if (studentLessons.isEmpty()) return 0;
        
        long present = 0;
        for (Lesson lesson : studentLessons) {
            if (lesson.isStudentPresent(student)) {
                present++;
            }
        }
        
        return (present * 100.0) / studentLessons.size();
    }
    
    public void printSchedule() {
        Map<DayOfWeek, List<Lesson>> schedule = getScheduleByDayOfWeek();
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("           SCHEDULE");
        System.out.println("═══════════════════════════════════════");
        
        // Порядок дней недели
        List<DayOfWeek> orderedDays = Arrays.asList(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
        );
        
        for (DayOfWeek day : orderedDays) {
            List<Lesson> dayLessons = schedule.get(day);
            if (dayLessons != null && !dayLessons.isEmpty()) {
                System.out.println("\n📅 " + day + ":");
                for (Lesson lesson : dayLessons) {
                    System.out.println("   " + lesson.getStartTime() + " - " + 
                                       lesson.getCourse().getName() + 
                                       " (" + lesson.getLessonType() + ") - " +
                                       lesson.getRoom());
                }
            }
        }
    }
}