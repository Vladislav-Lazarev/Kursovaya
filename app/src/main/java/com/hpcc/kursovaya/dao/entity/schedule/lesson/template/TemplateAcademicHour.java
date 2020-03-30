package com.hpcc.kursovaya.dao.entity.schedule.lesson.template;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.hpcc.kursovaya.dao.entity.EntityI;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TemplateAcademicHour extends RealmObject implements EntityI, Parcelable, Cloneable {
    private static final String TAG = TemplateAcademicHour.class.getSimpleName();

    @PrimaryKey
    private int id;// ID template for half pair
    private int idSubject;// ID subject
    private int idGroup;// ID group

    private int numberDayOfWeek;
    private int numberHalfPair;// Number half pair

    {
        id = 0;
        idSubject = 0;
        idGroup = 0;

        numberDayOfWeek = -1;
        numberHalfPair = -1;
    }
    public TemplateAcademicHour() {

    }
    public TemplateAcademicHour(@NotNull Subject subject, @NotNull Group group, Pair<Integer, Integer> dayAndPair) {
        setSubject(subject);
        setGroup(group);
        setNumberHalfPair(numberHalfPair);
    }

    public int getId() {
        return id;
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

    @NotNull
    public Subject getSubject() {
        return DBManager.read(Subject.class, ConstantEntity.ID, idSubject);
    }
    public TemplateAcademicHour setSubject(@NotNull Subject subject) {
        if(subject.getId() < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setSubject(" + subject.toString() + ")");
            throw new RuntimeException("setSubject(" + subject.toString() + ")");
        }

        this.idSubject = subject.getId();
        return this;
    }

    public Group getGroup() {
        return DBManager.read(Group.class, ConstantEntity.ID, idGroup);
    }
    public TemplateAcademicHour setGroup(@NotNull Group group) {
        if(group.getId() < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setGroup(" + group.toString() + ")");
            throw new RuntimeException("setGroup(" + group.toString() + ")");
        }

        this.idGroup = group.getId();
        return this;
    }

    int getNumberDayOfWeek() {
        return numberDayOfWeek;
    }
    int getNumberHalfPair() {
        return numberHalfPair % ConstantEntity.TWO + ConstantEntity.ONE;
    }
    int getNumberHalfPairButton(){
        return numberHalfPair;
    }
    public Pair<Integer, Integer> getDayAndPair(){
        return Pair.create(numberDayOfWeek, getNumberHalfPair());
    }
    private void setNumberHalfPair(int numberHalfPair) {
        if (numberHalfPair < ConstantEntity.ZERO || numberHalfPair >= ConstantEntity.MAX_COUNT_LESSON * ConstantEntity.TWO) {
            throw new RuntimeException("Exception! setNumberAcademicTwoHour() = " + numberHalfPair);
        }
        this.numberHalfPair = numberHalfPair;
    }
    private void setNumberDayOfWeek(int numberDayOfWeek) {
        if (numberDayOfWeek < ConstantEntity.MIN_DAY_OF_WEEK || numberDayOfWeek >= ConstantEntity.MAX_DAY_OF_WEEK) {
            throw new RuntimeException("Exception! setNumberAcademicTwoHour() = " + numberHalfPair);
        }
        this.numberDayOfWeek = numberDayOfWeek;
    }
    public TemplateAcademicHour setDayAndPair(Pair<Integer, Integer> dayAndPair){
        setNumberDayOfWeek(dayAndPair.first);
        setNumberHalfPair(dayAndPair.second);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        TemplateAcademicHour that = (TemplateAcademicHour) o;
        return idSubject == that.idSubject &&
                idGroup == that.idGroup &&
                numberHalfPair == that.numberHalfPair;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSubject, idGroup, numberHalfPair);
    }

    @Override
    public String toString() {
        return "TemplateAcademicHour{" +
                "id=" + id +
                ", idSubject=" + idSubject +
                ", idGroup=" + idGroup +
                ", numberDayOfWeek=" + numberDayOfWeek +
                ", numberHalfPair=" + numberHalfPair +
                '}';
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
                setSubject(getSubject());
                setGroup(getGroup());
                setNumberHalfPair(numberHalfPair);
            } catch (RuntimeException ex) {
                return false;
            }

            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);
        }
        return true;
    }

    @Override
    public Pair<List<String>, List<String>> entityToNameList() {
        Pair<List<String>, List<String>> result = Pair.create(new Subject().entityToNameList(),
                new Group().entityToNameList());
        return result;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Parcelable
    protected TemplateAcademicHour(Parcel in) {
        id = in.readInt();
        idSubject = in.readInt();
        idGroup = in.readInt();
        numberHalfPair = in.readInt();
    }
    public static final Creator<TemplateAcademicHour> CREATOR = new Creator<TemplateAcademicHour>() {
        @Override
        public TemplateAcademicHour createFromParcel(Parcel in) {
            return new TemplateAcademicHour(in);
        }

        @Override
        public TemplateAcademicHour[] newArray(int size) {
            return new TemplateAcademicHour[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(idSubject);
        dest.writeInt(idGroup);
        dest.writeInt(numberHalfPair);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Cloneable
    @NonNull
    @Override
    public TemplateAcademicHour clone() throws CloneNotSupportedException {
        return (TemplateAcademicHour) super.clone();
    }
}