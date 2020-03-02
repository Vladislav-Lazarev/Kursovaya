package com.hpcc.kursovaya.dao.schedule.call;

import com.hpcc.kursovaya.dao.ConstantEntity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CallClasses extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private RealmList<CallAcademicHour> callAcademicHourList;// Звонок на пару, занятие, пара(2 полупары)

    {
        id = 0;
        callAcademicHourList = new RealmList<>();
    }
    public CallClasses() {

    }
    public CallClasses(RealmList<CallAcademicHour> callAcademicHourList) {
        setCallAcademicHourList(callAcademicHourList);
    }

    public int getId() {
        return id;
    }

    public RealmList<CallAcademicHour> getCallAcademicHourList() {
        return callAcademicHourList;
    }
    public CallClasses setCallAcademicHourList(RealmList<CallAcademicHour> v) {
        try {
            if (callAcademicHourList.size() < ConstantEntity.ONE) {
                throw new Exception("Exception! setCallAcademicHourList()");
            }
            this.callAcademicHourList = callAcademicHourList;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return "CallClasses{" +
                "id=" + id +
                ", callAcademicHourList=" + callAcademicHourList +
                '}';
    }
}
