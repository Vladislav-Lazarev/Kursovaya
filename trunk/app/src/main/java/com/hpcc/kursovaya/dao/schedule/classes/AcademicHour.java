package com.hpcc.kursovaya.dao.schedule.classes;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.schedule.classes.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.schedule.date_time.DateTime;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AcademicHour extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private TemplateAcademicHour templateAcademicHour;// Шаблон полупары
    private DateTime dateTime;// Дата проведения
    private String note;// Заметка

    {
        id = 0;
        templateAcademicHour = new TemplateAcademicHour();
        dateTime = new DateTime();
        note = "";
    }
    public AcademicHour() {

    }
    public AcademicHour(TemplateAcademicHour templateAcademicHour, DateTime dateTime, String note) {
        setTemplateAcademicHour(templateAcademicHour);
        setDateTime(dateTime);
        setNote(note);
    }

    public TemplateAcademicHour getTemplateAcademicHour() {
        return templateAcademicHour;
    }
    public AcademicHour setTemplateAcademicHour(TemplateAcademicHour templateAcademicHour) {
        this.templateAcademicHour = templateAcademicHour;
        return this;
    }

    public DateTime getDate() {
        return dateTime;
    }
    public AcademicHour setDateTime(DateTime dateTime) {
        try {
            if (dateTime.compareTo(new DateTime()) < ConstantEntity.ONE) {
                throw new Exception("Exception! setDateTime()");
            }
            this.dateTime = dateTime;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    public String getNote() {
        return note;
    }
    public AcademicHour setNote(String note) {
        this.note = note;
        return this;
    }

    @Override
    public String toString() {
        return "AcademicHour{" +
                "id=" + id +
                ", templateAcademicHour=" + templateAcademicHour +
                ", dateTime=" + dateTime +
                ", note='" + note + '\'' +
                '}';
    }
}
