package com.hpcc.kursovaya.dao.entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Group extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название(имя) группы
    private Course course;// Номер курса группы
    private Specialty specialty;// Принадленость группы к специальности
    private RealmList<Subject> subjectList;// Список дисциплин

    {
        id = 0;
        name = "";
        course = new Course();
        specialty = new Specialty();
        subjectList = new RealmList<>();
    }
    public Group() {

    }
    public Group(String name, Course course, Specialty specialty, RealmList<Subject> subjectList) {
        setName(name);
        setCourse(course);
        setSpecialty(specialty);
        setSubjectList(subjectList);
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

    public Course getCourse() {
        return course;
    }
    public Group setCourse(Course course) {
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

    public RealmList<Subject> getSubjectList() {
        return subjectList;
    }
    public Group setSubjectList(RealmList<Subject> subjectList) {
        // TODO setSubjectList проверку
        this.subjectList = subjectList;
        return this;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", course=" + course +
                ", specialty=" + specialty +
                ", subjectList=" + subjectList +
                '}';
    }
}
