package com.hpcc.kursovaya.dao.entity.schedule.call;

import com.hpcc.kursovaya.dao.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CallLesson extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private RealmList<CallAcademicHour> callAcademicHourList;// Звонок на пару, занятие, пара(2 полупары)

    {
        id = 0;
        callAcademicHourList = new RealmList<>();
    }
    public CallLesson() {

    }
    public CallLesson(int id, @NotNull RealmList<CallAcademicHour> callAcademicHourList) {
        setId(id);
        setCallAcademicHourList(callAcademicHourList);
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
    public RealmList<CallAcademicHour> getCallAcademicHourList() {
        return callAcademicHourList;
    }
    public CallLesson setCallAcademicHourList(@NotNull RealmList<CallAcademicHour> callAcademicHourList) {
        // TODO setCallAcademicHourList - проверка
        /*try {
            if () {
                throw new Exception("Exception! setCallAcademicHourList()");
            }
            this.callAcademicHourList = callAcademicHourList;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }*/
        return this;
    }

    @Override
    public String toString() {
        return "CallClasses{" +
                "id=" + id +
                ", callAcademicHourList=" + callAcademicHourList.toString() +
                '}';
    }
}
