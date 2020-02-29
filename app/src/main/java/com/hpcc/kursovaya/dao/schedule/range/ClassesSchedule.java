package com.hpcc.kursovaya.dao.schedule.range;

public abstract class ClassesSchedule {
    protected DateRange dateRange;

    {
        dateRange = new DateRange();
    }
    ClassesSchedule() {

    }
    public ClassesSchedule(DateRange dateRange) {
        setDateRange(dateRange);
    }

    public DateRange getDateRange() {
        return dateRange;
    }
    public ClassesSchedule setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
        return this;
    }

    @Override
    public String toString() {
        return "ClassesSchedule{" +
                "dateRange=" + dateRange +
                '}';
    }
}
