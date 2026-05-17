package university.research;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResearchPaper implements Serializable, Comparable<ResearchPaper> {

    private static final long serialVersionUID = 1L;

    private String title;
    private List<String> authors;
    private String journal;
    private String doi;
    private int citations;
    private int pages;
    private LocalDate date;
    private String abstractText;

    public ResearchPaper() {
        this.authors = new ArrayList<>();
    }

    public ResearchPaper(String title,
                         List<String> authors,
                         String journal,
                         String doi,
                         int citations,
                         int pages,
                         LocalDate date,
                         String abstractText) {
        this.title = title;
        this.authors = authors != null ? authors : new ArrayList<>();
        this.journal = journal;
        this.doi = doi;
        this.citations = citations;
        this.pages = pages;
        this.date = date;
        this.abstractText = abstractText;
    }

    @Override
    public int compareTo(ResearchPaper o) {
        if (o == null) {
            return -1;
        }

        int citationCompare = Integer.compare(o.citations, this.citations);
        if (citationCompare != 0) {
            return citationCompare;
        }

        if (this.date != null && o.date != null) {
            int dateCompare = o.date.compareTo(this.date);
            if (dateCompare != 0) {
                return dateCompare;
            }
        } else if (this.date != null) {
            return -1;
        } else if (o.date != null) {
            return 1;
        }

        if (this.title == null && o.title == null) {
            return 0;
        }
        if (this.title == null) {
            return 1;
        }
        if (o.title == null) {
            return -1;
        }

        return this.title.compareToIgnoreCase(o.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResearchPaper)) {
            return false;
        }

        ResearchPaper that = (ResearchPaper) o;

        if (doi != null && !doi.isBlank() && that.doi != null && !that.doi.isBlank()) {
            return doi.equalsIgnoreCase(that.doi);
        }

        return Objects.equals(title, that.title)
                && Objects.equals(authors, that.authors)
                && Objects.equals(journal, that.journal)
                && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        if (doi != null && !doi.isBlank()) {
            return doi.toLowerCase().hashCode();
        }
        return Objects.hash(title, authors, journal, date);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public int getCitations() {
        return citations;
    }

    public void setCitations(int citations) {
        this.citations = citations;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    @Override
    public String toString() {
        return "ResearchPaper{" +
                "title='" + title + '\'' +
                ", authors=" + authors +
                ", journal='" + journal + '\'' +
                ", doi='" + doi + '\'' +
                ", citations=" + citations +
                ", pages=" + pages +
                ", date=" + date +
                '}';
    }
}
