package com.hpcc.kursovaya.dao.schedule.classes.template;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TemplateAcademicHour extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private Group group;// Группа на полупаре
    private Subject subject;// Дисциплина на полупаре
    private int numberAcademicTwoHour;// Номер полупары(1 или 2 полупара)

    {
        id = 0;
        group = new Group();
        subject = new Subject();
        numberAcademicTwoHour = 0;
    }
    public TemplateAcademicHour() {

    }
    public TemplateAcademicHour(Group group, Subject subject, int numberAcademicTwoHour) {
        setGroup(group);
        setSubject(subject);
        setNumberAcademicTwoHour(numberAcademicTwoHour);
    }

    public int getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }
    public TemplateAcademicHour setGroup(Group group) {
        this.group = group;
        return this;
    }

    public Subject getSubject() {
        return subject;
    }
    public TemplateAcademicHour setSubject(Subject subject) {
        this.subject = subject;
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
                ", group=" + group +
                ", discipline=" + subject +
                ", numberAcademicTwoHour=" + numberAcademicTwoHour +
                '}';
    }
}