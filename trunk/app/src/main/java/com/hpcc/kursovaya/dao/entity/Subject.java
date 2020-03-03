package com.hpcc.kursovaya.dao.entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Subject extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название дисциплины
    private int countHours;// Нагрузка - количество часов на дисциплину
    private RealmList<Specialty> specialtyList;// к специальности
    private RealmList<Semester> semesterList;// В каких семестрах проводится
    private int color;// Цвет дисциплины

    {
        id = 0;
        name = "";
        countHours = 0;
        specialtyList = new RealmList<>();
        semesterList = new RealmList<>();
        color = 0;
    }
    public Subject() {

    }
    public Subject(String name, int countHours, RealmList<Specialty> specialtyList, RealmList<Semester> semesterList, int color) {
        setName(name);
        setCountHours(countHours);
        setSpecialtyList(specialtyList);
        setSemesterList(semesterList);
        setColor(color);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public Subject setName(String name) {
        this.name = name;
        return this;
    }

    public RealmList<Specialty> getSpecialtyList() {
        return specialtyList;
    }
    public Subject setSpecialtyList(RealmList<Specialty> specialtyList) {
        this.specialtyList = specialtyList;
        return this;
    }

    public int getCountHours() {
        return countHours;
    }
    public Subject setCountHours(int countHours) {
        this.countHours = countHours;
        return this;
    }

    public RealmList<Semester> getSemesterList() {
        return semesterList;
    }
    public Subject setSemesterList(RealmList<Semester> semesters) {
        try {
            if (semesters.size() < 1) {
                throw new Exception("Exception! setSemesterList()");
            }
            this.semesterList = semesters;
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    public int getColor() {
        return color;
    }
    public Subject setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public String toString() {
        return "Discipline{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countHours=" + countHours +
                ", specialty=" + specialtyList +
                ", semesterList=" + semesterList +
                ", color=" + color +
                '}';
    }
}
