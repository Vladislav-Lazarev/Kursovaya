package com.hpcc.kursovaya.dao.schedule.range;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;

public class DateRange extends RealmObject {
    // TODO DateRange

    private static Calendar calendar;// Для манипулирования с классом Date
    private static SimpleDateFormat simpleDateFormat;// Для форматирования с классом Date

    public static String PATTERN_DATE = "dd-MM-yyyy";
    public static String PATTERN_WEEK_TEXT = "E";
    public static String PATTERN_WEEK_NUMBER = "u";
    public static String PATTERN_TIME = "HH:mm:ss";

    static {
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat(PATTERN_DATE + " " + PATTERN_TIME + " "
                + PATTERN_WEEK_TEXT + " (" + PATTERN_WEEK_NUMBER + ")");
    }

    public static String dateFormat(Date date) {
        return simpleDateFormat.format(date);
    }
    public static String  dateFormat(Date date, String pattern) {
        simpleDateFormat.applyPattern(pattern);
        return simpleDateFormat.format(date);
    }

    private Date start;// Начало даты
    private Date end;// Конец даты

    /*protected static void checkTime(Date start, Date end, Date compare, int amount) throws Exception {
        if (end.compareTo(compare) == 0) {
            calendar.setTime(start);
            calendar.add(Calendar.MINUTE, amount);
            start.setDate(calendar.getTime().getDate());
        } else {
            if (start.compareTo(end) > -1) {
                throw new Exception("Exception! setStartTime()");
            }
        }
    }*/

    {
        start = new Date(0);
        end = new Date(0);
    }
    public DateRange() {

    }
    public DateRange(Date start, Date end) {
        setStartDateFull(start);
        setEndDateFull(end);
    }

    public Date getStart(){
        return start;
    }
    public DateRange setStartDateFull(Date start) {

        return this;
    }
    public DateRange setStartDate(Date start) {

        return this;
    }
    public DateRange setStartTime(Date start) {
        /*Date defaultDate = new Date(0);

        try {
            if (startTime.compareTo(defaultDate) < 0){
                throw new Exception("Exception! setStartTime()");
            }
            this._startTime = startTime;
            time(_startTime, _endTime, defaultDate, 45);
             *//*if (_endTime.compareTo(defaultDate) == 0) {
                calendar.setTime(_startTime);
                calendar.add(Calendar.MINUTE, 45);
                _endTime = calendar.getTime();
            } else {
                if (_startTime.compareTo(_endTime) > -1) {
                    throw new Exception("Exception! setStartTime() -> switch");
                }
            }*//*
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }*/
        return this;
    }

    public Date getEnd(){
        return start;
    }
    public DateRange setEndDateFull(Date start) {

        return this;
    }
    public DateRange setEndDate(Date start) {

        return this;
    }
    public DateRange setEndTime(Date start) {

        return this;
    }

    @Override
    public String toString() {
        return "DateRange{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
