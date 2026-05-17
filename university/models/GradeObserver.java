package university.models;

public interface GradeObserver {
    void onGradeUpdated(Student student, Course course, double grade);
}
