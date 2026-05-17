package university.research;

import java.util.Comparator;

public interface Researcher {

    void printPapers(Comparator<ResearchPaper> c);

    int getHIndex();

    void addPaper(ResearchPaper p);

    void joinProject(ResearchProject p);
}
