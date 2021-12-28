import java.util.ArrayList;
import java.util.List;

public class EtxtTextUnique {

    String unique;
    List<PlagiatLink> link = new ArrayList<>();
    String queue;
    String checkPercent; //процент проверки текущего текста
    String msg;

    public List<PlagiatLink> getLink() {
        return link;
    }

    public void setLink(List<PlagiatLink> link) {
        this.link = link;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getCheckPercent() {
        return checkPercent;
    }

    public void setCheckPercent(String checkPercent) {
        this.checkPercent = checkPercent;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

}
