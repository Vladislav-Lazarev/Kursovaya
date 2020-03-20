package com.hpcc.kursovaya.dao.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Speciality extends RealmObject implements Entity<Speciality>, Parcelable {
    private static final String TAG = Speciality.class.getSimpleName();
    private static int countObj;

    static {
        countObj = 0;
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

    }
    protected Speciality(Parcel in) {
        id = in.readInt();
        name = in.readString();
        in.readInt();
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
    public boolean isEntity() {
        return !("".equals(name) || countCourse == ConstantEntity.ZERO || id == ConstantEntity.ZERO);
    }

    @Override
    public Speciality newEntity() throws Exception {
        if (isEntity()){
            try {
                setName(name);
                setCountCourse(countCourse);

                int maxID = DBManager.findMaxID(this.getClass());
                setId((maxID > ConstantEntity.ZERO) ? ++maxID : ++countObj);
            }
            catch (RuntimeException ex){
                String error = "Failed -> " + ex.getMessage();
                throw new Exception(error, ex);
            }
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
    public boolean equals(@NotNull Object obj) {
        Speciality speciality = (Speciality)obj;
        return this.name.equals(speciality.name);
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
