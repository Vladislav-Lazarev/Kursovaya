package com.hpcc.kursovaya.dao.my_type.date_time.vlad;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;

public class RealmDateTime_Vlad extends RealmObject implements Comparable<RealmDateTime_Vlad> {
    private static Calendar calendar;// Для манипулирования с классом Date
    private static SimpleDateFormat simpleDateFormat;// Для форматирования с классом Date

    static {
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat(PatternFormat.DATE_FULL.text(), Locale.ENGLISH);
    }

    // Дату выводит строкой по pattern
    public static String format(Date date, PatternFormat pattern) {
        simpleDateFormat.applyPattern(pattern.text());
        return simpleDateFormat.format(date);
    }
    public static String format(Calendar date, PatternFormat pattern) {
        simpleDateFormat.applyPattern(pattern.text());
        return simpleDateFormat.format(date.getTime());
    }

    // Выводит аттрибуты по частям
    public static int get(Date date, PatternCalendar pattern) {
        calendar.setTime(date);
        return calendar.get(pattern.ordinal());
    }

    // По pattern добавляет, отнимает значения в date
    public static Date add(Date date, PatternCalendar pattern, int amount) {
        calendar.setTime(date);
        calendar.add(pattern.number(), amount);
        return calendar.getTime();
    }

    // Проверка на высокосный год
    public static boolean isLeapYear(int year){
        calendar.set(year, Calendar.FEBRUARY, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH) == 29;
    }

    // Выводит день недели(число)
    public static int getFistDayOfWeek(Date date) {
        calendar.setTime(date);
        return calendar.getFirstDayOfWeek();
    }

    // Текущая дата
    public static RealmDateTime_Vlad currentDateTime() {
        RealmDateTime_Vlad realmDateTime = new RealmDateTime_Vlad();
        realmDateTime.date = Calendar.getInstance().getTime();
        return realmDateTime;
    }

    private Date date;// Внутри класса: дата, время

    {
        date = new Date(0);
    }
    public RealmDateTime_Vlad() {

    }
    public RealmDateTime_Vlad(String date, PatternFormat pattern) {
        setPattern(date, pattern);
    }
    public RealmDateTime_Vlad(String date) {
        setFull(date);
    }

    public RealmDateTime_Vlad(Date date, PatternFormat pattern) {
        setPattern(date, pattern);
    }
    public RealmDateTime_Vlad(Date date) {
        setFull(date);
    }

    public RealmDateTime_Vlad(Calendar date) {
        setDate(calendar.getTime());
    }

    // date в String
    public String getString() {
        return date.toString();
    }
    // Принимает String по шаблону
    public RealmDateTime_Vlad setPattern(String date, PatternFormat pattern) {
        simpleDateFormat.applyPattern(pattern.text());

        try {
            this.date = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            System.out.println("setPattern()");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return this;
    }
    public RealmDateTime_Vlad setFull(String date) {
        return setPattern(date, PatternFormat.DATE_FULL);
    }
    public RealmDateTime_Vlad setDateTime(String date) {
        return setPattern(date, PatternFormat.DATE_TIME);
    }
    public RealmDateTime_Vlad setDate(String date) {
        return setPattern(date, PatternFormat.DATE_DMY);
    }
    public RealmDateTime_Vlad setTime(String date) {
        return setPattern(date, PatternFormat.TIME_HM);
    }

    // date в Date
    public Date getDate(){
        return date;
    }
    // Принимает Date по шаблону
    public RealmDateTime_Vlad setPattern(Date date, PatternFormat pattern) {
        setPattern(format(date, pattern), pattern);
        return this;
    }
    public RealmDateTime_Vlad setFull(Date date) {
        return setPattern(date, PatternFormat.DATE_FULL);
    }
    public RealmDateTime_Vlad setDateTime(Date date) {
        return setPattern(date, PatternFormat.DATE_TIME);
    }
    public RealmDateTime_Vlad setDate(Date date) {
        return setPattern(date, PatternFormat.DATE_DMY);
    }
    public RealmDateTime_Vlad setTime(Date date) {
        return setPattern(date, PatternFormat.TIME_HM);
    }

    // date в Calendar
    public Calendar getCalendar(){
        calendar.setTime(date);
        return calendar;
    }
    // Принимает Calendar по шаблону
    public RealmDateTime_Vlad setPattern(Calendar date, PatternFormat pattern) {
        setPattern(format(date, pattern), pattern);
        return this;
    }
    public RealmDateTime_Vlad setFull(Calendar date) {
        return setPattern(date, PatternFormat.DATE_FULL);
    }
    public RealmDateTime_Vlad setDateTime(Calendar date) {
        return setPattern(date, PatternFormat.DATE_TIME);
    }
    public RealmDateTime_Vlad setDate(Calendar date) {
        return setPattern(date, PatternFormat.DATE_DMY);
    }
    public RealmDateTime_Vlad setTime(Calendar date) {
        return setPattern(date, PatternFormat.TIME_HM);
    }

    // Дату выводит строкой по pattern
    public String format(PatternFormat pattern) {
        return format(date, pattern);
    }

    // Выводит аттрибуты по частям
    public int get(PatternCalendar pattern) {
        return get(date, pattern);
    }

    // По pattern добавляет, отнимает значения в date
    public void add(PatternCalendar pattern, int amount) {
        date = add(date, pattern,amount);
    }

    // Сравнение
    @Override
    public int compareTo(RealmDateTime_Vlad realmDateTime) {
        calendar.setTime(date);

        Calendar calendarOther = Calendar.getInstance();
        calendarOther.setTime(realmDateTime.date);

        return calendar.compareTo(calendarOther);
    }
    // По pattern сравнивает dateTime
    public int compareTo(RealmDateTime_Vlad realmDateTime, PatternCalendar pattern) {
        calendar.setTime(date);

        Calendar calendarOther = Calendar.getInstance();
        calendarOther.setTime(realmDateTime.date);

        return Integer.compare(calendar.get(pattern.ordinal()), calendarOther.get(pattern.ordinal()));
    }

    @Override
    public String toString() {
        return "DateTime{" +
                "date=" + date +
                '}';
    }


}
