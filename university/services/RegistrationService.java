package university.services;

import university.models.Course;
import university.models.Student;
import university.exceptions.CreditLimitExceededException;
import university.exceptions.MaxFailAttemptsException;

import java.io.Serializable;

public class RegistrationService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * Регистрация студента на курс.
     * Использует методы студента: registerCourse() (с проверками внутри)
     */
    public void registerForCourse(Student student, Course course) {
        if (student == null || course == null) {
            System.out.println("[ERROR] Student or course is null");
            return;
        }
        
        try {
            // У студента уже есть метод registerCourse с проверками лимитов
            student.registerCourse(course);
            
            // Добавляем студента в курс, если ещё не добавлен
            if (!course.getStudents().contains(student)) {
                course.enrollStudent(student);
            }
            
            System.out.println(student.getFirstName() + " registered for " + course.getName());
            
        } catch (CreditLimitExceededException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (MaxFailAttemptsException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }
    
    /**
     * Отчисление студента с курса.
     */
    public void dropCourse(Student student, Course course) {
        if (student == null || course == null) return;
        
        student.dropCourse(course);
        course.removeStudent(student);
        
        System.out.println(student.getFirstName() + " dropped " + course.getName());
    }
    
    /**
     * Проверка, может ли студент зарегистрироваться на курс.
     */
    public boolean canRegister(Student student, Course course) {
        if (student == null || course == null) return false;
        
        // Проверка на уже зарегистрированные курсы
        if (student.getRegisteredCourses().contains(course)) {
            System.out.println("Already registered for " + course.getName());
            return false;
        }
        
        // Проверка кредитов
        int newTotal = student.getCredits() + course.getCredits();
        if (newTotal > Student.MAX_CREDITS) {
            System.out.println("Credit limit exceeded: " + newTotal + "/" + Student.MAX_CREDITS);
            return false;
        }
        
        // Проверка количества фейлов
        if (student.getFailCount() >= Student.MAX_FAIL_COUNT) {
            System.out.println("Max fail attempts reached: " + student.getFailCount());
            return false;
        }
        
        return true;
    }
}