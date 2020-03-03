package com.hpcc.kursovaya.dao.schedule.call;

import com.hpcc.kursovaya.dao.schedule.date_time.DateTime;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CallAcademicHour extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private DateTime dateTime;// Диапозон дат

    {
        dateTime = new DateTime();
    }
    public CallAcademicHour() {

    }
    public CallAcademicHour(DateTime dateTime) {
        setDateTime(dateTime);
    }

    public int getId() {
        return id;
    }

    public DateTime getDateTime() {
        return dateTime;
    }
    public CallAcademicHour setDateTime(DateTime dateTimeRange) {
        this.dateTime = dateTimeRange;
        return this;
    }

    @Override
    public String toString() {
        return "CallAcademicHour{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                '}';
    }
}