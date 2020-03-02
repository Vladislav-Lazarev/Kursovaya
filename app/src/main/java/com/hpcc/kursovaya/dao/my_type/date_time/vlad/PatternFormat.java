package com.hpcc.kursovaya.dao.my_type.date_time.vlad;

public enum  PatternFormat {
    DATE_DMY("dd/MM/yyyy"), // Дата с представлением день-месяц-год
    DATE_YMD("yyyy/MM/dd"), // Дата с представлением год-месяц-день
    TIME_HM("HH:mm"), // Время с представлением час:минута
    TIME_HMS(TIME_HM.format + ":ss"), // Время с представлением час:минута:секунда
    TIME_HMSMS(TIME_HMS.format + "SS"), // Время с представлением час:минута:секунда:милисекунда
    WEEK_TEXT("EEEE"), // День недели текстом
    WEEK_NUMBER("uu"), // День недели числом
    TIME_ZONE("X"), // Часовой пояс
    DATE_TIME(DATE_DMY.format + " " + TIME_HMS.format), // Представление год-месяц-день час:минута
    DATE_FULL(DATE_YMD.format + " " + TIME_HMSMS.format + " " + TIME_ZONE.format); // Полное представление год-месяц-день час:минута:секунда:милисекунда часовой пояса

    private String format;

    PatternFormat(String format) {
        this.format = format;
    }

    public String text() {
        return format;
    }
}
