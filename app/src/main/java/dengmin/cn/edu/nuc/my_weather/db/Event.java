package dengmin.cn.edu.nuc.my_weather.db;

import org.litepal.crud.DataSupport;

public class Event extends DataSupport {
    private int id;
    private String time;
    private String data;
    private String title;
    private String content;
    private String hz;
    private String remind_time;
    private int remindtime;

    public String getRemind_time() {
        return remind_time;
    }

    public void setRemind_time(String remind_time) {
        this.remind_time = remind_time;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRemindtime() {
        return remindtime;
    }

    public void setRemindtime(int remindtime) {
        this.remindtime = remindtime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHz() {
        return hz;
    }

    public void setHz(String hz) {
        this.hz = hz;
    }
}
