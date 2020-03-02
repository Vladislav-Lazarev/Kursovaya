package com.hpcc.kursovaya.dao.calendar;

import com.hpcc.kursovaya.dao.schedule.date_time.DateTime;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Holiday extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Назани(имя) праздника
    private DateTime dateTime;// Дата проведения праздника

    {
        id = 0;
        name = "";
        dateTime = new DateTime();
    }
    public Holiday() {

    }
    public Holiday(DateTime dateTime, String name) {
        setName(name);
        setDateTime(dateTime);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public Holiday setName(String name) {
        this.name = name;
        return this;
    }

    public DateTime getDateTime() {
        return dateTime;
    }
    public Holiday setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
