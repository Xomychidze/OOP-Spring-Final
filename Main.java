import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import university.enums.*;
import university.models.*;
import university.services.*;

/**
 * KBTU University Management System — Interactive Console
 *
 * Default accounts (seeded on startup):
 *   admin   / admin123   → Admin
 *   manager / pass123    → Manager (OR)
 *   shamoi  / pass123    → Teacher (Pakizar Shamoi)
 *   assel   / pass123    → Student (Assel Batyrbek)
 *   dariga  / pass123    → Student (Dariga Kassenova)
 *   nazerke / pass123    → Student (Nazerke Bayandina)
 */
public class Main {

    static final Scanner sc = new Scanner(System.in);
    static final List<User>   allUsers   = new ArrayList<>();
    static final List<Course> allCourses = new ArrayList<>();

    static GamificationService  gamification;
    static AttendanceManager    attendanceManager;
    static RegistrationService  registrationService;
    static AssignmentManager    assignmentManager;
    static Map<Student, Transcript> transcripts;

    static Admin   admin;
    static Manager manager;


    public static void main(String[] args) {
        resetSingletons();
        seedData();
        printBanner();

        while (true) {
            User current = loginPrompt();
            if (current == null) continue;
            sep();
            System.out.println("    Welcome, " + current.getFirstName()
                    + " " + current.getLastName()
                    + "  [" + current.getClass().getSimpleName() + "]");

            if      (current instanceof Admin)   adminMenu  ((Admin)   current);
            else if (current instanceof Manager) managerMenu((Manager) current);
            else if (current instanceof Teacher) teacherMenu((Teacher) current);
            else if (current instanceof Student) studentMenu((Student) current);
        }
    }

    // Reset singleton
    static void resetSingletons() {
        try {
            var f = GamificationService.class.getDeclaredField("instance");
            f.setAccessible(true);
            f.set(null, null);
        } catch (Exception ignored) {}
        gamification        = GamificationService.getInstance();
        attendanceManager   = new AttendanceManager(gamification);
        registrationService = new RegistrationService();
        transcripts         = new HashMap<>();
        assignmentManager   = new AssignmentManager(gamification, transcripts);
    }

    // Seed
    static void seedData() {
        admin = new Admin(1L, "admin", "admin123", "admin@kbtu.kz",
                "Admin", "System", "EMP-000", "IT", 500_000);
        manager = new Manager(2L, "manager", "pass123", "manager@kbtu.kz",
                "Aigerim", "Seitkali", "EMP-001", "Registrar", 400_000,
                ManagerType.OR);
        Teacher shamoi = new Teacher(3L, "shamoi", "pass123", "shamoi@kbtu.kz",
                "Pakizar", "Shamoi", "EMP-002", "Computer Science", 350_000,
                TeacherTitle.ASSOCIATE_PROFESSOR);
        Student assel   = new Student(4L, "assel",   "pass123", "assel@kbtu.kz",
                "Assel",   "Batyrbek",  "2022001", 2, School.FIT);
        Student dariga  = new Student(5L, "dariga",  "pass123", "dariga@kbtu.kz",
                "Dariga",  "Kassenova", "2022002", 2, School.FIT);
        Student nazerke = new Student(6L, "nazerke", "pass123", "nazerke@kbtu.kz",
                "Nazerke", "Bayandina", "2022003", 2, School.FIT);

        Course oop = new Course("CS101","Object-Oriented Programming",5,"Core OOP in Java",School.FIT,2);
        Course ds  = new Course("CS102","Data Structures",4,"Algorithms and DS",School.FIT,2);

        oop.addTeacher(shamoi); ds.addTeacher(shamoi);
        shamoi.addCourse(oop);  shamoi.addCourse(ds);

        silentReg(assel, oop); silentReg(dariga, oop);
        silentReg(nazerke, oop); silentReg(assel, ds);

        Lesson oopLec  = mkLesson("L001",oop,shamoi,LessonType.LECTURE,
                java.time.DayOfWeek.MONDAY,    9,  0,10,30,"Room 305",5,5);
        Lesson oopPrac = mkLesson("L002",oop,shamoi,LessonType.PRACTICE,
                java.time.DayOfWeek.WEDNESDAY,11,  0,12,30,"Lab 202", 5,7);
        Lesson dsLec   = mkLesson("L003",ds, shamoi,LessonType.LECTURE,
                java.time.DayOfWeek.TUESDAY,  14,  0,15,30,"Room 401",5,6);

        oop.addLesson(oopLec); oop.addLesson(oopPrac); ds.addLesson(dsLec);
        attendanceManager.addLesson(oopLec);
        attendanceManager.addLesson(oopPrac);
        attendanceManager.addLesson(dsLec);

        shamoi.putMark(assel,   oop, 85, 90, 88);
        shamoi.putMark(dariga,  oop, 70, 75, 72);
        shamoi.putMark(assel,   ds,  90, 88, 92);

        gamification.awardForGrade(assel,   88);
        gamification.awardForGrade(dariga,  72);
        gamification.awardForGrade(nazerke,  0);

        manager.addStudent(assel); manager.addStudent(dariga); manager.addStudent(nazerke);
        manager.createCourse(oop); manager.createCourse(ds);

        allUsers.addAll(List.of(admin, manager, shamoi, assel, dariga, nazerke));
        allCourses.addAll(List.of(oop, ds));
        for (User u : allUsers) admin.addUser(u);

        System.out.println("[SYSTEM] Seeded "
                + allUsers.size() + " users, " + allCourses.size() + " courses.\n");
    }

