package com.hpcc.kursovaya.dao.schedule.classes.template;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hpcc.kursovaya.dao.ConstantEntity;

import java.time.DayOfWeek;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TemplateScheduleDay extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private int dayOfWeek;// День недели
    private RealmList<TemplateClasses> templateClassesList;// Список пар на день

    {
        id = 0;
        dayOfWeek = 0;
        templateClassesList = new RealmList<>();
    }
    public TemplateScheduleDay() {

    }
    public TemplateScheduleDay(int dayOfWeek, RealmList<TemplateClasses> templateClassesList) {
        setDayOfWeek(dayOfWeek);
        setTemplateClassesList(templateClassesList);
    }
    public TemplateScheduleDay(DayOfWeek dayOfWeek, RealmList<TemplateClasses> templateClassesList) {
        setDayOfWeek(dayOfWeek);
        setTemplateClassesList(templateClassesList);
    }

    public int getId() {
        return id;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }
    public TemplateScheduleDay setDayOfWeek(int dayOfWeek) {
        try {
            if (dayOfWeek < DayOfWeek.MONDAY.ordinal() || dayOfWeek > DayOfWeek.SUNDAY.ordinal()) {
                throw new Exception("Exception! setDay()");
            }
            this.dayOfWeek = dayOfWeek;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }
    public TemplateScheduleDay setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek.ordinal();
        return this;
    }

    public RealmList<TemplateClasses> getTemplateClassesList() {
        return templateClassesList;
    }
    public TemplateScheduleDay setTemplateClassesList(RealmList<TemplateClasses> templateClassesList) {
        try {
            if (templateClassesList.size() < ConstantEntity.ONE) {
                throw new Exception("Exception! setTemplateClassesList()");
            }
            this.templateClassesList = templateClassesList;
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
                ", templateClassesList=" + templateClassesList +
                '}';
    }
}
