package com.hpcc.kursovaya.dao.schedule.classes;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.my_type.date_time.vlad.DateTime;
import com.hpcc.kursovaya.dao.schedule.classes.template.TemplateAcademicHour;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AcademicHour extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private TemplateAcademicHour templateAcademicHour;// Шаблон полупары
    private DateTime dateTime;// Дата проведения
    private String note;// Заметка
    private boolean isCompleted;// Проведенная или не проведенная полупара
    private boolean isCanceled;// Отмененная или не проведенная полупара

    {
        id = 0;
        templateAcademicHour = new TemplateAcademicHour();
        dateTime = new DateTime();
        note = "";
        isCompleted = false;
        isCanceled = false;
    }
    public AcademicHour() {

    }
    public AcademicHour(TemplateAcademicHour templateAcademicHour, DateTime dateTime, String note, boolean isCompleted, boolean isCanceled) {
        setTemplateAcademicHour(templateAcademicHour);
        setDateTime(dateTime);
        setNote(note);
        setCompleted(isCompleted);
        setCanceled(isCanceled);
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

    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isCanceled() {
        return isCanceled;
    }
    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    @Override
    public String toString() {
        return "AcademicHour{" +
                "id=" + id +
                ", templateAcademicHour=" + templateAcademicHour +
                ", dateTime=" + dateTime +
                ", note='" + note + '\'' +
                ", isCompleted=" + isCompleted +
                ", isCanceled=" + isCanceled +
                '}';
    }
}
