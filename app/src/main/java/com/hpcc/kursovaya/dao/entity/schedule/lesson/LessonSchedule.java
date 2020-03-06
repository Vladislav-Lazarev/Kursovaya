package com.hpcc.kursovaya.dao.entity.schedule.lesson;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.my_type.date_time.maks.RealmDateTime;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LessonSchedule extends RealmObject {
    @PrimaryKey
    private int id;
    private RealmList<AcademicHour> academicHourList;

    {
        id = 0;
        academicHourList = new RealmList<>();
    }
    public LessonSchedule() {

    }
    public LessonSchedule(int id, @NotNull RealmList<AcademicHour> academicHourList) {
        setId(id);
        setAcademicHourList(academicHourList);
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
    public RealmList<AcademicHour> getAcademicHourList() {
        return academicHourList;
    }
    public LessonSchedule setAcademicHourList(@NotNull RealmList<AcademicHour> academicHourList) {
        // TODO setAcademicHourList() - проверка
        this.academicHourList = academicHourList;
        return this;
    }

    public int countHours(RealmDateTime start, RealmDateTime end, Subject subject){
        // TODO countHours - подсчет проведенных пар
        RealmDateTime startOther = start;
        RealmDateTime endOther = end;

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
