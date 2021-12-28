public class SearchResultPosition {

    String site = "";
    String query = "";
    int yaPosition = -1;
    int gPosition = -1;

    SearchResultPosition(String site, String query, int yaPosition, int gPosition) {

        this.site = site;
        this.query = query;
        this.yaPosition = yaPosition;
        this.gPosition = gPosition;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getYaPosition() {
        return yaPosition;
    }

    public void setYaPosition(int yaPosition) {
        this.yaPosition = yaPosition;
    }

    public int getgPosition() {
        return gPosition;
    }

    public void setgPosition(int gPosition) {
        this.gPosition = gPosition;
    }
}
