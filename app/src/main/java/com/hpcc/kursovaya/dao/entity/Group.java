package com.hpcc.kursovaya.dao.entity;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Group extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название(имя) группы
    private Course course;// Номер курса группы
    private Specialty speciality;// Принадленость группы к специальности

    {
        id = 0;
        name = "";
        course = new Course();
        speciality = new Specialty();
    }
    public Group() {

    }
    public Group(int id, String name, Course course, Specialty speciality) {
        setId(id);
        setName(name);
        setCourse(course);
        setSpecialty(speciality);
    }

    private void setId(int id){this.id = id;}
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public Group setName(String name) {
        this.name = name;
        return this;
    }

    public Course getCourse() {
        return course;
    }
    public Group setCourse(Course course) {
        this.course = course;
        return this;
    }

    public Specialty getSpecialty() {
        return speciality;
    }
    public Group setSpecialty(Specialty specialty) {
        this.speciality = speciality;
        return this;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", course=" + course +
                ", specialty=" + speciality +
                '}';
    }
}