    static void silentReg(Student s, Course c) {
        try { s.registerCourse(c); if (!c.getStudents().contains(s)) c.enrollStudent(s); }
        catch (Exception ignored) {}
    }

    static Lesson mkLesson(String id, Course c, Teacher t, LessonType type,
                            java.time.DayOfWeek day, int h1, int m1, int h2, int m2,
                            String room, int month, int dayN) {
        return new Lesson(id, c, t, type, day,
                LocalTime.of(h1,m1), LocalTime.of(h2,m2),
                room, LocalDate.of(2025, month, dayN));
    }

    // Login
    static User loginPrompt() {
        System.out.println("\n" + "─".repeat(52));
        System.out.println("    LOGIN");
        System.out.println("─".repeat(52));
        System.out.print("  Login   : "); String login = sc.nextLine().trim();
        System.out.print("  Password: "); String pass  = sc.nextLine().trim();
        for (User u : allUsers)
            if (u.getLogin().equals(login) && u.authenticate(pass)) return u;
        System.out.println("\n    Invalid credentials.");
        return null;
    }

    
    // STUDENT
    
    static void studentMenu(Student s) {
        while (true) {
            menu("STUDENT", s.getFirstName(),
                    "1. My Courses",
                    "2. My Schedule",
                    "3. My Marks & Transcript",
                    "4. Gamification Progress",
                    "5. Rate a Teacher",
                    "6. Register for Course",
                    "7. Drop a Course",
                    "0. Logout");
            switch (prompt()) {
                case "1" -> sCourses(s);
                case "2" -> sSchedule(s);
                case "3" -> sTranscript(s);
                case "4" -> sGamification(s);
                case "5" -> sRateTeacher(s);
                case "6" -> sRegister(s);
                case "7" -> sDrop(s);
                case "0" -> { s.logout(); return; }
                default  -> bad();
            }
        }
    }

    static void sCourses(Student s) {
        List<Course> list = s.getRegisteredCourses();
        if (list.isEmpty()) { System.out.println("  No enrolled courses."); return; }
        System.out.println("\n    Your Courses  (credits: " + s.getCredits() + "/" + Student.MAX_CREDITS + ")");
        for (int i = 0; i < list.size(); i++) {
            Course c = list.get(i);
            System.out.printf("    %d. [%s] %s — %d credits%n", i+1, c.getCourseId(), c.getName(), c.getCredits());
        }
    }

    static void sSchedule(Student s) {
        List<Lesson> lessons = s.viewSchedule();
        if (lessons.isEmpty()) { System.out.println("  No lessons."); return; }
        System.out.println("\n    Schedule:");
        System.out.printf("  %-22s %-10s %-12s %-13s %s%n","Course","Type","Day","Time","Room");
        System.out.println("  " + "─".repeat(72));
        for (Lesson l : lessons)
            System.out.printf("  %-22s %-10s %-12s %s-%-6s %s%n",
                    trunc(l.getCourse().getName(),21), l.getLessonType(),
                    l.getDayOfWeek(), l.getStartTime(), l.getEndTime(), l.getRoom());
    }

