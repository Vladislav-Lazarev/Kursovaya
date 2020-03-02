package com.hpcc.kursovaya.dao.schedule.classes.template;

import com.hpcc.kursovaya.dao.ConstantEntity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TemplateScheduleWeek extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private RealmList<TemplateScheduleDay> templateScheduleDayList;// Список дней, неделя, 7 дней

    {
        id = 0;
        templateScheduleDayList = new RealmList<>();
    }
    public TemplateScheduleWeek() {

    }
    public TemplateScheduleWeek(RealmList<TemplateScheduleDay> templateScheduleDayList) {
        setTemplateScheduleDayList(templateScheduleDayList);
    }

    public int getId() {
        return id;
    }

    public RealmList<TemplateScheduleDay> getTemplateScheduleDayList() {
        return templateScheduleDayList;
    }
    public TemplateScheduleWeek setTemplateScheduleDayList(RealmList<TemplateScheduleDay> templateScheduleDayList) {
        try {
            if (templateScheduleDayList.size() > ConstantEntity.MAX_WEEK || templateScheduleDayList.size() < ConstantEntity.MIN_WEEK) {
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
                ", templateScheduleDayList=" + templateScheduleDayList +
                '}';
    }
}
