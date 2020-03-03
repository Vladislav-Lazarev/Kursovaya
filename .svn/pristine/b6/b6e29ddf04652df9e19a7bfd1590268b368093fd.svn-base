package com.hpcc.kursovaya.dao.calendar;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HolidayCalendar extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private RealmList<Holiday> holidayList;// Календарь(список) праздничных дней

    {
        id = 0;
        holidayList = new RealmList<>();
    }
    public HolidayCalendar() {

    }
    public HolidayCalendar(RealmList<Holiday> holidayList) {
        setHolidayList(holidayList);
    }

    public int getId() {
        return id;
    }

    public RealmList<Holiday> getHolidayList() {
        return holidayList;
    }
    public HolidayCalendar setHolidayList(RealmList<Holiday> holidayList) {
        this.holidayList = holidayList;
        return this;
    }

    @Override
    public String toString() {
        return "HolidayCalendar{" +
                "id=" + id +
                ", holidayList=" + holidayList +
                '}';
    }
}
