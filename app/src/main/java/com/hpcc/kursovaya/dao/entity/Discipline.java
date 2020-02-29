package com.hpcc.kursovaya.dao.entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Discipline extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название дисциплины
    private int countHours;// Нагрузка - количество часов на дисциплину
    private Specialty specialty;// Отношение к специальности
    private RealmList<Semester> semesterList;// В каких семестрах проводится
    private int color;// Цвет дисциплины

    {
        id = 0;
        name = "";
        countHours = 0;
        specialty = new Specialty();
        semesterList = new RealmList<>();
        color = 0;
    }
    public Discipline() {

    }
    public Discipline(String name, int countHours, Specialty specialty, RealmList<Semester> semesterList, int color) {
        setName(name);
        setCountHours(countHours);
        setSpecialty(specialty);
        setSemesterList(semesterList);
        setColor(color);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public Discipline setName(String name) {
        this.name = name;
        return this;
    }

    public Specialty getSpecialty() {
        return specialty;
    }
    public Discipline setSpecialty(Specialty specialty) {
        this.specialty = specialty;
        return this;
    }

    public int getCountHours() {
        return countHours;
    }
    public Discipline setCountHours(int countHours) {
        this.countHours = countHours;
        return this;
    }

    public RealmList<Semester> getSemesterList() {
        return semesterList;
    }
    public Discipline setSemesterList(RealmList<Semester> semesters) {
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
    public Discipline setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public String toString() {
        return "Discipline{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countHours=" + countHours +
                ", specialty=" + specialty +
                ", semesterList=" + semesterList +
                ", color=" + color +
                '}';
    }
}
