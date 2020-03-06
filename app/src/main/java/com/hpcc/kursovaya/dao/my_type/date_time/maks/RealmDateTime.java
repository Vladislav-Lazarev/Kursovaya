package com.hpcc.kursovaya.dao.my_type.date_time.maks;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class RealmDateTime extends RealmObject {
    private static DateTimeFormatter dateTimeFormatter;// Для форматирования с классом Date
    private static DateTimeFormat dateTimeFormat;// Для форматирования с классом Date

    public static String MIN = "";
    public static String MAX = "";

    private Date date;
    @Ignore
    private DateTime dateTime;// Для манипулирования с классом Date

    public RealmDateTime() {
    }
}