    static void sTranscript(Student s) {
        List<Mark> marks = s.viewMarks();
        if (marks.isEmpty()) { System.out.println("  No marks yet."); return; }
        System.out.println("\n    Transcript — " + s.getFirstName() + " " + s.getLastName());
        System.out.printf("  %-34s %5s %5s %5s %5s  %s%n","Course","A1","A2","Fin","Tot","Grade");
        System.out.println("  " + "─".repeat(63));
        double gpaSum = 0;
        for (Mark m : marks) {
            System.out.printf("  %-34s %5.1f %5.1f %5.1f %5.1f  %s%n",
                    trunc(m.getCourse().getName(),33),
                    m.getAttestation1(), m.getAttestation2(),
                    m.getFinalExam(), m.getTotal(), m.getLetterGrade());
            gpaSum += m.getGPA();
        }
        System.out.println("  " + "─".repeat(63));
        System.out.printf("  GPA: %.2f / 4.0%n", gpaSum / marks.size());
    }

    static void sGamification(Student s) {
        StudentProgress p = gamification.getProgress(s);
        if (p == null) { System.out.println("  No data yet."); return; }
        System.out.println("\n    " + p);
        gamification.getLeaderboard().printLeaderboard(Leaderboard.COMPARE_BY_TOTAL);
    }

    static void sRateTeacher(Student s) {
        Set<Teacher> set = new LinkedHashSet<>();
        for (Course c : s.getRegisteredCourses()) set.addAll(c.getTeachers());
        if (set.isEmpty()) { System.out.println("  No teachers to rate."); return; }
        List<Teacher> list = new ArrayList<>(set);
        System.out.println("\n    Rate a Teacher:");
        for (int i = 0; i < list.size(); i++) {
            Teacher t = list.get(i);
            System.out.printf("    %d. %s %s  (avg %.1f, %d votes)%n",
                    i+1, t.getFirstName(), t.getLastName(),
                    t.getRating(), t.getRatingCount());
        }
        System.out.print("  Teacher (number): ");
        int ti = readInt(1, list.size()) - 1;
        System.out.print("  Rating 1–5: ");
        int r = readInt(1, 5);
        s.rateTeacher(list.get(ti), r);
    }

    static void sRegister(Student s) {
        List<Course> avail = new ArrayList<>();
        for (Course c : allCourses) if (!s.getRegisteredCourses().contains(c)) avail.add(c);
        if (avail.isEmpty()) { System.out.println("  No available courses."); return; }
        System.out.println("\n    Available:");
        for (int i = 0; i < avail.size(); i++)
            System.out.printf("    %d. [%s] %s — %d credits%n",
                    i+1, avail.get(i).getCourseId(), avail.get(i).getName(), avail.get(i).getCredits());
        System.out.print("  Select (0=cancel): "); int idx = readInt(0, avail.size());
        if (idx == 0) return;
        registrationService.registerForCourse(s, avail.get(idx-1));
    }

    static void sDrop(Student s) {
        List<Course> enrolled = s.getRegisteredCourses();
        if (enrolled.isEmpty()) { System.out.println("  No courses to drop."); return; }
        System.out.println("\n     Drop:");
        for (int i = 0; i < enrolled.size(); i++)
            System.out.printf("    %d. %s%n", i+1, enrolled.get(i).getName());
        System.out.print("  Select (0=cancel): "); int idx = readInt(0, enrolled.size());
        if (idx == 0) return;
        registrationService.dropCourse(s, enrolled.get(idx-1));
    }

    
    // TEACHER
    
    static void teacherMenu(Teacher t) {
        while (true) {
            menu("TEACHER", t.getFirstName(),
                    "1. My Courses & Students",
                    "2. Put / Update Marks",
                    "3. Mark Attendance",
                    "4. View Attendance Stats",
                    "5. View Full Schedule",
                    "6. Generate Mark Report",
                    "7. Create Assessment",
                    "0. Logout");
            switch (prompt()) {
                case "1" -> tCourses(t);
                case "2" -> tPutMark(t);
                case "3" -> tMarkAttendance(t);
                case "4" -> tAttendanceStats(t);
                case "5" -> attendanceManager.printSchedule();
                case "6" -> tReport(t);
                case "7" -> tCreateAssessment(t);
                case "0" -> { t.logout(); return; }
                default  -> bad();
            }
        }
    }

