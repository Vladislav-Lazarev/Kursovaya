package com.hpcc.kursovaya.dao.entity.schedule.lesson.template;

import android.util.Log;

import androidx.annotation.NonNull;

import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TemplateAcademicHour extends RealmObject{
    private static final String TAG = TemplateAcademicHour.class.getSimpleName();

    @PrimaryKey
    private int id;// ID template for half pair
    private int idSubject;// ID subject
    private int idGroup;// ID group
    private int numberHalfPair;// Number half pair (1 or 2)

    {
        id = 0;
        idSubject = 0;
        idGroup = 0;
        numberHalfPair = 0;
    }
    public TemplateAcademicHour() {

    }
    public TemplateAcademicHour(@NotNull Subject subject, @NotNull Group group, int numberHalfPair) {
        setSubject(subject);
        setGroup(group);
        setNumberHalfPair(numberHalfPair);
    }

    public int getId() {
        return id;
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

    @NotNull
    public Subject getSubject() {
        return DBManager.read(Subject.class, ConstantEntity.ID, idSubject);
    }
    public TemplateAcademicHour setSubject(@NotNull Subject subject) {
        if(subject.getId() < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setSubject(" + subject.toString() + ")");
            throw new RuntimeException("setSubject(" + subject.toString() + ")");
        }

        this.idSubject = subject.getId();
        return this;
    }

    @NotNull
    public Group getGroup() {
        return DBManager.read(Group.class, ConstantEntity.ID, idGroup);
    }
    public TemplateAcademicHour setGroup(@NotNull Group group) {
        if(group.getId() < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setGroup(" + group.toString() + ")");
            throw new RuntimeException("setGroup(" + group.toString() + ")");
        }

        this.idGroup = group.getId();
        return this;
    }

    public int getNumberHalfPair() {
        return numberHalfPair;
    }
    public TemplateAcademicHour setNumberHalfPair(int numberHalfPair) {
        if (numberHalfPair < ConstantEntity.MIN_COUNT_ACADEMIC_HOUR ||
                numberHalfPair > ConstantEntity.MAX_COUNT_ACADEMIC_HOUR) {
            throw new RuntimeException("Exception! setNumberAcademicTwoHour()");
        }
        this.numberHalfPair = numberHalfPair;
        return this;
    }

    @Override
    public String toString() {
        return "TemplateAcademicHour{" +
                "id=" + id +
                ", idSubject=" + idSubject +
                ", idGroup=" + idGroup +
                ", numberHalfPair=" + numberHalfPair +
                '}';
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // EntityI
    private static int countObj = 0;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Cloneable
    @NonNull
    @Override
    public TemplateAcademicHour clone() throws CloneNotSupportedException {
        return (TemplateAcademicHour) super.clone();
    }
}