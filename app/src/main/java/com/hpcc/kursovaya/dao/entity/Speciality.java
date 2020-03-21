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
    private static int countObj;

    static {
        countObj = 0;
    }

    public static void deleteAllLinks(@NotNull Speciality speciality){
        // Удаление специальности в дисциплине
        List<Subject> list = DBManager.readAll(Subject.class, ConstantEntity.ID);
        ArrayList<Subject> subjectArrayList = new ArrayList<>(list);
        Log.d("deleteAllLinks", "list = " + list.toString());
        Log.d("deleteAllLinks", "subjectArrayList = " + subjectArrayList.toString());
        for (Subject subject : subjectArrayList){
            Log.d("deleteAllLinks", "subject = " + subject.toString());
            if (subject.initMap().containsKeySpecialityCountHour(speciality)){
                Subject modifiedSubject = new Subject();
                subject.removeSpecialityCountHour(speciality);
                try {
                    modifiedSubject = subject.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                DBManager.delete(Subject.class, ConstantEntity.ID, subject.getId());
                DBManager.write(modifiedSubject);
            }
        }

        // Удаление групп по специальности
        for (Group group : DBManager.readAll(Group.class, ConstantEntity.ID)){
            if (speciality.equals(group.getSpecialty())){
                DBManager.delete(Group.class, ConstantEntity.ID, group.getId());
            }
        }

        // Удаление специальности
        DBManager.delete(Speciality.class, ConstantEntity.ID, speciality.getId());
    }

    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название(имя) специальности
    private int countCourse;// Количество проведения курсов в конкретной специальности

    public Speciality() {
        id = 0;
        name = "";
        countCourse = 0;
    }
    public Speciality(@NotNull String name, int countCourse) {
        this();

        setName(name);
        setCountCourse(countCourse);

        createEntity();
    }
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
    public Speciality createEntity() {
        if (id < ConstantEntity.ZERO){
            setName(name);
            setCountCourse(countCourse);

            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);
        }
        return this;
    }

    private void setId(int id) {
        if (id < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setId(id = " + id + ")");
            throw new RuntimeException("setId(id = "+ id + ")");
        }
        this.id = id;
    }
    public int getId() {
        return id;
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

    @NonNull
    @Override
    public Speciality clone() throws CloneNotSupportedException {
        return (Speciality) super.clone();
    }

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

    @Override
    public String toString() {
        return "Specialty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countCourse=" + countCourse +
                '}';
    }
}