    static void tCourses(Teacher t) {
        if (t.getCourses().isEmpty()) { System.out.println("  No courses assigned."); return; }
        for (Course c : t.getCourses()) {
            System.out.printf("%n    %s [%s] — %d credits%n", c.getName(), c.getCourseId(), c.getCredits());
            List<Student> students = c.getStudents();
            if (students.isEmpty()) { System.out.println("    (no students)"); continue; }
            System.out.printf("    %-4s %-26s %5s%n","#","Name","GPA");
            System.out.println("    " + "─".repeat(38));
            for (int i = 0; i < students.size(); i++) {
                Student st = students.get(i);
                System.out.printf("    %-4d %-26s %5.2f%n",
                        i+1, st.getFirstName()+" "+st.getLastName(), st.getGpa());
            }
        }
    }

    static void tPutMark(Teacher t) {
        Course c = pickCourse(t.getCourses()); if (c == null) return;
        Student st = pickStudent(c.getStudents()); if (st == null) return;
        System.out.print("  Attestation 1 (0–100): "); double a1  = readDouble(0,100);
        System.out.print("  Attestation 2 (0–100): "); double a2  = readDouble(0,100);
        System.out.print("  Final Exam    (0–100): "); double fin = readDouble(0,100);
        t.putMark(st, c, a1, a2, fin);
        gamification.awardForGrade(st, a1*0.3 + a2*0.3 + fin*0.4);
        System.out.println("    Mark saved.");
    }

    static void tMarkAttendance(Teacher t) {
        List<Lesson> lessons = new ArrayList<>();
        for (Course c : t.getCourses()) lessons.addAll(c.getLessons());
        if (lessons.isEmpty()) { System.out.println("  No lessons."); return; }
        System.out.println("\n  Select Lesson:");
        for (int i = 0; i < lessons.size(); i++) {
            Lesson l = lessons.get(i);
            System.out.printf("    %d. %s — %s (%s, %s)%n",
                    i+1, l.getCourse().getName(), l.getLessonType(), l.getDayOfWeek(), l.getDate());
        }
        System.out.print("  (0=cancel): "); int li = readInt(0, lessons.size());
        if (li == 0) return;
        Lesson lesson = lessons.get(li-1);
        List<Student> students = lesson.getCourse().getStudents();
        if (students.isEmpty()) { System.out.println("  No students."); return; }
        System.out.println("  Mark attendance — y=present, n=absent:");
        for (Student st : students) {
            System.out.printf("  %s %s: ", st.getFirstName(), st.getLastName());
            boolean present = sc.nextLine().trim().equalsIgnoreCase("y");
            attendanceManager.markAttendanceManual(lesson, st, present, t);
        }
        System.out.println("    Attendance recorded.");
    }

    static void tAttendanceStats(Teacher t) {
        System.out.println("\n    Attendance:");
        for (Course c : t.getCourses()) {
            System.out.println("  " + c.getName() + ":");
            for (Student st : c.getStudents())
                System.out.printf("    %-26s %.0f%%%n",
                        st.getFirstName()+" "+st.getLastName(),
                        attendanceManager.getAttendancePercentage(st));
        }
    }

    static void tReport(Teacher t) {
        Course c = pickCourse(t.getCourses()); if (c == null) return;
        System.out.println("\n    " + t.generateMarkReport(c));
    }

    static void tCreateAssessment(Teacher t) {
        Course c = pickCourse(t.getCourses()); if (c == null) return;
        AssessmentType[] types = AssessmentType.values();
        System.out.println("  Type:");
        for (int i = 0; i < types.length; i++) System.out.printf("    %d. %s%n", i+1, types[i]);
        System.out.print("  Select: "); AssessmentType type = types[readInt(1, types.length)-1];
        System.out.print("  Max score: "); int max = readInt(1, 200);
        System.out.print("  Due date (YYYY-MM-DD): "); LocalDate due = parseDate(sc.nextLine().trim());
        System.out.print("  Description: "); String desc = sc.nextLine().trim();
        Assessment a = new Assessment(type, max, due, desc);
        assignmentManager.createAssignment(c, a, t);
        System.out.println("    Assessment created.");
    }

