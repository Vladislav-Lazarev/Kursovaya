package com.hpcc.kursovaya.dao.entity.calendar;

import com.hpcc.kursovaya.dao.ConstantEntity;

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
        setDateTime(date);
    }

    private void setId(int id){
        try{
            if (id < ConstantEntity.ONE){
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
    public Date getRealmDateTime() {
        return date;
    }
    public Holiday setDateTime(@NotNull Date date) {
        // TODO setRealmDateTime - проверка
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
