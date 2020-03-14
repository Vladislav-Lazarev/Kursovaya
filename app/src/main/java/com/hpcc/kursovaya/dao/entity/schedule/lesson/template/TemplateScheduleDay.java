package com.hpcc.kursovaya.dao.entity.schedule.lesson.template;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TemplateScheduleDay extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private int dayOfWeek;// День недели
    private RealmList<TemplateLesson> templateLessonList;// Список пар на день

    public TemplateScheduleDay() {
        id = 0;
        dayOfWeek = 0;
        templateLessonList = new RealmList<>();
    }
    public TemplateScheduleDay(int id, int dayOfWeek, @NotNull RealmList<TemplateLesson> templateLessonList) {
        this();
        setId(id);
        setDayOfWeek(dayOfWeek);
        setTemplateLessonList(templateLessonList);
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
    public TemplateScheduleDay setDayOfWeek(int dayOfWeek) {
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
    public RealmList<TemplateLesson> getTemplateLessonList() {
        return templateLessonList;
    }
    public TemplateScheduleDay setTemplateLessonList(@NotNull RealmList<TemplateLesson> templateLessonList) {
        try {
            if (templateLessonList.size() < ConstantEntity.MIN_COUNT_LESSON ||
                    templateLessonList.size() > ConstantEntity.MAX_COUNT_LESSON) {
                throw new Exception("Exception! setTemplateClassesList()");
            }
            this.templateLessonList = templateLessonList;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return "TemplateScheduleDay{" +
                "id=" + id +
                ", dayOfWeek=" + dayOfWeek +
                ", templateClassesList=" + templateLessonList.toString() +
                '}';
    }
}
