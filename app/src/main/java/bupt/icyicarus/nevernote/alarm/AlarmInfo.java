package bupt.icyicarus.nevernote.alarm;

public class AlarmInfo {
    private int id = -1;
    private String year = "";
    private String month = "";
    private String day = "";
    private String hour = "";
    private String minute = "";
    private int noteid = -1;
    private String name = "";
    private String content = "";

    public AlarmInfo(int id, String year, String month, String day, String hour, String minute, int noteid, String name, String content) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.noteid = noteid;
        this.name = name;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    public int getNoteID() {
        return noteid;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
