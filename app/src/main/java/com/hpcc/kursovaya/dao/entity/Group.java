package com.hpcc.kursovaya.dao.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Group extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название(имя) группы
    private int course;// Номер курса группы
    private Specialty specialty;// Принадленость группы к специальности

    {
        id = 0;
        name = "";
        course = 0;
        specialty = new Specialty();
    }
    public Group() {

    }
    public Group(String name, int course, Specialty specialty) {
        setName(name);
        setCourse(course);
        setSpecialty(specialty);
    }

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

    public int getCourse() {
        return course;
    }
    public Group setCourse(int course) {
        this.course = course;
        return this;
    }

    public Specialty getSpecialty() {
        return specialty;
    }
    public Group setSpecialty(Specialty specialty) {
        this.specialty = specialty;
        return this;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", course=" + course +
                ", specialty=" + specialty +
                '}';
    }
}
