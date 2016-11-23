package minor.com.dailylogs;

/**
 * Created by vicky on 9/18/2016.
 */
public class LogsProperties {

    private String title;
    private String logs;
    private long id;

    public String getTitle() {
        return title;
    }

    public String getLogs() {
        return logs;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLogs(String log) {
        this.logs = log;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
