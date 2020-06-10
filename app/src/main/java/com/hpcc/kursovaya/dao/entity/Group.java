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

    private void setId(int id) {
        if (id < ConstantApplication.ONE){
            Log.e(TAG, "Failed -> setId(id = " + id + ")");
            throw new RuntimeException("setId(id = "+ id + ")");
        }
        this.id = id;
    }

    public String getName() {
        return ConstantApplication.textVisual(ConstantApplication.PATTERN_TEXT_VISUAL, name);
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
        return DBManager.copyObjectFromRealm(
                DBManager.read(Speciality.class, ConstantApplication.ID, idSpeciality));
    }
    public Group setSpecialty(@NotNull Speciality speciality) {
        if(speciality.getId() < ConstantApplication.ONE){
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
        if(numberCourse < ConstantApplication.ONE){
            Log.e(TAG, "Failed -> setCountCourse(countCourse = " + numberCourse + ")");
            throw new RuntimeException("setCountCourse(countCourse = " + numberCourse + ")");
        }
        this.numberCourse = numberCourse;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        Group group = (Group) obj;
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

    // Specific query
    public List<Subject> toSubjectList(int numberCourse){
        return toSubjectList(numberCourse, null);
    }
    public List<Subject> toSubjectList(Speciality speciality){
        return toSubjectList(null, speciality);
    }
    public List<Subject> toSubjectList(Integer numberCourse, Speciality speciality){
        // TODO Реалтзовать readAll что бы можно было сортиорвать по множеству занчений
        List<Subject> result = new ArrayList<>();

        // toSubjectList(int numberCourse)
        if (numberCourse != null && speciality == null){
            result = DBManager.readAll(Subject.class,
                    ConstantApplication.NUMBER_COURSE, this.getNumberCourse(),
                    ConstantApplication.NUMBER_COURSE);
            return result;
        }

        // toSubjectList(Speciality speciality) or
        // toSubjectList(Integer numberCourse, Speciality speciality)
        result = DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class,
                ConstantApplication.NUMBER_COURSE, this.getNumberCourse(),
                ConstantApplication.NAME));

        for (int i = 0; i< result.size(); i++){
            if (!result.get(i).initMap().containsKeySpecialityCountHour(speciality)){
                result.remove(i);
            }
        }

        return result;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // EntityI
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static int countObj = 0;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean existsEntity() {
        RealmResults<Group> existingEntities =
                DBManager.readAll(Group.class, ConstantApplication.NAME, this.getName(), ConstantApplication.NAME);
        for (Group entity : existingEntities) {
            if (this.getName().equals(entity.getName()) && this.getId() != entity.getId()) {
                Log.d(TAG, "DB Group = " + entity);
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
    public void checkEntity() throws Exception {
        try {
            setName(name);
            setSpecialty(getSpecialty());
            setNumberCourse(getNumberCourse());
        } catch(RuntimeException ex) {
            throw new Exception("Entity = ", ex);
        }
    }
    @Override
    public Group createEntity() throws Exception {
        if (!isEntity()){
            checkEntity();
            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantApplication.ZERO)? ++maxID : ++countObj);
        }
        return this;
    }

    public static List<String> entityToNameList(List<Group> entityList) {
        List<String> result = new ArrayList<>();

        for (Group group : entityList){
            result.add(group.getName());
        }
        return result;
    }
    @Override
    public List<String> entityToNameList() {
        return entityToNameList(DBManager.readAll(Group.class, ConstantApplication.COUNT_COURSE));
    }

    public static int findMaxNumberCourse(List<Group> groupList){
        int maxNumberCourse = 1;

        for(Group group: groupList){
            if(group.getNumberCourse() > maxNumberCourse){
                maxNumberCourse = group.getNumberCourse();
            }
        }

        return maxNumberCourse;
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

    public void deleteAllLinks() {
        //удаление полупар из расписания, которые имеют такую же группу
        List<AcademicHour> academicHourList = DBManager.copyObjectFromRealm(DBManager.readAll(AcademicHour.class));
        for(AcademicHour academicHour : academicHourList){
            if(academicHour.getTemplateAcademicHour()!=null){
                TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                if(templateAcademicHour.getGroup()!=null){
                    Group group = templateAcademicHour.getGroup();
                    if(group!=null && group.getId()==getId()){
                        DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID, templateAcademicHour.getId());
                        DBManager.delete(AcademicHour.class, ConstantApplication.ID, academicHour.getId());
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
                    if (group != null && group.getId()==getId()) {
                        templateScheduleWeek.deleteTemplateAcademicHour(templateAcademicHour.getId());
                    }
                }
            }
        }
        // Удаление групп по специальности
        DBManager.delete(Group.class, ConstantApplication.ID, getId());
    }

    public int getPlanHours(Subject subject, List<AcademicHour> academicHourList){
        int result = 0;
        for(AcademicHour academicHour : academicHourList){
            TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
            if(templateAcademicHour!=null){
                Subject subjectFromHour = templateAcademicHour.getSubject();
                if(subjectFromHour!=null && subject.equals(subjectFromHour)){
                    result++;
                }
            }
        }
        return result;
    }

    public int getReadHours(Subject subject, List<AcademicHour> academicHourList){
        int result = 0;
        for(AcademicHour academicHour : academicHourList){
            TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
            if(templateAcademicHour!=null){
                Subject subjectFromHour = templateAcademicHour.getSubject();
                if(subjectFromHour!=null && subject.equals(subjectFromHour) && academicHour.hasCompleted()){
                    result++;
                }
            }
        }
        return result;
    }

    public int getCanceledHours(Subject subject, List<AcademicHour> academicHourList){
        int result = 0;
        for(AcademicHour academicHour : academicHourList){
            TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
            if(templateAcademicHour!=null){
                Subject subjectFromHour = templateAcademicHour.getSubject();
                if(subjectFromHour!=null && subject.equals(subjectFromHour) && academicHour.hasCanceled()){
                    result++;
                }
            }
        }
        return result;
    }

    public SubjectGroupsInfo toSubjectGroupsInfo(Subject subject, List<AcademicHour> academicHourList) {
        SubjectGroupsInfo result = new SubjectGroupsInfo();
        int hoursPlan = subject.getSpecialityCountHourMap().get(getSpecialty());
        int hourCompleted = getReadHours(subject,academicHourList);
        int hourCanceled = getCanceledHours(subject,academicHourList);
        result.setGroup(this);
        result.setSubject(subject);
        result.setHoursPlan(hoursPlan);
        result.setHoursCanceled(hourCanceled);
        result.setHoursDeducted(hourCompleted);
        return result;
    }

    public int getIdSpeciality() {
        return idSpeciality;
    }

    public void setIdSpeciality(int idSpeciality) {
        this.idSpeciality = idSpeciality;
    }
}
