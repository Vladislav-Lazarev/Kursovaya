package com.hpcc.kursovaya.dao.entity.schedule.lesson.template;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TemplateScheduleWeek extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private RealmList<TemplateScheduleDay> templateScheduleDayList;// Список дней, неделя, 7 дней

    public TemplateScheduleWeek() {
        id = 0;
        templateScheduleDayList = new RealmList<>();
    }
    public TemplateScheduleWeek(int id, @NotNull RealmList<TemplateScheduleDay> templateScheduleDayList) {
        this();
        setId(id);
        setTemplateScheduleDayList(templateScheduleDayList);
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
    public RealmList<TemplateScheduleDay> getTemplateScheduleDayList() {
        return templateScheduleDayList;
    }
    public TemplateScheduleWeek setTemplateScheduleDayList(@NotNull RealmList<TemplateScheduleDay> templateScheduleDayList) {
        try {
            if (templateScheduleDayList.size() < ConstantEntity.MIN_COUNT_WEEK ||
                    templateScheduleDayList.size() > ConstantEntity.MAX_COUNT_WEEK) {
                throw new Exception("Exception! setTemplateScheduleDayList()");
            }
            this.templateScheduleDayList = templateScheduleDayList;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return "TemplateScheduleWeek{" +
                "id=" + id +
                ", templateScheduleDayList=" + templateScheduleDayList.toString() +
                '}';
    }
}
