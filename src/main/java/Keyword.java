import java.util.ArrayList;
import java.util.List;

public class Keyword {

    String word = "";
    String frequency = "";
    List<String> competitorsByKeyword = new ArrayList<>();

    Keyword(String word, String frequency, List<String> competitorsByKeyword) {

        this.word = word;
        this.frequency = frequency;
        this.competitorsByKeyword = competitorsByKeyword;

    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<String> getCompetitorsByKeyword() {
        return competitorsByKeyword;
    }

    public void setCompetitorsByKeyword(List<String> competitorsByKeyword) {
        this.competitorsByKeyword = competitorsByKeyword;
    }
}
