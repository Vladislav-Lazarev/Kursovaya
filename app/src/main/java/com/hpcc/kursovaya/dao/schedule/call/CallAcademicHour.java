package com.hpcc.kursovaya.dao.schedule.call;

import com.hpcc.kursovaya.dao.schedule.range.DateRange;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CallAcademicHour extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private DateRange dateRange;// Диапозон дат

    {
        dateRange = new DateRange();
    }
    public CallAcademicHour() {

    }
    public CallAcademicHour(DateRange dateRange) {
        setDateRange(dateRange);
    }

    public int getId() {
        return id;
    }

    public DateRange getDateRange() {
        return dateRange;
    }
    public CallAcademicHour setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
        return this;
    }

    @Override
    public String toString() {
        return "CallAcademicHour{" +
                "id=" + id +
                ", dateRange=" + dateRange +
                '}';
    }
}