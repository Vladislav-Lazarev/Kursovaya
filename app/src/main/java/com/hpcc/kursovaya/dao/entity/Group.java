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

public class Group extends RealmObject implements EntityI<Group>, Parcelable, Cloneable {
    private static final String TAG = Group.class.getSimpleName();

    @PrimaryKey
    private int id;// ID group
    private String name;// Name group
    private int idSpeciality;// ID speciality
    private int numberCourse;// Number course group

    {
        id = 0;
        name = "";
        idSpeciality = 0;
        numberCourse = 0;
    }
    public Group() {

    }
    public Group(@NotNull String name, @NotNull Speciality speciality, int numberCourse) {
        setName(name);
        setSpecialty(speciality);
        setNumberCourse(numberCourse);
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
        return DBManager.read(Speciality.class, ConstantEntity.ID, idSpeciality);
    }
    public Group setSpecialty(@NotNull Speciality speciality) {
        if(speciality.getId() < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setSpeciality("+speciality.toString()+")");
            throw new RuntimeException("setSpeciality("+speciality.toString()+")");
        }

        this.idSpeciality = speciality.getId();
        return this;
    }

    public int getNumberCourse() {
        return numberCourse;
    }
    public Group setNumberCourse(int numberCourse) {
        if(numberCourse < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setCountCourse(countCourse = " + numberCourse + ")");
            throw new RuntimeException("setCountCourse(countCourse = " + numberCourse + ")");
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
                idSpeciality == group.idSpeciality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, idSpeciality, numberCourse);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", idSpeciality=" + idSpeciality +
                ", numberCourse=" + numberCourse +
                '}';
    }

    public List<Subject> toSubjectList(int countCourse){
        return toSubjectList(countCourse, null);
    }
    public List<Subject> toSubjectList(Speciality speciality){
        return toSubjectList(null, speciality);
    }
    public List<Subject> toSubjectList(Integer countCourse, Speciality speciality){
        // TODO Реалтзовать readAll что бы можно было сортиорвать по множеству занчений
        List<Subject> result = new ArrayList<>();
        List<Subject> subjectList = null;

        if (countCourse != null && speciality == null){
            result = DBManager.copyObjectFromRealm(
                    DBManager.readAll(Subject.class, ConstantEntity.NUMBER_COURSE, this.numberCourse, ConstantEntity.NAME));
            return result;
        }

        if (countCourse != null){
            subjectList = DBManager.copyObjectFromRealm(
                    DBManager.readAll(Subject.class, ConstantEntity.NUMBER_COURSE, this.numberCourse, ConstantEntity.NAME));
        }

        if (speciality != null){
            int idlePassage = 0;
            for (Subject subject : subjectList){
                idlePassage--;
                if (subject.initMap().containsKeySpecialityCountHour(speciality)){
                    result.add(subject);
                    idlePassage = result.size();
                }
                if (idlePassage < result.size()){
                    break;
                }
            }
        }

        return result;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // EntityI
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static int countObj = 0;
    @Override
    public boolean isEntity() {
        if (id < ConstantEntity.ONE){
            try {
                setName(name);
                setSpecialty(DBManager.read(Speciality.class, ConstantEntity.ID, idSpeciality));
                setNumberCourse(numberCourse);
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
        List<Group> groupList = DBManager.copyObjectFromRealm(DBManager.readAll(Group.class, ConstantEntity.COUNT_COURSE));
        List<String> result = new ArrayList<>();

        for (Group group : groupList){
            result.add(group.getName());
        }
        return result;
    }

    @Override
    public List<String> entityToNameList(List<Group> entityList) {
        List<String> result = new ArrayList<>();

        for (Group group : entityList){
            result.add(group.getName());
        }
        return result;
    }

    // Parcelable
    protected Group(Parcel in) {
        id = in.readInt();
        name = in.readString();
        idSpeciality = in.readInt();
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
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(idSpeciality);
        dest.writeInt(numberCourse);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Cloneable
    @NonNull
    @Override
    public Group clone() throws CloneNotSupportedException {
        return (Group) super.clone();
    }
}
