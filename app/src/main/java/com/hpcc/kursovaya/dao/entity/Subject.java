package com.hpcc.kursovaya.dao.entity;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Subject extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название дисциплины
    private RealmList<Speciality> specialityList;// к специальности
    private int countHours;// Нагрузка - количество часов на дисциплину
    private RealmList<Course> courseList;// Номер курса
    private int color;// Цвет дисциплины

    public Subject() {
        id = 0;
        name = "";
        specialityList = new RealmList<>();
        countHours = 0;
        courseList = new RealmList<>();
        color = 0;
    }
    public Subject(int id, @NotNull String name, @NotNull RealmList<Speciality> specialityList, int countHours, @NotNull RealmList<Course> courseList, int color) {
        this();
        setId(id);
        setName(name);
        setSpecialityList(specialityList);
        setCountHours(countHours);
        setCourseList(courseList);
        setColor(color);
    }

    private void setId(int id){
        try{
            if (id < ConstantEntity.ONE){
                throw new Exception("Exception! setId()");
            }
            this.id = id;
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    public int getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }
    public Subject setName(@NotNull String name) {
        // TODO setName - сделать проверку
        this.name = name;
        return this;
    }

    @NotNull
    public RealmList<Speciality> getSpecialityList() {
        return specialityList;
    }
    public Subject setSpecialityList(@NotNull RealmList<Speciality> specialityList) {
        // TODO setSpecialtyList - сделать проверку
        this.specialityList = specialityList;
        return this;
    }

    public int getCountHours() {
        return countHours;
    }
    public Subject setCountHours(int countHours) {
        // TODO setCountHours - сделать проверку
        this.countHours = countHours;
        return this;
    }

    @NotNull
    public RealmList<Course> getCourseList() {
        return courseList;
    }
    public Subject setCourseList(@NotNull RealmList<Course> courseList) {
        try {
            for (Course course:courseList){
                if (course.getNumber() < ConstantEntity.MIN_COUNT_COURSE ||
                        course.getNumber() > ConstantEntity.MAX_COUNT_COURSE){
                    throw new Exception("Exception! setCourseList()");
                }
            }
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
        // TODO setColor - проверку
        this.color = color;
        return this;
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        Subject subject = (Subject)obj;
        return this.id == subject.id && this.name.equals(subject.name);
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", specialtyList=" + specialityList.toString() +
                ", countHours=" + countHours +
                ", course=" + courseList.toString() +
                ", color=" + color +
                '}';
    }
}
