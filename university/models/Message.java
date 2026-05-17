package university.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private Employee sender;
    private Employee recipient;
    private String text;
    private LocalDateTime sentAt;

    public Message() {}

    public Message(Employee sender, Employee recipient, String text) {
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        this.sentAt = LocalDateTime.now();
    }

    public Employee getSender() { return sender; }
    public Employee getRecipient() { return recipient; }
    public String getText() { return text; }
    public LocalDateTime getSentAt() { return sentAt; }

    @Override
    public String toString() {
        return "Message{from=" + (sender != null ? sender.getLogin() : "?") +
               ", to=" + (recipient != null ? recipient.getLogin() : "?") +
               ", text='" + text + "', at=" + sentAt + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message m)) return false;
        return Objects.equals(sender, m.sender) &&
               Objects.equals(recipient, m.recipient) &&
               Objects.equals(text, m.text) &&
               Objects.equals(sentAt, m.sentAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, recipient, text, sentAt);
    }
}
