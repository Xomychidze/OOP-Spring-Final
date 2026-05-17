package university.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import university.enums.ManagerType;

public class Manager extends Employee {
    private ManagerType managerType;

    private final List<Course> courses = new ArrayList<>();
    private final List<Request> requests = new ArrayList<>();
    private final List<News> newsList = new ArrayList<>();
    private final List<Log> logs = new ArrayList<>();
    private final List<Student> students = new ArrayList<>();

    public Manager() {
    }

    public Manager(Long id,
                   String login,
                   String password,
                   String email,
                   String firstName,
                   String lastName,
                   String employeeId,
                   String department,
                   double salary,
                   ManagerType managerType) {
        super(id, login, password, email, firstName, lastName, employeeId, department, salary);
        this.managerType = managerType;
    }

    public ManagerType getManagerType() {
        return managerType;
    }

    public void setManagerType(ManagerType managerType) {
        this.managerType = managerType;
    }

    public void approveRegistration(Student s, Course c) {
        Request request = findRequest(s, c);
        if (request != null) {
            request.setStatus("APPROVED");
            c.enrollStudent(s);
            if (!students.contains(s)) {
                students.add(s);
            }
            logs.add(new Log(
                    "Manager approved registration: " + s + " -> " + c,
                    LocalDateTime.now(),
                    this
            ));
        }
    }

    public void declineRegistration(Student s, Course c) {
        Request request = findRequest(s, c);
        if (request != null) {
            request.setStatus("DECLINED");
            logs.add(new Log(
                    "Manager declined registration: " + s + " -> " + c,
                    LocalDateTime.now(),
                    this
            ));
        }
    }

    public void assignTeacher(Teacher t, Course c) {
        c.addTeacher(t);
        logs.add(new Log(
                "Manager assigned teacher " + t + " to course " + c,
                LocalDateTime.now(),
                this
        ));
    }

    public void createCourse(Course c) {
        if (!courses.contains(c)) {
            courses.add(c);
            logs.add(new Log(
                    "Manager created course: " + c,
                    LocalDateTime.now(),
                    this
            ));
        }
    }

    public Report generateAcademicReport() {
        int totalStudents = students.size();
        double averageGpa = students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);

        String content = "Academic Report\n" +
                "Manager type: " + managerType + "\n" +
                "Total students: " + totalStudents + "\n" +
                "Average GPA: " + String.format("%.2f", averageGpa) + "\n" +
                "Total courses: " + courses.size();

        logs.add(new Log(
                "Manager generated academic report",
                LocalDateTime.now(),
                this
        ));

        return new Report("Academic Report", content);
    }

    public List<Student> viewStudentsSortedByGpa() {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparingDouble(Student::getGpa).reversed());
        return sorted;
    }

    public List<Student> viewStudentsAlphabetically() {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparing(Student::getLastName)
                .thenComparing(Student::getFirstName));
        return sorted;
    }

    public void manageNews(News n) {
        newsList.add(n);
        logs.add(new Log(
                "Manager published news: " + n.getTitle(),
                LocalDateTime.now(),
                this
        ));
    }

    public List<Request> viewRequests() {
        return new ArrayList<>(requests);
    }

    public void addRequest(Request request) {
        requests.add(request);
    }

    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
        }
    }

    public List<Log> getLogs() {
        return new ArrayList<>(logs);
    }

    public List<News> getNewsList() {
        return new ArrayList<>(newsList);
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    private Request findRequest(Student s, Course c) {
        for (Request request : requests) {
            if (request.getStudent().equals(s) && request.getCourse().equals(c)) {
                return request;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "managerType=" + managerType +
                ", employeeId='" + getEmployeeId() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                '}';
    }
}