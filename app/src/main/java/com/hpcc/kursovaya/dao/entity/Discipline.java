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
    private RealmList<Course> courseList;// В каких семестрах проводится
    private int color;// Цвет дисциплины

    {
        id = 0;
        name = "";
        countHours = 0;
        specialty = new Specialty();
        courseList = new RealmList<>();
        color = 0;
    }
    public Discipline() {

    }
    public Discipline(String name, int countHours, Specialty specialty, RealmList<Course>  courseList, int color) {
        setName(name);
        setCountHours(countHours);
        setSpecialty(specialty);
        setCourseList(courseList);
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

    public RealmList<Course> getCourseList() {
        return courseList;
    }
    public Discipline setCourseList(RealmList<Course> courses) {
        try {
            if (courses.size() < 1) {
                throw new Exception("Exception! setSemesterList()");
            }
            this.courseList = courses;
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
                ", semesterList=" + courseList +
                ", color=" + color +
                '}';
    }
}