    // MANAGER
    static void managerMenu(Manager mgr) {
        while (true) {
            menu("MANAGER", mgr.getFirstName(),
                    "1. View Students (by GPA)",
                    "2. View Students (alphabetically)",
                    "3. Assign Teacher to Course",
                    "4. Academic Report",
                    "5. Publish News",
                    "6. View News",
                    "7. Activity Logs",
                    "8. Leaderboard",
                    "0. Logout");
            switch (prompt()) {
                case "1" -> { System.out.println("\n    By GPA:"); int i=1;
                              for (Student s : mgr.viewStudentsSortedByGpa())
                                  System.out.printf("  %2d. %-25s GPA: %.2f%n",i++,
                                          s.getFirstName()+" "+s.getLastName(), s.getGpa()); }
                case "2" -> { System.out.println("\n    Alphabetically:"); int i=1;
                              for (Student s : mgr.viewStudentsAlphabetically())
                                  System.out.printf("  %2d. %s %s%n",i++,s.getFirstName(),s.getLastName()); }
                case "3" -> mAssignTeacher(mgr);
                case "4" -> System.out.println("\n    " + mgr.generateAcademicReport());
                case "5" -> mPublishNews(mgr);
                case "6" -> { List<News> nl = mgr.getNewsList();
                              if (nl.isEmpty()) System.out.println("  No news.");
                              else nl.forEach(n -> System.out.println("  ─\n  " + n.getTitle()
                                      + " [" + n.getDate() + "]\n  " + n.getContent())); }
                case "7" -> mgr.getLogs().forEach(l -> System.out.println("  " + l));
                case "8" -> gamification.getLeaderboard().printLeaderboard(Leaderboard.COMPARE_BY_TOTAL);
                case "0" -> { mgr.logout(); return; }
                default  -> bad();
            }
        }
    }

    static void mAssignTeacher(Manager mgr) {
        List<Teacher> teachers = new ArrayList<>();
        for (User u : allUsers) if (u instanceof Teacher tch) teachers.add(tch);
        if (teachers.isEmpty()) { System.out.println("  No teachers."); return; }
        System.out.println("  Teacher:");
        for (int i = 0; i < teachers.size(); i++)
            System.out.printf("    %d. %s %s%n",i+1,teachers.get(i).getFirstName(),teachers.get(i).getLastName());
        System.out.print("  Select: "); Teacher tch = teachers.get(readInt(1,teachers.size())-1);
        System.out.println("  Course:");
        for (int i = 0; i < allCourses.size(); i++)
            System.out.printf("    %d. %s%n",i+1,allCourses.get(i).getName());
        System.out.print("  Select: "); Course c = allCourses.get(readInt(1,allCourses.size())-1);
        mgr.assignTeacher(tch, c);
        tch.addCourse(c);
        System.out.println("    Done.");
    }

    static void mPublishNews(Manager mgr) {
        System.out.print("  Title  : "); String title = sc.nextLine().trim();
        System.out.print("  Content: "); String content = sc.nextLine().trim();
        mgr.manageNews(new News(title, content, LocalDate.now()));
        System.out.println("    Published.");
    }

    
    // ADMIN
    
    static void adminMenu(Admin adm) {
        while (true) {
            menu("ADMIN", "System",
                    "1. List All Users",
                    "2. Add Student",
                    "3. Add Teacher",
                    "4. Remove User by ID",
                    "5. Block User by ID",
                    "6. List All Courses",
                    "7. Create Course",
                    "8. System Logs",
                    "0. Logout");
            switch (prompt()) {
                case "1" -> aListUsers(adm);
                case "2" -> aAddStudent(adm);
                case "3" -> aAddTeacher(adm);
                case "4" -> aRemoveUser(adm);
                case "5" -> aBlockUser(adm);
                case "6" -> aListCourses();
                case "7" -> aCreateCourse(adm);
                case "8" -> aLogs(adm);
                case "0" -> { adm.logout(); return; }
                default  -> bad();
            }
        }
    }

