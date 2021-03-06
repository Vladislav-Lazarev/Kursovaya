package com.hpcc.kursovaya.dao.entity.schedule.template;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.EntityI;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class TemplateAcademicHour extends RealmObject implements EntityI<TemplateAcademicHour>, Parcelable, Cloneable {
    private static final String TAG = TemplateAcademicHour.class.getSimpleName();

    @PrimaryKey
    private int id;// ID template for half pair
    private int idSubject;// ID subject
    private int idGroup;// ID group

    private int numberDayOfWeek;// Number day of Week
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

    private void setId(int id){
        try{
            if (id < ConstantApplication.ONE){
                throw new Exception("Exception! setId()");
            }
            this.id = id;
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public int getIdSubject(){
        return idSubject;
    }

    public int getIdGroup(){
        return idGroup;
    }

    public Subject getSubject() {
        return DBManager.copyObjectFromRealm(
                DBManager.read(Subject.class, ConstantApplication.ID, idSubject));
    }
    public TemplateAcademicHour setSubject(@NotNull Subject subject) {

        this.idSubject = subject.getId();
        return this;
    }

    public Group getGroup() {
        return DBManager.copyObjectFromRealm(
                DBManager.read(Group.class, ConstantApplication.ID, idGroup));
    }
    public TemplateAcademicHour setGroup(@NotNull Group group) {

        this.idGroup = group.getId();
        return this;
    }

    public int getNumberDayOfWeek() {
        return numberDayOfWeek;
    }
    int getNumberHalfPair() {
        return numberHalfPair % ConstantApplication.TWO + ConstantApplication.ONE;
    }
    public int getNumberHalfPairButton(){
        return numberHalfPair;
    }

    public Pair<Integer, Integer> getDayAndPair(){
        return Pair.create(getNumberDayOfWeek(), getNumberHalfPair());
    }
    public Pair<Integer, Integer> getDayAndPairButton(){
        return Pair.create(getNumberDayOfWeek(), getNumberHalfPairButton());
    }

    private void setNumberHalfPair(int numberHalfPair) {
        if (numberHalfPair < ConstantApplication.ZERO || numberHalfPair >= ConstantApplication.MAX_COUNT_LESSON * ConstantApplication.TWO) {
            throw new RuntimeException("Exception! setNumberAcademicTwoHour() = " + numberHalfPair);
        }
        this.numberHalfPair = numberHalfPair;
    }
    private void setNumberDayOfWeek(int numberDayOfWeek) {
        if (numberDayOfWeek < ConstantApplication.MIN_DAY_OF_WEEK || numberDayOfWeek > ConstantApplication.MAX_DAY_OF_WEEK) {
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
    public int getId() {
        return id;
    }

    @Override
    public boolean existsEntity() {
        // TODO Пока коряво
        RealmResults<TemplateAcademicHour> existingEntities =
                DBManager.readAll(TemplateAcademicHour.class);
        for (TemplateAcademicHour entity : existingEntities) {
            if (this.equals(entity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEntity() {
        setSubject(getSubject());
        setGroup(getGroup());
        setDayAndPair(getDayAndPairButton());

        return id > ConstantApplication.ZERO;
    }
    @Override
    public void checkEntity() throws Exception {
        try {
            setGroup(getGroup());
            setSubject(getSubject());
            setDayAndPair(getDayAndPairButton());
        } catch(Throwable ex) {
            throw new Exception(ex);
        }
    }
    @Override
    public TemplateAcademicHour createEntity() throws Exception {
        if (!isEntity()){
            checkEntity();
            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantApplication.ZERO)? ++maxID : ++countObj);
        }

        return this;
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