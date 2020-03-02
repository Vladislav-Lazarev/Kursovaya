package com.hpcc.kursovaya.dao.schedule.classes.template;

import com.hpcc.kursovaya.dao.ConstantEntity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TemplateClasses extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private RealmList<TemplateAcademicHour> templateAcademicHourList;// Шаблое занятия, пары

    {
        id = 0;
        templateAcademicHourList = new RealmList<>();
    }
    public TemplateClasses() {

    }
    public TemplateClasses(RealmList<TemplateAcademicHour> templateAcademicHourList) {
        setTemplateAcademicHourList(templateAcademicHourList);
    }

    public int getId() {
        return id;
    }

    public RealmList<TemplateAcademicHour> getTemplateAcademicHourList() {
        return templateAcademicHourList;
    }
    public TemplateClasses setTemplateAcademicHourList(RealmList<TemplateAcademicHour> templateAcademicHourList) {
        try {
            if (templateAcademicHourList.size() < ConstantEntity.MIN_COUNT_ACADEMIC_HOUR ||
                    templateAcademicHourList.size() > ConstantEntity.MAX_COUNT_ACADEMIC_HOUR) {
                throw new Exception("Exception! setTemplateAcademicHourList()");
            }
            this.templateAcademicHourList = templateAcademicHourList;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return "TemplateClasses{" +
                "id=" + id +
                ", templateAcademicHourList=" + templateAcademicHourList +
                '}';
    }
}
