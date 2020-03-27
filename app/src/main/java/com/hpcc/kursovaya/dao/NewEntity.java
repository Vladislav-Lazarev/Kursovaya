package com.hpcc.kursovaya.dao;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import java.util.Objects;

public class NewEntity implements Parcelable, Cloneable {
    private int idSubject;
    private int idGroup;
    private int hoursPlan;
    private int hoursDeducted;
    private int hoursCanceled;

    {
        idSubject = 0;
        idGroup = 0;
        hoursPlan = 0;
        hoursDeducted = 0;
        hoursCanceled = 0;
    }
    public NewEntity() {
    }
    public NewEntity(Subject subject, Group group, int hoursPlan, int hoursDeducted, int hoursCanceled) {
        setSubject(subject);
        setHoursPlan(hoursPlan);
        setHoursDeducted(hoursDeducted);
        setHoursCanceled(hoursCanceled);
    }

    public Subject getSubject() {
        return DBManager.read(Subject.class, ConstantEntity.ID, idSubject);
    }
    public NewEntity setSubject(Subject subject) {
        this.idSubject = subject.getId();
        return  this;
    }

    public Group getGroup() {
        return DBManager.read(Group.class, ConstantEntity.ID, idGroup);
    }
    public NewEntity setGroup(Group group) {
        this.idGroup = group.getId();
        return  this;
    }

    public int getHoursPlan() {
        return hoursPlan;
    }
    public NewEntity setHoursPlan(int hoursPlan) {
        this.hoursPlan = hoursPlan;
        return  this;
    }

    public int getHoursDeducted() {
        return hoursDeducted;
    }
    public NewEntity setHoursDeducted(int hoursDeducted) {
        this.hoursDeducted = hoursDeducted;
        return  this;
    }

    public int getHoursCanceled() {
        return hoursCanceled;
    }
    public NewEntity setHoursCanceled(int hoursCanceled) {
        this.hoursCanceled = hoursCanceled;
        return  this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        NewEntity newEntity = (NewEntity) o;
        return idSubject == newEntity.idSubject &&
                idGroup == newEntity.idGroup &&
                hoursPlan == newEntity.hoursPlan &&
                hoursDeducted == newEntity.hoursDeducted &&
                hoursCanceled == newEntity.hoursCanceled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSubject, idGroup, hoursPlan, hoursDeducted, hoursCanceled);
    }

    @Override
    public String toString() {
        return "NewEntity{" +
                "idSubject=" + idSubject +
                ", idGroup=" + idGroup +
                ", hoursPlan=" + hoursPlan +
                ", hoursDeducted=" + hoursDeducted +
                ", hoursCanceled=" + hoursCanceled +
                '}';
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Parcelable
    protected NewEntity(Parcel in) {
        idSubject = in.readInt();
        idGroup = in.readInt();
        hoursPlan = in.readInt();
        hoursDeducted = in.readInt();
        hoursCanceled = in.readInt();
    }
    public static final Parcelable.Creator<NewEntity> CREATOR = new Parcelable.Creator<NewEntity>() {
        @Override
        public NewEntity createFromParcel(Parcel in) {
            return new NewEntity(in);
        }

        @Override
        public NewEntity[] newArray(int size) {
            return new NewEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idSubject);
        dest.writeInt(idGroup);
        dest.writeInt(hoursPlan);
        dest.writeInt(hoursDeducted);
        dest.writeInt(hoursCanceled);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Cloneable
    @NonNull
    @Override
    public NewEntity clone() throws CloneNotSupportedException {
        return (NewEntity) super.clone();
    }
}