    static void aListUsers(Admin adm) {
        List<Long> blocked = adm.getBlockedUserIds();
        System.out.printf("%n  %-4s %-10s %-26s %-12s %s%n","ID","Role","Name","Login","Status");
        System.out.println("  " + "─".repeat(68));
        for (User u : adm.getUsers())
            System.out.printf("  %-4d %-10s %-26s %-12s %s%n",
                    u.getId(), u.getClass().getSimpleName(),
                    u.getFirstName()+" "+u.getLastName(),
                    u.getLogin(),
                    blocked.contains(u.getId()) ? "BLOCKED" : "active");
    }

    static void aAddStudent(Admin adm) {
        System.out.println("\n    New Student:");
        System.out.print("  First name : "); String first = sc.nextLine().trim();
        System.out.print("  Last name  : "); String last  = sc.nextLine().trim();
        System.out.print("  Login      : "); String login = sc.nextLine().trim();
        System.out.print("  Password   : "); String pass  = sc.nextLine().trim();
        System.out.print("  Email      : "); String email = sc.nextLine().trim();
        System.out.print("  Student ID : "); String sid   = sc.nextLine().trim();
        System.out.print("  Year (1–4) : "); int year = readInt(1,4);
        long id = nextId();
        Student s = new Student(id, login, pass, email, first, last, sid, year, School.FIT);
        allUsers.add(s); adm.addUser(s); manager.addStudent(s);
        System.out.println("   Student added (login: "+login+", id: "+id+")");
    }

    static void aAddTeacher(Admin adm) {
        System.out.println("\n    New Teacher:");
        System.out.print("  First name : "); String first = sc.nextLine().trim();
        System.out.print("  Last name  : "); String last  = sc.nextLine().trim();
        System.out.print("  Login      : "); String login = sc.nextLine().trim();
        System.out.print("  Password   : "); String pass  = sc.nextLine().trim();
        System.out.print("  Email      : "); String email = sc.nextLine().trim();
        System.out.print("  Department : "); String dept  = sc.nextLine().trim();
        System.out.print("  Salary (₸) : "); double sal  = readDouble(0, 10_000_000);
        TeacherTitle[] titles = TeacherTitle.values();
        System.out.println("  Title:");
        for (int i = 0; i < titles.length; i++) System.out.printf("    %d. %s%n",i+1,titles[i]);
        System.out.print("  Select: "); TeacherTitle title = titles[readInt(1,titles.length)-1];
        long id = nextId();
        Teacher t = new Teacher(id, login, pass, email, first, last,
                "EMP-"+String.format("%03d",id), dept, sal, title);
        allUsers.add(t); adm.addUser(t);
        System.out.println("    Teacher added (login: "+login+", id: "+id+")");
    }

    static void aRemoveUser(Admin adm) {
        System.out.print("  User ID: ");
        try { long id = Long.parseLong(sc.nextLine().trim());
              allUsers.removeIf(u -> u.getId().equals(id)); adm.removeUser(id);
              System.out.println("    Removed."); }
        catch (NumberFormatException e) { System.out.println("    Invalid ID."); }
    }

    static void aBlockUser(Admin adm) {
        System.out.print("  User ID: ");
        try { long id = Long.parseLong(sc.nextLine().trim()); adm.blockUser(id);
              System.out.println("    Blocked."); }
        catch (NumberFormatException e) { System.out.println("    Invalid ID."); }
    }

    static void aListCourses() {
        System.out.printf("%n  %-8s %-36s %7s %9s%n","ID","Name","Credits","Students");
        System.out.println("  " + "─".repeat(63));
        for (Course c : allCourses)
            System.out.printf("  %-8s %-36s %7d %9d%n",
                    c.getCourseId(), c.getName(), c.getCredits(), c.getStudents().size());
    }

    static void aCreateCourse(Admin adm) {
        System.out.println("\n    New Course:");
        System.out.print("  Course ID  : "); String id   = sc.nextLine().trim();
        System.out.print("  Name       : "); String name = sc.nextLine().trim();
        System.out.print("  Credits    : "); int creds   = readInt(1,10);
        System.out.print("  Description: "); String desc = sc.nextLine().trim();
        System.out.print("  Target year: "); int year    = readInt(1,4);
        School[] schools = School.values();
        System.out.println("  School:");
        for (int i = 0; i < schools.length; i++) System.out.printf("    %d. %s%n",i+1,schools[i].name());
        System.out.print("  Select: "); School school = schools[readInt(1,schools.length)-1];
        Course c = new Course(id, name, creds, desc, school, year);
        allCourses.add(c); manager.createCourse(c);
        System.out.println("    Course \""+name+"\" created.");
    }

