package com.hpcc.kursovaya.dao.entity.schedule.lesson.template;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TemplateLesson extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private RealmList<TemplateAcademicHour> templateAcademicHourList;// Шаблое занятия, пары

    public TemplateLesson() {
        id = 0;
        templateAcademicHourList = new RealmList<>();
    }
    public TemplateLesson(int id, @NotNull RealmList<TemplateAcademicHour> templateAcademicHourList) {
        this();
        setId(id);
        setTemplateAcademicHourList(templateAcademicHourList);
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
    public RealmList<TemplateAcademicHour> getTemplateAcademicHourList() {
        return templateAcademicHourList;
    }
    public TemplateLesson setTemplateAcademicHourList(@NotNull RealmList<TemplateAcademicHour> templateAcademicHourList) {
        // TODO setTemplateAcademicHourList - проверка
        /*try {
            if () {
                throw new Exception("Exception! setTemplateAcademicHourList()");
            }
            this.templateAcademicHourList = templateAcademicHourList;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }*/
        return this;
    }

    @Override
    public String toString() {
        return "TemplateClasses{" +
                "id=" + id +
                ", templateAcademicHourList=" + templateAcademicHourList.toString() +
                '}';
    }
}
