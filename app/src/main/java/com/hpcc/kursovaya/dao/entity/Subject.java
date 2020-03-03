package com.hpcc.kursovaya.dao.entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Subject extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название дисциплины
    private int countHours;// Нагрузка - количество часов на дисциплину
    private RealmList<Specialty> specialtyList;// к специальности
    private Course course;// Гомер курса
    private RealmList<Group> groupList; // Список дисциплин
    private int color;// Цвет дисциплины

    {
        id = 0;
        name = "";
        countHours = 0;
        specialtyList = new RealmList<>();
        course = new Course();
        color = 0;
    }
    public Subject() {

    }
    public Subject(int id, String name, int countHours, RealmList<Specialty> specialtyList, Course course, int color) {
        setId(id);
        setName(name);
        setCountHours(countHours);
        setSpecialtyList(specialtyList);
        setCourse(course);
        setColor(color);
    }

    private void setId(int id){this.id = id;}
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

    public Course getCourse() {
        return course;
    }
    public Subject setCourse(Course course) {
        try {
            // TODO setCourse
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
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countHours=" + countHours +
                ", specialtyList=" + specialtyList +
                ", course=" + course +
                ", groupList=" + groupList +
                ", color=" + color +
                '}';
    }
}
