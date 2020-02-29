package com.hpcc.kursovaya.dao.calendar;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Holiday extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Назани(имя) праздника
    private Date date;// Дата проведения праздника

    {
        id = 0;
        name = "";
        date = new Date();
    }
    public Holiday() {

    }
    public Holiday(Date date, String name) {
        setName(name);
        setDate(date);
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

    public Date getDate() {
        return date;
    }
    public Holiday setDate(Date date) {
        this.date = date;
        return this;
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                '}';
    }
}
