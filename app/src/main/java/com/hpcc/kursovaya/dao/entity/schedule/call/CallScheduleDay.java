package com.hpcc.kursovaya.dao.entity.schedule.call;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CallScheduleDay extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private int dayOfWeek;// Номер дня недели
    private RealmList<CallLesson> callLessonList;// Расписание звонков на день

    public CallScheduleDay() {
        id = 0;
        dayOfWeek = -1;
        callLessonList = new RealmList<>();
    }
    public CallScheduleDay(int id, int dayOfWeek, @NotNull RealmList<CallLesson> callLessonList) {
        this();
        setId(id);
        setDayOfWeek(dayOfWeek);
        setCallLessonList(callLessonList);
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

    public int getDayOfWeek() {
        return dayOfWeek;
    }
    public CallScheduleDay setDayOfWeek(int dayOfWeek) {
        try {
            if (dayOfWeek < ConstantEntity.MIN_DAY_OF_WEEK || dayOfWeek > ConstantEntity.MAX_DAY_OF_WEEK) {
                throw new Exception("Exception! setDayOfWeek()");
            }
            this.dayOfWeek = dayOfWeek;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    @NotNull
    public RealmList<CallLesson> getCallLessonList() {
        return callLessonList;
    }
    public CallScheduleDay setCallLessonList(@NotNull RealmList<CallLesson> callLessonList) {
        // TODO setCallLessonList - проверка
        try {
            if (callLessonList.size() < ConstantEntity.MIN_COUNT_LESSON ||
                    callLessonList.size() > ConstantEntity.MAX_COUNT_LESSON) {
                throw new Exception("Exception! setCallLessonList()");
            }
            this.callLessonList = callLessonList;
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
                ", callLessonList=" + callLessonList.toString() +
                '}';
    }
}
