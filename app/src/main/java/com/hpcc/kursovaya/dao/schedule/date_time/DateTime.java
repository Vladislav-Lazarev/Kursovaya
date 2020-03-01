package com.hpcc.kursovaya.dao.schedule.date_time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;

public class DateTime extends RealmObject implements Comparable<DateTime> {
    private static Calendar calendar;// Для манипулирования с классом Date
    private static SimpleDateFormat simpleDateFormat;// Для форматирования с классом Date

    static {
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat(PatternFormat.DATE_FULL.text(), Locale.ENGLISH);
    }

    public static String format(Date date, PatternFormat pattern) {
        simpleDateFormat.applyPattern(pattern.text());
        return simpleDateFormat.format(date);
    }
    public static String format(Calendar date, PatternFormat pattern) {
        simpleDateFormat.applyPattern(pattern.text());
        return simpleDateFormat.format(date.getTime());
    }

    public static int get(Date date, PatternCalendar pattern) {
        calendar.setTime(date);
        return calendar.get(pattern.ordinal());
    }

    public static boolean isLeapYear(int year){
        calendar.set(year, Calendar.FEBRUARY, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH) == 29;
    }

    public static int getFistDayOfWeek(Date date) {
        calendar.setTime(date);
        return calendar.getFirstDayOfWeek();
    }

    public static DateTime currentDateTime() {
        DateTime dateTime = new DateTime();
        dateTime.date = Calendar.getInstance().getTime();
        return dateTime;
    }

    private Date date;// Внутри класса: дата, время

    {
        date = new Date(0);
    }
    public DateTime() {

    }
    public DateTime(String date, PatternFormat pattern) {
        setPattern(date, pattern);
    }
    public DateTime(String date) {
        setFull(date);
    }

    public DateTime(Date date, PatternFormat pattern) {
        setPattern(date, pattern);
    }
    public DateTime(Date date) {
        setFull(date);
    }

    public DateTime(Calendar date) {
        setDate(calendar.getTime());
    }

    public String getString() {
        return date.toString();
    }
    public DateTime setPattern(String date, PatternFormat pattern) {
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
    public DateTime setFull(String date) {
        return setPattern(date, PatternFormat.DATE_FULL);
    }
    public DateTime setDateTime(String date) {
        return setPattern(date, PatternFormat.DATE_TIME);
    }
    public DateTime setDate(String date) {
        return setPattern(date, PatternFormat.DATE_DMY);
    }
    public DateTime setTime(String date) {
        return setPattern(date, PatternFormat.TIME_HM);
    }

    public Date getDate(){
        return date;
    }
    public DateTime setPattern(Date date, PatternFormat pattern) {
        setPattern(format(date, pattern), pattern);
        return this;
    }
    public DateTime setFull(Date date) {
        return setPattern(date, PatternFormat.DATE_FULL);
    }
    public DateTime setDateTime(Date date) {
        return setPattern(date, PatternFormat.DATE_TIME);
    }
    public DateTime setDate(Date date) {
        return setPattern(date, PatternFormat.DATE_DMY);
    }
    public DateTime setTime(Date date) {
        return setPattern(date, PatternFormat.TIME_HM);
    }

    public Calendar getCalendar(){
        calendar.setTime(date);
        return calendar;
    }
    public DateTime setPattern(Calendar date, PatternFormat pattern) {
        setPattern(format(date, pattern), pattern);
        return this;
    }
    public DateTime setFull(Calendar date) {
        return setPattern(date, PatternFormat.DATE_FULL);
    }
    public DateTime setDateTime(Calendar date) {
        return setPattern(date, PatternFormat.DATE_TIME);
    }
    public DateTime setDate(Calendar date) {
        return setPattern(date, PatternFormat.DATE_DMY);
    }
    public DateTime setTime(Calendar date) {
        return setPattern(date, PatternFormat.TIME_HM);
    }

    public void add(PatternCalendar pattern, int amount) {
        calendar.setTime(date);
        calendar.add(pattern.ordinal(), amount);
    }

    @Override
    public int compareTo(DateTime dateTime) {
        calendar.setTime(date);

        Calendar calendarOther = Calendar.getInstance();
        calendarOther.setTime(dateTime.date);

        return calendar.compareTo(calendarOther);
    }
    public int compareTo(DateTime dateTime, PatternCalendar pattern) {
        calendar.setTime(date);

        Calendar calendarOther = Calendar.getInstance();
        calendarOther.setTime(dateTime.date);

        return Integer.compare(calendar.get(pattern.ordinal()), calendarOther.get(pattern.ordinal()));
    }

    @Override
    public String toString() {
        return "DateTime{" +
                "date=" + date +
                '}';
    }
}
