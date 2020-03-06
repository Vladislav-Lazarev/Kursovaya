package com.hpcc.kursovaya.dao.entity.schedule.call;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.my_type.date_time.maks.RealmDateTime;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CallAcademicHour extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private RealmDateTime startDateTime;// Начало пары
    private RealmDateTime endDateTime;// Начало пары

    {
        id = 0;
        startDateTime = new RealmDateTime();
        endDateTime = new RealmDateTime();
    }
    public CallAcademicHour() {

    }
    public CallAcademicHour(int id, @NotNull RealmDateTime startDateTime, @NotNull RealmDateTime endDateTime) {
        setId(id);
        setStartDateTime(startDateTime);
        setEndDateTime(endDateTime);
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
    public RealmDateTime getStartDateTime() {
        return startDateTime;
    }
    public CallAcademicHour setStartDateTime(@NotNull RealmDateTime startDateTime) {
        // TODO setStartDateTime - провекра
        this.startDateTime = startDateTime;
        return this;
    }

    @NotNull
    public RealmDateTime getEndDateTime() {
        return endDateTime;
    }
    public CallAcademicHour setEndDateTime(@NotNull RealmDateTime endDateTime) {
        // TODO setEndDateTime - провекра
        this.endDateTime = endDateTime;
        return this;
    }

    @Override
    public String toString() {
        return "CallAcademicHour{" +
                "id=" + id +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                '}';
    }
}