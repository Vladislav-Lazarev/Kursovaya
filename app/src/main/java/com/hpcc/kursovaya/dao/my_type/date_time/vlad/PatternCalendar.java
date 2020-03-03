package com.hpcc.kursovaya.dao.my_type.date_time.vlad;

public enum PatternCalendar {
    ERA(0),
    YEAR(1),
    MONTH(2),
    WEEK_OF_YEAR(3),
    WEEK_OF_MONTH(4),
    DATE(5),
    DAY_OF_MONTH(5),
    DAY_OF_YEAR(6),
    DAY_OF_WEEK(7),
    DAY_OF_WEEK_IN_MONTH(8),
    AM_PM(9),
    HOUR(10),
    HOUR_OF_DAY(11),
    MINUTE(12),
    SECOND(13),
    MILLISECOND(14),
    ZONE_OFFSET(15),
    DST_OFFSET(16),
    FIELD_COUNT(17);

    private int number;

    PatternCalendar(int number) {
        this.number = number;
    }

    public int number() {
        return number;
    }
}
