package com.hpcc.kursovaya.dao.my_type;

import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PairSpecialityCountHours extends RealmObject {
    @PrimaryKey
    private int id;
    private Speciality speciality;
    private int countHours;

    public PairSpecialityCountHours() {
        id = 0;
        speciality = new Speciality();
        countHours = 0;
    }
    public PairSpecialityCountHours(int id, @NotNull Speciality speciality, int countHours) {
        this();
        setId(id);
        set(speciality, countHours);
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
    public Speciality getSpeciality() {
        return speciality;
    }
    private PairSpecialityCountHours setSpeciality(@NotNull Speciality speciality) {
        // TODO setSpeciality
        this.speciality = speciality;
        return this;
    }

    @NotNull
    public int getCountHours() {
        return countHours;
    }
    private PairSpecialityCountHours setCountHours(int countHours) {
        // TODO setCountHours - проверка
        this.countHours = countHours;
        return this;
    }

    public PairSpecialityCountHours set(@NotNull Speciality speciality, int countHours){
        setSpeciality(speciality);
        setCountHours(countHours);
        return this;
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        PairSpecialityCountHours pairSpecialityCountHours = (PairSpecialityCountHours)obj;
        return this.id == pairSpecialityCountHours.id && this.speciality.equals(pairSpecialityCountHours.speciality);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "id=" + id +
                ", speciality=" + speciality +
                ", countHours=" + countHours +
                '}';
    }
}
