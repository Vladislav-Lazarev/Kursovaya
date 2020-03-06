package com.hpcc.kursovaya.dao.entity.schedule.lesson;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.my_type.date_time.maks.RealmDateTime;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AcademicHour extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private TemplateAcademicHour templateAcademicHour;// Шаблон полупары
    private RealmDateTime dateTime;// Дата проведения
    private String note;// Заметка
    private boolean isCompleted;// Проведенная или не проведенная полупара
    private boolean isCanceled;// Отмененная или не проведенная полупара

    {
        id = 0;
        templateAcademicHour = new TemplateAcademicHour();
        dateTime = new RealmDateTime();
        note = "";
        isCompleted = false;
        isCanceled = false;
    }
    public AcademicHour() {

    }
    public AcademicHour(int id, @NotNull TemplateAcademicHour templateAcademicHour, @NotNull RealmDateTime dateTime, @NotNull String note, boolean isCompleted, boolean isCanceled) {
        setId(id);
        setTemplateAcademicHour(templateAcademicHour);
        setDateTime(dateTime);
        setNote(note);
        setCompleted(isCompleted);
        setCanceled(isCanceled);
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
    public TemplateAcademicHour getTemplateAcademicHour() {
        return templateAcademicHour;
    }
    public AcademicHour setTemplateAcademicHour(@NotNull TemplateAcademicHour templateAcademicHour) {
        // TODO setTemplateAcademicHour - проверка
        this.templateAcademicHour = templateAcademicHour;
        return this;
    }

    @NotNull
    public RealmDateTime getDateTime() {
        return dateTime;
    }
    public AcademicHour setDateTime(@NotNull RealmDateTime dateTime) {
        // TODO setDateTime - проверка
        this.dateTime = dateTime;
        return this;
    }

    @NotNull
    public String getNote() {
        return note;
    }
    public AcademicHour setNote(@NotNull String note) {
        // TODO setNote - проверка
        this.note = note;
        return this;
    }

    public boolean hasCompleted() {
        return isCompleted;
    }
    public AcademicHour setCompleted(boolean completed) {
        // TODO setNote - проверка
        isCompleted = completed;
        return this;
    }

    public boolean hasCanceled() {
        return isCanceled;
    }
    public AcademicHour setCanceled(boolean canceled) {
        // TODO setNote - проверка
        isCanceled = canceled;
        return this;
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
