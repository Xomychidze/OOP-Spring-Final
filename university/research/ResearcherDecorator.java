package university.research;

import university.models.Employee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResearcherDecorator implements Researcher, Serializable {

    private static final long serialVersionUID = 1L;

    private Employee wrapped;
    private int hIndex;
    private List<ResearchPaper> papers;
    private List<ResearchProject> projects;

    public ResearcherDecorator(Employee wrapped, int hIndex) {
        this.wrapped = wrapped;
        this.hIndex = hIndex;
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> c) {
        List<ResearchPaper> sortedPapers = new ArrayList<>(papers);

        if (c != null) {
            sortedPapers.sort(c);
        } else {
            sortedPapers.sort(Comparator.naturalOrder());
        }

        for (ResearchPaper paper : sortedPapers) {
            System.out.println(paper);
        }
    }

    @Override
    public int getHIndex() {
        return hIndex;
    }

    @Override
    public void addPaper(ResearchPaper p) {
        if (p != null && !papers.contains(p)) {
            papers.add(p);
        }
    }

    @Override
    public void joinProject(ResearchProject p) {
        if (p != null && !projects.contains(p)) {
            projects.add(p);
            p.addParticipant(this);
        }
    }

    public Employee getWrapped() {
        return wrapped;
    }

    public void setWrapped(Employee wrapped) {
        this.wrapped = wrapped;
    }

    public void setHIndex(int hIndex) {
        this.hIndex = hIndex;
    }

    public List<ResearchPaper> getPapers() {
        return papers;
    }

    public void setPapers(List<ResearchPaper> papers) {
        this.papers = papers;
    }

    public List<ResearchProject> getProjects() {
        return projects;
    }

    public void setProjects(List<ResearchProject> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "ResearcherDecorator{" +
                "wrapped=" + wrapped +
                ", hIndex=" + hIndex +
                ", papers=" + papers.size() +
                ", projects=" + projects.size() +
                '}';
    }
}
