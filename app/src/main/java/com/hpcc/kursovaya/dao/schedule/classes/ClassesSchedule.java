package com.hpcc.kursovaya.dao.schedule.classes;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.schedule.date_time.DateTime;
import com.hpcc.kursovaya.dao.schedule.date_time.PatternCalendar;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ClassesSchedule extends RealmObject {
    // TODO ClassesSchedule

    @PrimaryKey
    private int id;
    private RealmList<AcademicHour> academicHourList;

    {
        id = 0;
        academicHourList = new RealmList<>();
    }
    public ClassesSchedule() {

    }
    public ClassesSchedule(RealmList<AcademicHour> academicHourList) {
        this.academicHourList = academicHourList;
    }

    public int getId() {
        return id;
    }

    public RealmList<AcademicHour> getAcademicHourList() {
        return academicHourList;
    }
    public void setAcademicHourList(RealmList<AcademicHour> academicHourList) {
        // TODO setAcademicHourList()
        this.academicHourList = academicHourList;
    }

    public int countHours(DateTime start, DateTime end, Subject subject){
        DateTime startOther = start;
        DateTime endOther = end;
        int result = 0;

        for (int i = 0; startOther.compareTo(endOther) == 0; i++) {
            if (subject.equals(academicHourList.get(i).getTemplateAcademicHour().getSubject())) {
                ++result;
            }
            startOther.add(PatternCalendar.DAY_OF_MONTH, ConstantEntity.ONE);
        }
        return result;
    }

    @Override
    public String toString() {
        return "ClassesSchedule{" +
                "id=" + id +
                ", academicHourList=" + academicHourList +
                '}';
    }
}
