package university.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Log implements Serializable {
    private String action;
    private LocalDateTime timestamp;
    private User user;

    public Log() {
    }

    public Log(String action, LocalDateTime timestamp, User user) {
        this.action = action;
        this.timestamp = timestamp;
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Log{" +
                "action='" + action + '\'' +
                ", timestamp=" + timestamp +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Log log)) return false;
        return Objects.equals(action, log.action)
                && Objects.equals(timestamp, log.timestamp)
                && Objects.equals(user, log.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, timestamp, user);
    }
}