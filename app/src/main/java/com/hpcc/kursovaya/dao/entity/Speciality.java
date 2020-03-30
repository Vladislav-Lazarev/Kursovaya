package com.hpcc.kursovaya.dao.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Speciality extends RealmObject implements EntityI<Speciality>, Parcelable, Cloneable {
    private static final String TAG = Speciality.class.getSimpleName();

    @PrimaryKey
    private int id;// ID speciality
    private String name;// Name speciality
    private int countCourse;// The number of courses in a particular specialty

    {
        id = 0;
        name = "";
        countCourse = 0;
    }
    public Speciality() {

    }
    public Speciality(@NotNull String name, int countCourse) {
        this();

        setName(name);
        setCountCourse(countCourse);
    }

    public int getId() {
        return id;
    }
    private void setId(int id) {
        if (id < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setId(id = " + id + ")");
            throw new RuntimeException("setId(id = "+ id + ")");
        }
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }
    public Speciality setName(@NotNull String name) {
        if("".equals(name)){
            Log.e(TAG, "Failed -> setName(name = " + name +")");
            throw new RuntimeException("setName(name = " + name +")");
        }
        this.name = name;
        return this;
    }

    public int getCountCourse() {
        return countCourse;
    }
    public Speciality setCountCourse(int countCourse) {
        if(countCourse < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setCountCourse(countCourse = " + countCourse + ")");
            throw new RuntimeException("setCountCourse(countCourse = " + countCourse + ")");
        }
        this.countCourse = countCourse;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Speciality that = (Speciality) o;
        return countCourse == that.countCourse &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, countCourse);
    }

    @Override
    public String toString() {
        return "Specialty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countCourse=" + countCourse +
                '}';
    }

    public void deleteAllLinks(){
        // Удаление специальности в дисциплине
        List<Subject> subjectList = DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class));
        for (Subject subject : subjectList) {
            if (subject.initMap().containsKeySpecialityCountHour(this)){
                if (subject.getSpecialityCountHourMap().size() == ConstantEntity.ONE) {
                    DBManager.delete(Subject.class, ConstantEntity.ID, subject.getId());
                } else {
                    subject.removeSpecialityCountHour(this);
                    DBManager.write(subject);
                }
            }
        }

        List<Subject> check = DBManager.readAll(Subject.class);

        // Удаление групп по специальности
        DBManager.deleteAll(Group.class, "idSpeciality", id);

        // Удаление специальности
        DBManager.delete(Speciality.class, ConstantEntity.ID, id);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // EntityI
    private static int countObj = 0;

    @Override
    public boolean isEntity() {
        return id > ConstantEntity.ZERO;
    }

    @Override
    public boolean createEntity() {
        if (!isEntity()){
            try {
                setName(name);
                setCountCourse(countCourse);
            } catch (RuntimeException ex) {
                return false;
            }

            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);
        }
        return true;
    }

    @Override
    public List<String> entityToNameList() {
        List<Speciality> specialityList = DBManager.copyObjectFromRealm(DBManager.readAll(Speciality.class, ConstantEntity.COUNT_COURSE));
        List<String> result = new ArrayList<>();

        for (Speciality speciality : specialityList){
            result.add(speciality.getName());
        }
        return result;
    }

    @Override
    public List<String> entityToNameList(List<Speciality> entityList) {
        List<String> result = new ArrayList<>();

        for (Speciality speciality : entityList){
            result.add(speciality.getName());
        }
        return result;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Parcelable
    protected Speciality(Parcel in) {
        id = in.readInt();
        name = in.readString();
        countCourse = in.readInt();
    }
    public static final Creator<Speciality> CREATOR = new Creator<Speciality>() {
        @Override
        public Speciality createFromParcel(Parcel in) {
            return new Speciality(in);
        }

        @Override
        public Speciality[] newArray(int size) {
            return new Speciality[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(countCourse);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Cloneable
    @NonNull
    @Override
    public Speciality clone() throws CloneNotSupportedException {
        return (Speciality) super.clone();
    }
}
