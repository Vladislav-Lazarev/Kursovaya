package com.hpcc.kursovaya.dao.entity;

import com.hpcc.kursovaya.dao.ConstantEntity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Specialty extends RealmObject{
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название(имя) специальности
    private int countSemester;// Количество проведения семестров в конкретной специальности

    {
        id = 0;
        name = "";
        countSemester = 0;
    }
    public Specialty() {

    }
    public Specialty(String name, int countSemester) {
        setName(name);
        setCountSemester(countSemester);
    }

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

    public int getCountSemester() {
        return countSemester;
    }
    public Specialty setCountSemester(int countSemester) {
        try {
            if (countSemester > ConstantEntity.MIN_COUNT_SEMESTER && countSemester < ConstantEntity.MAX_COUNT_SEMESTER) {
                throw new Exception("Exception! setCountSemester()");
            }
            this.countSemester = countSemester;
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
                ", countSemester=" + countSemester +
                '}';
    }
}
