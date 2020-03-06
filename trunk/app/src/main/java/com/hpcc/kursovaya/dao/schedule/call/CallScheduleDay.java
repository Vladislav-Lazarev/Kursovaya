package com.hpcc.kursovaya.dao.entity.schedule.call;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hpcc.kursovaya.dao.ConstantEntity;

import java.time.DayOfWeek;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CallScheduleDay extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private int dayOfWeek;// Номер дня недели
    private RealmList<CallClasses> callLessonList;// Расписание звонков на день

    {
        id = 0;
        dayOfWeek = DayOfWeek.MONDAY.getValue();
        callLessonList = new RealmList<>();
    }
    public CallScheduleDay() {

    }
    public CallScheduleDay(int dayOfWeek, RealmList<CallClasses> callAcademicTwoHourList) {
        setDay(dayOfWeek);
        setCallAcademicTwoHourList(callAcademicTwoHourList);
    }
    public CallScheduleDay(DayOfWeek dayOfWeek, RealmList<CallClasses> callAcademicTwoHourList) {
        setDay(dayOfWeek.ordinal());
        setCallAcademicTwoHourList(callAcademicTwoHourList);
    }

    public int getId() {
        return id;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }
    public CallScheduleDay setDay(int day) {
        try {
            if (day < DayOfWeek.MONDAY.ordinal() || day > DayOfWeek.SUNDAY.ordinal()) {
                throw new Exception("Exception! setDay()");
            }
            this.dayOfWeek = day;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }
    public CallScheduleDay setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek.ordinal();
        return this;
    }

    public RealmList<CallClasses> getCallAcademicTwoHourList() {
        return callLessonList;
    }
    public CallScheduleDay setCallAcademicTwoHourList(RealmList<CallClasses> callAcademicTwoHourList) {
        try {
            if (callAcademicTwoHourList.size() < ConstantEntity.MIN_COUNT_LESSON ||
                    callAcademicTwoHourList.size() > ConstantEntity.MAX_COUNT_LESSON) {
                throw new Exception("Exception! setCallAcademicTwoHourList()");
            }
            this.callLessonList = callAcademicTwoHourList;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return "CallScheduleDay{" +
                "id=" + id +
                ", day=" + dayOfWeek +
                ", callLessonList=" + callLessonList +
                '}';
    }
}
