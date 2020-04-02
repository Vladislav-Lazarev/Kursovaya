package com.hpcc.kursovaya.dao.entity.schedule.call;

import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CallAcademicHour extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private Date startDate;// Начало пары
    private Date endDate;// Начало пары
    
    public CallAcademicHour() {
        id = 0;
        startDate = new Date();
        endDate = new Date();
    }
    public CallAcademicHour(int id, @NotNull Date startDate, @NotNull Date endDate) {
        this();
        setId(id);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    public int getId() {
        return id;
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

    @NotNull
    public Date getStartDate() {
        return startDate;
    }
    public CallAcademicHour setStartDate(@NotNull Date startDate) {
        // TODO setStartDateTime - провекра
        this.startDate = startDate;
        return this;
    }

    @NotNull
    public Date getEndDate() {
        return endDate;
    }
    public CallAcademicHour setEndDate(@NotNull Date endDate) {
        // TODO setEndDateTime - провекра
        this.endDate = endDate;
        return this;
    }

    @Override
    public String toString() {
        return "CallAcademicHour{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}