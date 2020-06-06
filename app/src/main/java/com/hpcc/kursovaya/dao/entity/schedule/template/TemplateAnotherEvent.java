package com.hpcc.kursovaya.dao.entity.schedule.template;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.EntityI;
import com.hpcc.kursovaya.dao.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class TemplateAnotherEvent extends RealmObject implements EntityI<TemplateAnotherEvent>, Parcelable, Cloneable{
    private static final String TAG = TemplateAnotherEvent.class.getSimpleName();

    @PrimaryKey
    private int id;// ID template for half pair
    private String title;
    private int color;

    private int numberDayOfWeek;// Number day of Week
    private int numberHalfPair;// Number half pair

    {
        id = 0;
        title = "";

        numberDayOfWeek = -1;
        numberHalfPair = -1;
    }
    public TemplateAnotherEvent() {

    }
    public TemplateAnotherEvent(@NotNull String title, int color, @NotNull Pair<Integer, Integer> dayAndPair) {
        setTitle(title);
        setColor(color);
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

    public String getTitle() {
        return title;
    }
    public TemplateAnotherEvent setTitle(@NotNull String title) {
        this.title = title;
        return this;
    }

    public int getColor() {
        return color;
    }
    public TemplateAnotherEvent setColor(int color) {
        this.color = color;
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
    public TemplateAnotherEvent setDayAndPair(Pair<Integer, Integer> dayAndPair){
        setNumberDayOfWeek(dayAndPair.first);
        setNumberHalfPair(dayAndPair.second);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        TemplateAnotherEvent that = (TemplateAnotherEvent) o;
        return title.equals(that.title) &&
                color == that.color &&
                numberHalfPair == that.numberHalfPair;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, color, numberHalfPair);
    }

    @Override
    public String toString() {
        return "TemplateAcademicHour{" +
                "id=" + id +
                ", title=" + title +
                ", color=" + color +
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
        RealmResults<TemplateAnotherEvent> existingEntities =
                DBManager.readAll(TemplateAnotherEvent.class);
        for (TemplateAnotherEvent entity : existingEntities) {
            if (this.equals(entity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEntity() {
        setTitle(title);
        setColor(color);
        setDayAndPair(getDayAndPairButton());

        return id > ConstantApplication.ZERO;
    }
    @Override
    public void checkEntity() throws Exception {
        try {
            setTitle(title);
            setColor(color);
            setDayAndPair(getDayAndPairButton());
        } catch(Throwable ex) {
            throw new Exception(ex);
        }
    }
    @Override
    public TemplateAnotherEvent createEntity() throws Exception {
        if (!isEntity()){
            checkEntity();
            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantApplication.ZERO)? ++maxID : ++countObj);
        }

        return this;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Parcelable
    protected TemplateAnotherEvent(Parcel in) {
        id = in.readInt();
        title = in.readString();
        color = in.readInt();
        numberHalfPair = in.readInt();
    }
    public static final Creator<TemplateAnotherEvent> CREATOR = new Creator<TemplateAnotherEvent>() {
        @Override
        public TemplateAnotherEvent createFromParcel(Parcel in) {
            return new TemplateAnotherEvent(in);
        }

        @Override
        public TemplateAnotherEvent[] newArray(int size) {
            return new TemplateAnotherEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeInt(color);
        dest.writeInt(numberHalfPair);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Cloneable
    @NonNull
    @Override
    public TemplateAnotherEvent clone() throws CloneNotSupportedException {
        return (TemplateAnotherEvent) super.clone();
    }
}
