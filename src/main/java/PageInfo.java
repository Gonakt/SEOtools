public class PageInfo {

    private String httpResponse = "неизвестно";
    private String time = "неизвестно";

    PageInfo(String HttpResponse, String time) {

        this.httpResponse = HttpResponse;
        this.time = time;
    }

    public String getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(String httpResponse) {
        this.httpResponse = httpResponse;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