    static void aLogs(Admin adm) {
        List<Log> logs = adm.viewLogs();
        System.out.println("\n    System Logs (" + logs.size() + " entries):");
        if (logs.isEmpty()) { System.out.println("  (empty)"); return; }
        for (Log l : logs) {
            String time = l.getTimestamp() != null
                    ? l.getTimestamp().toLocalTime().toString().substring(0,8) : "--:--:--";
            System.out.println("  [" + time + "]  " + l.getAction());
        }
    }

    // ── Helpers
    static void menu(String role, String name, String... items) {
        int w = 36;
        System.out.println("\n  ╔" + "═".repeat(w) + "╗");
        System.out.printf("  ║ %-" + (w-1) + "s║%n", role + " — " + name);
        System.out.println("  ╠" + "═".repeat(w) + "╣");
        for (String item : items) System.out.printf("  ║  %-" + (w-2) + "s║%n", item);
        System.out.println("  ╚" + "═".repeat(w) + "╝");
        System.out.print("  Choice: ");
    }

    static String prompt() { return sc.nextLine().trim(); }

    static void bad() { System.out.println("    Unknown option."); }

    static void sep() { System.out.println("\n" + "═".repeat(52)); }

    static Course pickCourse(List<Course> courses) {
        if (courses.isEmpty()) { System.out.println("  No courses."); return null; }
        System.out.println("  Course:");
        for (int i = 0; i < courses.size(); i++)
            System.out.printf("    %d. %s%n", i+1, courses.get(i).getName());
        System.out.print("  (0=cancel): "); int idx = readInt(0, courses.size());
        return idx == 0 ? null : courses.get(idx-1);
    }

    static Student pickStudent(List<Student> students) {
        if (students.isEmpty()) { System.out.println("  No students."); return null; }
        System.out.println("  Student:");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            System.out.printf("    %d. %s %s%n", i+1, s.getFirstName(), s.getLastName());
        }
        System.out.print("  (0=cancel): "); int idx = readInt(0, students.size());
        return idx == 0 ? null : students.get(idx-1);
    }

    static int readInt(int min, int max) {
        while (true) {
            try { int v = Integer.parseInt(sc.nextLine().trim());
                  if (v >= min && v <= max) return v;
                  System.out.printf("  Enter %d–%d: ", min, max);
            } catch (NumberFormatException e) { System.out.print("  Number please: "); }
        }
    }

    static double readDouble(double min, double max) {
        while (true) {
            try { double v = Double.parseDouble(sc.nextLine().trim());
                  if (v >= min && v <= max) return v;
                  System.out.printf("  Enter %.0f–%.0f: ", min, max);
            } catch (NumberFormatException e) { System.out.print("  Number please: "); }
        }
    }

    static LocalDate parseDate(String s) {
        try { return LocalDate.parse(s); }
        catch (Exception e) { return LocalDate.now().plusWeeks(2); }
    }

    static long nextId() {
        return allUsers.stream().mapToLong(u -> u.getId()).max().orElse(0) + 1;
    }

    static String trunc(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max-1) + "…";
    }

    static void printBanner() {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║       🎓  KBTU UNIVERSITY MANAGEMENT SYSTEM          ║");
        System.out.println("║               OOP Spring Final Project               ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  Default accounts:                                   ║");
        System.out.println("║    admin   / admin123   →  Admin                     ║");
        System.out.println("║    manager / pass123    →  Manager (Aigerim Seitkali)║");
        System.out.println("║    shamoi  / pass123    →  Teacher (Pakizar Shamoi)  ║");
        System.out.println("║    assel   / pass123    →  Student (Assel Batyrbek)  ║");
        System.out.println("║    dariga  / pass123    →  Student (Dariga Kassenova)║");
        System.out.println("║    nazerke / pass123    →  Student (Nazerke Bayandina║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
