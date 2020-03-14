package com.hpcc.kursovaya.dao.entity;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;
import com.hpcc.kursovaya.dao.my_type.PairSpecialityCountHours;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Subject extends RealmObject {
    private static int countObj;

    static {
        countObj = 0;
    }

    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название дисциплины
    private RealmList<PairSpecialityCountHours> pairSpecialityCountHoursList;
    private int course;// Номер курса
    private int color;// Цвет дисциплины

    public Subject() {
        id = 0;
        name = "";
        pairSpecialityCountHoursList = new RealmList<>();
        course = 0;
        color = 0;
    }
    public Subject(@NotNull String name, @NotNull RealmList<PairSpecialityCountHours> pairSpecialityCountHoursList, int course, int color) {
        this();
        int maxID = DBManager.findMaxID(this.getClass());

        setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);
        setName(name);
        setPairSpecialityCountHoursList(pairSpecialityCountHoursList);
        setCourse(course);
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
    public RealmList<PairSpecialityCountHours> getPairSpecialityCountHoursList() {
        return pairSpecialityCountHoursList;
    }
    public Subject setPairSpecialityCountHoursList(@NotNull RealmList<PairSpecialityCountHours> pairSpecialityCountHoursList) {
        // TODO setPairSpecialityCountHoursList
        this.pairSpecialityCountHoursList = pairSpecialityCountHoursList;
        return this;
    }

    public int getCourse() {
        return course;
    }
    public Subject setCourse(int course) {
        // TODO setCourse
        this.course = course;
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

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public int pairListSize() {
        return pairSpecialityCountHoursList.size();
    }
    public boolean isPairListEmpty() {
        return pairSpecialityCountHoursList.isEmpty();
    }

    public boolean containsPairListKey(Speciality key) {
        for(PairSpecialityCountHours pairSpecialityCountHours : pairSpecialityCountHoursList){
            if(pairSpecialityCountHours.getSpeciality().equals(key)){
                return true;
            }
        }

        return false;
    }
    public boolean containsPairListValue(int value) {
        for(PairSpecialityCountHours pairSpecialityCountHours : pairSpecialityCountHoursList){
            if(pairSpecialityCountHours.getCountHours() == value){
                return true;
            }
        }

        return false;
    }

    public int pairListGet(Speciality key) {
        for(PairSpecialityCountHours pairSpecialityCountHours : pairSpecialityCountHoursList){
            if(pairSpecialityCountHours.getSpeciality().equals(key)){
                return pairSpecialityCountHours.getCountHours();
            }
        }
        return 0;
    }

    public int pairListPut(PairSpecialityCountHours pairSpecialityCountHours) {
        if (!containsPairListKey(pairSpecialityCountHours.getSpeciality())){
            pairSpecialityCountHoursList.add(pairSpecialityCountHours);
        }
        else {
            int index = pairSpecialityCountHoursList.indexOf(pairSpecialityCountHours);
            pairSpecialityCountHoursList.get(index).set(pairSpecialityCountHours.getSpeciality(), pairSpecialityCountHours.getCountHours());
        }
        return pairListGet(pairSpecialityCountHours.getSpeciality());
    }

    public int pairListRemove(Speciality key) {
        for (PairSpecialityCountHours pairSpecialityCountHours : pairSpecialityCountHoursList){
            if (key.equals(pairSpecialityCountHours.getSpeciality())){
                pairSpecialityCountHoursList.remove(pairSpecialityCountHours);
                return pairSpecialityCountHours.getCountHours();
            }
        }
        return 0;
    }

    public void pairListClear() {
        pairSpecialityCountHoursList.clear();
    }
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
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
                ", pairSpecialityCountHoursList=" + pairSpecialityCountHoursList +
                ", course=" + course +
                ", color=" + color +
                '}';
    }
}
