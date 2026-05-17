package university.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Admin extends Employee {

    private final List<User> users = new ArrayList<>();
    private final List<Log> logs = new ArrayList<>();
    private final List<Long> blockedUserIds = new ArrayList<>();

    public Admin() {
    }

    public Admin(Long id,
                 String login,
                 String password,
                 String email,
                 String firstName,
                 String lastName,
                 String employeeId,
                 String department,
                 double salary) {
        super(id, login, password, email, firstName, lastName, employeeId, department, salary);
    }

    public void addUser(User u) {
        if (u != null && !users.contains(u)) {
            users.add(u);
            logs.add(new Log(
                    "Admin added user: " + u,
                    LocalDateTime.now(),
                    this
            ));
        }
    }

    public void removeUser(Long id) {
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getId().equals(id)) {
                iterator.remove();
                logs.add(new Log(
                        "Admin removed user with id: " + id,
                        LocalDateTime.now(),
                        this
                ));
                return;
            }
        }
    }

    public void updateUser(User u) {
        if (u == null) return;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(u.getId())) {
                users.set(i, u);
                logs.add(new Log(
                        "Admin updated user: " + u,
                        LocalDateTime.now(),
                        this
                ));
                return;
            }
        }
    }

    public List<Log> viewLogs() {
        return new ArrayList<>(logs);
    }

    public void blockUser(Long id) {
        if (!blockedUserIds.contains(id)) {
            blockedUserIds.add(id);
            logs.add(new Log(
                    "Admin blocked user with id: " + id,
                    LocalDateTime.now(),
                    this
            ));
        }
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<Long> getBlockedUserIds() {
        return new ArrayList<>(blockedUserIds);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "employeeId='" + getEmployeeId() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                '}';
    }
}