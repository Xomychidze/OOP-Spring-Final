package university.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Complaint implements Serializable {

    private static final long serialVersionUID = 1L;

    private Employee filer;
    private String reason;
    private LocalDateTime filedAt;
    private String status; 

    public Complaint() {}

    public Complaint(Employee filer, String reason) {
        this.filer = filer;
        this.reason = reason;
        this.filedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Employee getFiler() { return filer; }
    public String getReason() { return reason; }
    public LocalDateTime getFiledAt() { return filedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Complaint{filer=" + (filer != null ? filer.getLogin() : "?") +
               ", reason='" + reason + "', status='" + status + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complaint c)) return false;
        return Objects.equals(filer, c.filer) &&
               Objects.equals(reason, c.reason) &&
               Objects.equals(filedAt, c.filedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filer, reason, filedAt);
    }
}
