package com.hpcc.kursovaya.dao.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Group extends RealmObject implements EntityI<Group>, Parcelable, Cloneable {
    private static final String TAG = Group.class.getSimpleName();
    private static int countObj;

    static {
        countObj = 0;
    }

    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название(имя) группы
    private Speciality speciality;// Принадленость группы к специальности
    private int numberCourse;// Номер курса группы

    {
        id = 0;
        name = "";
        speciality = new Speciality();
        numberCourse = 0;
    }
    public Group() {

    }
    public Group(@NotNull String name, @NotNull Speciality speciality, int numberCourse) {
        this();

        setName(name);
        setSpecialty(speciality);
        setNumberCourse(numberCourse);

        createEntity();
    }
    protected Group(Parcel in) {
        id = in.readInt();
        name = in.readString();
        speciality = in.readParcelable(Speciality.class.getClassLoader());
        numberCourse = in.readInt();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public Group createEntity() {
        if (id < ConstantEntity.ZERO){
            setName(name);
            setSpecialty(speciality);
            setNumberCourse(numberCourse);

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

    public String getName() {
        return name;
    }
    public Group setName(@NotNull String name) {
        if("".equals(name)){
            Log.e(TAG, "Failed -> setName(name = " + name +")");
            throw new RuntimeException("setName(name = " + name +")");
        }
        this.name = name;
        return this;
    }

    public Speciality getSpecialty() {
        return speciality;
    }
    public Group setSpecialty(@NotNull Speciality speciality) {
        if(speciality.getId() < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setSpeciality("+speciality.toString()+")");
            throw new RuntimeException("setSpeciality("+speciality.toString()+")");
        }
        this.speciality = speciality;
        return this;
    }

    public int getNumberCourse() {
        return numberCourse;
    }
    public Group setNumberCourse(int numberCourse) {
        if(numberCourse < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setNumberCourse(numberCourse = " + numberCourse + ")");
            throw new RuntimeException("setNumberCourse(numberCourse = " + numberCourse + ")");
        }
        this.numberCourse = numberCourse;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Group group = (Group) o;
        return numberCourse == group.numberCourse &&
                name.equals(group.name) &&
                speciality.equals(group.speciality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, speciality, numberCourse);
    }

    @NonNull
    @Override
    public Group clone() throws CloneNotSupportedException {
        return (Group) super.clone();
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeParcelable(speciality, flags);
        dest.writeInt(numberCourse);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", specialty=" + speciality +
                ", numberCourse=" + numberCourse +
                '}';
    }
}
