package com.hpcc.kursovaya.dao.entity;

import com.hpcc.kursovaya.dao.ConstantEntity;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Specialty extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название(имя) специальности
    private int countCourse;// Количество проведения семестров в конкретной специальности

    {
        id = 0;
        name = "";
        countCourse = 0;
    }
    public Specialty() {

    }

    public Specialty(int id, String name, int countCourse) {
        setId(id);
        setName(name);
        setCountCourse(countCourse);
    }

    private void setId(int id){this.id = id;}
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public Specialty setName(String name) {
        this.name = name;
        return this;
    }

    public int getCountCourseCourse() {
        return countCourse;
    }
    public Specialty setCountCourse(int countCourse) {
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
    public String toString() {
        return "Specialty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countCourse=" + countCourse +
                '}';
    }
}
