package com.hpcc.kursovaya.dao.schedule.classes;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.schedule.classes.template.TemplateAcademicHour;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AcademicHour extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private TemplateAcademicHour templateAcademicHour;// Шаблон полупары
    private Date date;// Дата проведения
    private String note;// Заметка

    {
        id = 0;
        templateAcademicHour = new TemplateAcademicHour();
        date = new Date(0);
        note = "";
    }
    public AcademicHour() {

    }
    public AcademicHour(TemplateAcademicHour templateAcademicHour, Date date, String note) {
        setTemplateAcademicHour(templateAcademicHour);
        setDate(date);
        setNote(note);
    }

    public TemplateAcademicHour getTemplateAcademicHour() {
        return templateAcademicHour;
    }
    public AcademicHour setTemplateAcademicHour(TemplateAcademicHour templateAcademicHour) {
        this.templateAcademicHour = templateAcademicHour;
        return this;
    }

    public Date getDate() {
        return date;
    }
    public AcademicHour setDate(Date date) {
        try {
            if (date.compareTo(new Date(0)) < ConstantEntity.ONE) {
                throw new Exception("Exception! setDate()");
            }
            this.date = date;
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
                ", date=" + date +
                ", note='" + note + '\'' +
                '}';
    }
}
