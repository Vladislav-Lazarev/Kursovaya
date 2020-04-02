package com.hpcc.kursovaya.dao.entity.calendar;

import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HolidayCalendar extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private RealmList<Holiday> holidayList;// Календарь(список) праздничных дней

    public HolidayCalendar() {
        id = 0;
        holidayList = new RealmList<>();
    }
    public HolidayCalendar(int id, @NotNull RealmList<Holiday> holidayList) {
        this();
        setId(id);
        setHolidayList(holidayList);
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
    public RealmList<Holiday> getHolidayList() {
        return holidayList;
    }
    public HolidayCalendar setHolidayList(@NotNull RealmList<Holiday> holidayList) {
        // TODO setHolidayList - проверка
        this.holidayList = holidayList;
        return this;
    }

    @Override
    public String toString() {
        return "HolidayCalendar{" +
                "id=" + id +
                ", holidayList=" + holidayList.toString() +
                '}';
    }
}
