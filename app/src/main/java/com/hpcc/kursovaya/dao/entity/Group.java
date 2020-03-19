package com.hpcc.kursovaya.dao.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Group extends RealmObject implements Entity<Group>, Parcelable {
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

    public Group() {
        id = 0;
        name = "";
        speciality = new Speciality();
        numberCourse = 0;
    }
    public Group(@NotNull String name, @NotNull Speciality speciality, int numberCourse) {
        this();
        int maxID = DBManager.findMaxID(this.getClass());

        setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);
        setName(name);
        setSpecialty(speciality);
        setNumberCourse(numberCourse);
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
    public boolean hasEntity() {
        return !("".equals(name) || speciality.getId() < ConstantEntity.ONE || numberCourse == ConstantEntity.ZERO);
    }
    @Override
    public Group newEntity() {
        if (hasEntity()){
            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);
        }
        return this;
    }

    private void setId(int id) {
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

    public String getName() {
        return name;
    }
    public Group setName(String name) {
        // TODO setName - сделать проверку
        this.name = name;
        return this;
    }

    public Speciality getSpecialty() {
        return speciality;
    }
    public Group setSpecialty(Speciality speciality) {
        // TODO setName - сделать проверку
        this.speciality = speciality;
        return this;
    }

    public int getNumberCourse() {
        return numberCourse;
    }
    public Group setNumberCourse(int numberCourse) {
        // TODO setName - сделать проверку
        this.numberCourse = numberCourse;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        Group group = (Group)obj;
        return this.name.equals(group.name);
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
