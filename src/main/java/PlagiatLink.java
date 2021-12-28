public class PlagiatLink {

    String link;
    String samePercent;

    PlagiatLink(String link, String samePercent) {

        this.link = link;
        this.samePercent = samePercent;

    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSamePercent() {
        return samePercent;
    }

    public void setSamePercent(String samePercent) {
        this.samePercent = samePercent;
    }
}
