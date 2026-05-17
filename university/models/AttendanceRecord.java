package university.models;

import java.io.Serializable;
import java.time.LocalDate;

public class AttendanceRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Lesson lesson;
    private boolean present;
    private LocalDate date;
    
    public AttendanceRecord(Lesson lesson, boolean present) {
        this.lesson = lesson;
        this.present = present;
        this.date = LocalDate.now();
    }
    
    public boolean isPresent() { return present; }
    public Lesson getLesson() { return lesson; }
    public LocalDate getDate() { return date; }
    
    @Override
    public String toString() {
        return date + " - " + lesson.getCourse().getName() + 
               " (" + lesson.getLessonType() + "): " + (present ? "PRESENT" : "ABSENT");
    }
}
