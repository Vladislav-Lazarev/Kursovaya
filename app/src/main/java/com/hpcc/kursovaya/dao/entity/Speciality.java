package com.hpcc.kursovaya.dao.entity;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Speciality extends RealmObject {
    private static int countObj;

    static {
        countObj = 0;
    }

    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название(имя) специальности
    private int countCourse;// Количество проведения курсов в конкретной специальности

    public Speciality() {
        id = 0;
        name = "";
        countCourse = 0;
    }
    public Speciality(@NotNull String name, int countCourse) {
        this();
        int maxID = DBManager.findMaxID(this.getClass());

        setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);
        setName(name);
        setCountCourse(countCourse);
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
    public Speciality setName(String name) {
        // TODO setName - проверка
        this.name = name;
        return this;
    }

    public int getCountCourse() {
        return countCourse;
    }
    public Speciality setCountCourse(int countCourse) {
        try {
            if (countCourse < ConstantEntity.MIN_COUNT_COURSE || countCourse > ConstantEntity.MAX_COUNT_COURSE) {
                throw new Exception("Exception! setCountSemester()");
            }
            this.countCourse = countCourse;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        Speciality speciality = (Speciality)obj;
        return this.name.equals(speciality.name);
    }
    @Override
    public String toString() {
        return "Specialty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countCourse=" + countCourse +
                '}';
    }
}
