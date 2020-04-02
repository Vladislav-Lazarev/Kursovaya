package com.hpcc.kursovaya.dao.entity.calendar;

import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Holiday extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Назани(имя) праздника
    private Date date;// Дата проведения праздника

    public Holiday() {
        id = 0;
        name = "";
        date = new Date();
    }
    public Holiday(int id, @NotNull String name, @NotNull Date date) {
        this();
        setId(id);
        setName(name);
        setDate(date);
    }

    private void setId(int id){
        try{
            if (id < ConstantApplication.ONE){
                throw new Exception("Exception! setId()");
            }
            this.id = id;
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    public int getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }
    public Holiday setName(@NotNull String name) {
        // TODO setName - проверка
        this.name = name;
        return this;
    }

    @NotNull
    public Date getDate() {
        return date;
    }
    public Holiday setDate(@NotNull Date dateTime) {
        // TODO setRealmDateTime - проверка
        this.date = dateTime;
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
