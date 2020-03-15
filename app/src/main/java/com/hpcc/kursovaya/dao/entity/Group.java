package com.hpcc.kursovaya.dao.entity;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Group extends RealmObject implements Serializable {
    private static int countObj;

    static {
        countObj = 0;
    }

    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название(имя) группы
    private Speciality speciality;// Принадленость группы к специальности
    private int course;// Номер курса группы

    public Group() {
        id = 0;
        name = "";
        speciality = new Speciality();
        course = 0;
    }
    public Group(@NotNull String name, @NotNull Speciality speciality, int course) {
        this();
        int maxID = DBManager.findMaxID(this.getClass());

        setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);
        setName(name);
        setSpecialty(speciality);
        setCourse(course);
    }

    private void setId(int id) {
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

    public String getName() {
        return name;
    }
    public Group setName(String name) {
        // TODO setName - сделать проверку
        this.name = name;
        return this;
    }

    public Speciality getSpecialty() {
        return speciality;
    }
    public Group setSpecialty(Speciality speciality) {
        // TODO setName - сделать проверку
        this.speciality = speciality;
        return this;
    }

    public int getCourse() {
        return course;
    }
    public Group setCourse(int course) {
        // TODO setName - сделать проверку
        this.course = course;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        Group group = (Group)obj;
        return this.name.equals(group.name);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", specialty=" + speciality +
                ", course=" + course +
                '}';
    }
}
