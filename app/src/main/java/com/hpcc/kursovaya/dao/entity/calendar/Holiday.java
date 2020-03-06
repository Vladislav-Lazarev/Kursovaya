package com.hpcc.kursovaya.dao.entity.calendar;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.my_type.date_time.maks.RealmDateTime;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Holiday extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Назани(имя) праздника
    private RealmDateTime dateTime;// Дата проведения праздника

    {
        id = 0;
        name = "";
        dateTime = new RealmDateTime();
    }
    public Holiday() {

    }
    public Holiday(int id, @NotNull String name, @NotNull RealmDateTime dateTime) {
        setId(id);
        setName(name);
        setDateTime(dateTime);
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
    public RealmDateTime getRealmDateTime() {
        return dateTime;
    }
    public Holiday setDateTime(@NotNull RealmDateTime dateTime) {
        // TODO setRealmDateTime - проверка
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
