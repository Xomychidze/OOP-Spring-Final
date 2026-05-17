package university.models;

import java.io.Serializable;
import java.util.Objects;

public class Report implements Serializable {
    private String title;
    private String content;

    public Report() {
    }

    public Report(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Report{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report report)) return false;
        return Objects.equals(title, report.title)
                && Objects.equals(content, report.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content);
    }
}