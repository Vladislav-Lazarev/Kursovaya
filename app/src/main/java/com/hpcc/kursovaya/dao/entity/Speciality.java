package com.hpcc.kursovaya.dao.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateScheduleWeek;
import com.hpcc.kursovaya.dao.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.RealmObject;
import io.realm.RealmResults;
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

    private void setId(int id) {
        if (id < ConstantApplication.ONE){
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
        if(countCourse < ConstantApplication.ONE){
            Log.e(TAG, "Failed -> setCountCourse(countCourse = " + countCourse + ")");
            throw new RuntimeException("setCountCourse(countCourse = " + countCourse + ")");
        }
        this.countCourse = countCourse;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        Speciality that = (Speciality) obj;
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

    // Specific query
    public void deleteAllLinks(){
        //удаление полупар из расписания, которые имеют такую же специальность
        List<AcademicHour> academicHourList = DBManager.copyObjectFromRealm(DBManager.readAll(AcademicHour.class));
        for(AcademicHour academicHour : academicHourList){
            if(academicHour.getTemplateAcademicHour()!=null){
                TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                if(templateAcademicHour.getGroup()!=null){
                    Group group = templateAcademicHour.getGroup();
                    if(group.getSpecialty()!=null){
                        Speciality speciality = group.getSpecialty();
                        if(speciality!=null && speciality.getId()==getId()) {
                            DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID, templateAcademicHour.getId());
                            DBManager.delete(AcademicHour.class, ConstantApplication.ID, academicHour.getId());
                        }
                    }
                }
            }
        }
        //удаление шаблонов
        List<TemplateScheduleWeek> templateScheduleWeeks = DBManager.copyObjectFromRealm(DBManager.readAll(TemplateScheduleWeek.class));
        for(TemplateScheduleWeek templateScheduleWeek : templateScheduleWeeks){
            List<TemplateAcademicHour> templateAcademicHourList = templateScheduleWeek.getTemplateAcademicHourList();
            if(templateAcademicHourList!=null) {
                for (TemplateAcademicHour templateAcademicHour : templateAcademicHourList) {
                    Group group = templateAcademicHour.getGroup();
                    if (group != null) {
                        Speciality speciality = group.getSpecialty();
                        if (speciality != null && speciality.getId() == getId()) {
                            templateScheduleWeek.deleteTemplateAcademicHour(templateAcademicHour.getId());
                        }
                    }
                }
            }
        }

        // Удаление специальности в дисциплине
        List<Subject> subjectList = DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class));
        for (Subject subject : subjectList) {
            if (subject.initMap().containsKeySpecialityCountHour(this)){
                if (subject.getSpecialityCountHourMap().size() == ConstantApplication.ONE) {
                    DBManager.delete(Subject.class, ConstantApplication.ID, subject.getId());
                } else {
                    subject.removeSpecialityCountHour(this);
                    DBManager.write(subject);
                }
            }
        }

        // Удаление групп по специальности
        DBManager.deleteAll(Group.class, "idSpeciality", getId());

        // Удаление специальности
        DBManager.delete(Speciality.class, ConstantApplication.ID, getId());
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // EntityI
    private static int countObj = 0;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean existsEntity() {
        RealmResults<Speciality> existingEntities =
                DBManager.readAll(Speciality.class, ConstantApplication.NAME, this.getName(), ConstantApplication.NAME);
        for (Speciality entity : existingEntities) {
            if (this.getName().equals(entity.getName()) && this.getId() != entity.getId()) {
                Log.d(TAG, "DB Speciality = " + entity);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEntity() {
        return id > ConstantApplication.ZERO;
    }
    @Override
    public void checkEntity() throws Exception{
        try {
            setName(getName());
            setCountCourse(getCountCourse());
        } catch(RuntimeException ex) {
            throw new Exception("Entity = ", ex);
        }
    }
    @Override
    public Speciality createEntity() throws Exception {
        if (!isEntity()){
            checkEntity();
            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantApplication.ZERO)? ++maxID : ++countObj);
        }
        return this;
    }

    public static List<String> entityToNameList(List<Speciality> entityList) {
        List<String> result = new ArrayList<>();

        for (Speciality speciality : entityList){
            result.add(speciality.getName());
        }
        return result;
    }
    @Override
    public List<String> entityToNameList() {


        return entityToNameList(DBManager.readAll(Speciality.class, ConstantApplication.COUNT_COURSE));
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
