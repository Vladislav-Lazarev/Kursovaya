package com.hpcc.kursovaya.dao.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Group extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название(имя) группы
    private Semester semester;// Номер семестра группы
    private Specialty specialty;// Принадленость группы к специальности

    {
        id = 0;
        name = "";
        semester = new Semester();
        specialty = new Specialty();
    }
    public Group() {

    }
    public Group(String name, Semester semester, Specialty specialty) {
        setName(name);
        setSemester(semester);
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

    public Semester getSemester() {
        return semester;
    }
    public Group setSemester(Semester semester) {
        this.semester = semester;
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
                ", semester=" + semester +
                ", specialty=" + specialty +
                '}';
    }
}
