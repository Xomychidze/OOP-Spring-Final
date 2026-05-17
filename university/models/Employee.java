package university.models;

import java.util.ArrayList;
import java.util.List;

public abstract class Employee extends User {

    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String department;
    private double salary;

    // Shared inbox — any employee can receive messages
    private final List<Message> inbox = new ArrayList<>();
    private final List<Complaint> complaints = new ArrayList<>();

    public Employee() {}

    public Employee(Long id, String login, String password,
                    String email, String firstName, String lastName,
                    String employeeId, String department, double salary) {
        super(id, login, password, email, firstName, lastName);
        this.employeeId = employeeId;
        this.department = department;
        this.salary = salary;
    }

    public void sendMessage(Employee recipient, String text) {
        if (recipient == null || text == null || text.isBlank()) return;
        Message msg = new Message(this, recipient, text);
        recipient.receiveMessage(msg);
        System.out.println("[MSG] " + getFirstName() + " → " + recipient.getFirstName() + ": " + text);
    }

    public void receiveMessage(Message msg) {
        inbox.add(msg);
    }

    public void sendComplaint(String reason) {
        if (reason == null || reason.isBlank()) return;
        Complaint complaint = new Complaint(this, reason);
        complaints.add(complaint);
        System.out.println("[COMPLAINT] " + getFirstName() + " filed: " + reason);
    }

    public List<Message> getInbox() { return new ArrayList<>(inbox); }
    public List<Complaint> getComplaints() { return new ArrayList<>(complaints); }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}
