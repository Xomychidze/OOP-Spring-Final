package university.models;

import university.enums.LessonType;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Lesson implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String lessonId;        
    private Course course;
    private Teacher teacher;
    private LessonType lessonType;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;      
    private LocalTime endTime;       
    private String room;
    
    private LocalDate date;
    private int duration;             
    private Map<Student, Boolean> attendance; 
    
    
    public Lesson() {
        this.attendance = new HashMap<>();
    }
    
    public Lesson(String lessonId, Course course, Teacher teacher,
                  LessonType lessonType, DayOfWeek dayOfWeek,
                  LocalTime startTime, LocalTime endTime, String room,
                  LocalDate date) {
        this();
        this.lessonId = lessonId;
        this.course = course;
        this.teacher = teacher;
        this.lessonType = lessonType;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.date = date;
        this.duration = (int) java.time.Duration.between(startTime, endTime).toMinutes();
    }

    public Lesson(LessonType lessonType, LocalDate date, LocalTime timeStart, 
                  int duration, Course course, Teacher teacher, String room) {
        this();
        this.lessonId = generateLessonId(course, date);
        this.lessonType = lessonType;
        this.date = date;
        this.duration = duration;
        this.dayOfWeek = date.getDayOfWeek();
        this.course = course;
        this.teacher = teacher;
        this.room = room;
        this.startTime = timeStart;
        this.endTime = timeStart.plusMinutes(duration);
    }
    
    
    public void markAttendance(Student student, boolean present) {
        attendance.put(student, present);
    }
    
    public boolean canTeacherEdit(Teacher teacher) {
        if (teacher == null) return false;
        
        // 1. Проверка: основной преподаватель урока
        if (this.teacher != null && this.teacher.equals(teacher)) {
            return true;
        }
        
        // 2. Проверка: преподаватель курса (используем getTeachers(), а не getCoInstructors)
        if (course != null && course.getTeachers() != null) {
            return course.getTeachers().contains(teacher);
        }
        
        return false;
    }
    
    public Map<Student, Boolean> getAttendance() {
        return attendance;
    }
    
    public boolean isStudentPresent(Student student) {
        return attendance.getOrDefault(student, false);
    }
    
    // === Вспомогательные методы ===
    
    private String generateLessonId(Course course, LocalDate date) {
        return (course != null ? course.getCourseId() : "UNKNOWN") + "_" + date.toString();
    }
    
    public LocalTime getTimeEnd() {
        return endTime != null ? endTime : startTime.plusMinutes(duration);
    }
    
    // === Getters & Setters ===
    
    public String getLessonId() { return lessonId; }
    public void setLessonId(String lessonId) { this.lessonId = lessonId; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    
    public LessonType getLessonType() { return lessonType; }
    public void setLessonType(LessonType lessonType) { this.lessonType = lessonType; }
    
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { 
        this.startTime = startTime; 
        if (endTime != null) {
            this.duration = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { 
        this.endTime = endTime;
        if (startTime != null) {
            this.duration = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }
    
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { 
        this.date = date;
        this.dayOfWeek = date.getDayOfWeek();
    }
    
    public LocalTime getTimeStart() { return startTime; }
    public void setTimeStart(LocalTime timeStart) {
        this.startTime = timeStart;
    }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) {
        this.duration = duration;
        if (startTime != null) {
            this.endTime = startTime.plusMinutes(duration);
        }
    }
    
    // === Переопределённые методы ===
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson l)) return false;
        return Objects.equals(lessonId, l.lessonId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(lessonId);
    }
    
    @Override
    public String toString() {
        if (date != null) {
            return course != null ? course.getName() + " (" + lessonType + ") - " + date + " " + startTime : 
                   "Lesson{" + "id='" + lessonId + '\'' + '}';
        } else {
            return "Lesson{" +
                    "course=" + (course != null ? course.getName() : "?") +
                    ", type=" + lessonType +
                    ", day=" + dayOfWeek +
                    ", time=" + startTime + "-" + endTime +
                    ", room='" + room + '\'' +
                    '}';
        }
    }
}