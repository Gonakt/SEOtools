import java.util.ArrayList;
import java.util.List;

public class TextRuUnique {

    String queue;
    String status;
    String unique;
    String water;
    String spam;
    List<PlagiatLink> links = new ArrayList<>();

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getSpam() {
        return spam;
    }

    public void setSpam(String spam) {
        this.spam = spam;
    }

    public List<PlagiatLink> getLinks() {
        return links;
    }

    public void setLinks(List<PlagiatLink> links) {
        this.links = links;
    }
}
