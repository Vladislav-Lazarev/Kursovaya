package com.hpcc.kursovaya.dao.entity.schedule.lesson;

import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LessonSchedule extends RealmObject {
    @PrimaryKey
    private int id;
    private RealmList<AcademicHour> academicHourList;

    public LessonSchedule() {
        id = 0;
        academicHourList = new RealmList<>();
    }
    public LessonSchedule(int id, @NotNull RealmList<AcademicHour> academicHourList) {
        this();
        setId(id);
        setAcademicHourList(academicHourList);
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
    public RealmList<AcademicHour> getAcademicHourList() {
        return academicHourList;
    }
    public LessonSchedule setAcademicHourList(@NotNull RealmList<AcademicHour> academicHourList) {
        // TODO setAcademicHourList() - проверка
        this.academicHourList = academicHourList;
        return this;
    }

    public int countHours(Date start, Date end, Subject subject){
        // TODO countHours - подсчет проведенных пар
        Date startOther = start;
        Date endOther = end;

        int result = 0;
        return result;
    }

    @Override
    public String toString() {
        return "ClassesSchedule{" +
                "id=" + id +
                ", academicHourList=" + academicHourList.toString() +
                '}';
    }
}
