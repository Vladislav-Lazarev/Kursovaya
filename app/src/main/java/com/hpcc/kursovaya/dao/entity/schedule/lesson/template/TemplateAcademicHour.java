package com.hpcc.kursovaya.dao.entity.schedule.lesson.template;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TemplateAcademicHour extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private Subject subject;// Дисциплина на полупаре
    private Group group;// Группа на полупаре
    private int numberAcademicTwoHour;// Номер полупары(1 или 2 полупара)

    public TemplateAcademicHour() {
        id = 0;
        group = new Group();
        subject = new Subject();
        numberAcademicTwoHour = 0;
    }
    public TemplateAcademicHour(int id, @NotNull Subject subject, @NotNull Group group, int numberAcademicTwoHour) {
        this();
        setId(id);
        setSubject(subject);
        setGroup(group);
        setNumberAcademicTwoHour(numberAcademicTwoHour);
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
    public Subject getSubject() {
        return subject;
    }
    public TemplateAcademicHour setSubject(@NotNull Subject subject) {
        // TODO setSubject - проверка
        this.subject = subject;
        return this;
    }

    @NotNull
    public Group getGroup() {
        return group;
    }
    public TemplateAcademicHour setGroup(@NotNull Group group) {
        // TODO setGroup - проверка
        this.group = group;
        return this;
    }

    public int getNumberAcademicTwoHour() {
        return numberAcademicTwoHour;
    }
    public TemplateAcademicHour setNumberAcademicTwoHour(int numberAcademicTwoHour) {
        try {
            if (numberAcademicTwoHour < ConstantEntity.MIN_COUNT_ACADEMIC_HOUR ||
                    numberAcademicTwoHour > ConstantEntity.MAX_COUNT_ACADEMIC_HOUR) {
                throw new Exception("Exception! setNumberAcademicTwoHour()");
            }
            this.numberAcademicTwoHour = numberAcademicTwoHour;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return "TemplateAcademicHour{" +
                "id=" + id +
                ", discipline=" + subject +
                ", group=" + group +
                ", numberAcademicTwoHour=" + numberAcademicTwoHour +
                '}';
    }
}