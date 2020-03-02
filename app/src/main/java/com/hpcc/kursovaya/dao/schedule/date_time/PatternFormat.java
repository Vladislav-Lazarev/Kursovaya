package com.hpcc.kursovaya.dao.schedule.date_time;

public enum  PatternFormat {
    DATE_DMY("dd/MM/yyyy"), // Дата с представлением день-месяц-год
    DATE_YMD("yyyy/MM/dd"), // Дата с представлением год-месяц-день
    TIME_HM("HH:mm"), // Время с представлением час:минута
    TIME_HMS(TIME_HM + ":ss"), // Время с представлением час:минута:секунда
    TIME_HMSMS(TIME_HMS + "SSSS"), // Время с представлением час:минута:секунда:милисекунда
    WEEK_TEXT("EEEE"), // День недели текстом
    WEEK_NUMBER("uu"), // День недели числом
    TIME_ZONE("X"), // Часовой пояс
    DATE_TIME(DATE_DMY + " " + TIME_HM), // Представление год-месяц-день час:минута
    DATE_FULL(DATE_YMD + " " + TIME_HMSMS + " " + TIME_ZONE); // Полное представление год-месяц-день час:минута:секунда:милисекунда часовой пояса

    private String format;

    PatternFormat(String format){
        this.format = format;
    }

    public String formatText() {
        return format;
    }

    @Override
    public String toString() {
        return "PatternFormat{" +
                "format='" + format + '\'' +
                '}';
    }
}
