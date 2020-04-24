package com.hpcc.kursovaya.dao.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.query.DBManager;

import java.util.Objects;

public class SubjectGroupsInfo implements Parcelable, Cloneable {
    private int idSubject;
    private int idGroup;
    private int hoursPlan;
    private int hoursDeducted;
    private int hoursCanceled;
    private int rest;

    {
        idSubject = 0;
        idGroup = 0;
        hoursPlan = 0;
        hoursDeducted = 0;
        hoursCanceled = 0;
        rest = 0;
    }

    public SubjectGroupsInfo() {

    }

    public SubjectGroupsInfo(Subject subject, Group group, int hoursPlan, int hoursDeducted, int hoursCanceled) {
        setSubject(subject);
        setHoursPlan(hoursPlan);
        setHoursDeducted(hoursDeducted);
        setHoursCanceled(hoursCanceled);
        rest = hoursPlan - hoursCanceled - hoursDeducted;
    }


    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public Subject getSubject() {
        return DBManager.read(Subject.class, ConstantApplication.ID, idSubject);
    }

    public SubjectGroupsInfo setSubject(Subject subject) {
        this.idSubject = subject.getId();
        return  this;
    }

    public Group getGroup() {
        return DBManager.read(Group.class, ConstantApplication.ID, idGroup);
    }

    public SubjectGroupsInfo setGroup(Group group) {
        this.idGroup = group.getId();
        return  this;
    }

    public int getHoursPlan() {
        return hoursPlan;
    }
    public SubjectGroupsInfo setHoursPlan(int hoursPlan) {
        this.hoursPlan = hoursPlan;
        return  this;
    }

    public int getHoursDeducted() {
        return hoursDeducted;
    }
    public SubjectGroupsInfo setHoursDeducted(int hoursDeducted) {
        this.hoursDeducted = hoursDeducted;
        return  this;
    }

    public int getHoursCanceled() {
        return hoursCanceled;
    }
    public SubjectGroupsInfo setHoursCanceled(int hoursCanceled) {
        this.hoursCanceled = hoursCanceled;
        return  this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        SubjectGroupsInfo subjectGroupsInfo = (SubjectGroupsInfo) o;
        return idSubject == subjectGroupsInfo.idSubject &&
                idGroup == subjectGroupsInfo.idGroup &&
                hoursPlan == subjectGroupsInfo.hoursPlan &&
                hoursDeducted == subjectGroupsInfo.hoursDeducted &&
                hoursCanceled == subjectGroupsInfo.hoursCanceled;
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
    protected SubjectGroupsInfo(Parcel in) {
        idSubject = in.readInt();
        idGroup = in.readInt();
        hoursPlan = in.readInt();
        hoursDeducted = in.readInt();
        hoursCanceled = in.readInt();
    }
    public static final Creator<SubjectGroupsInfo> CREATOR = new Creator<SubjectGroupsInfo>() {
        @Override
        public SubjectGroupsInfo createFromParcel(Parcel in) {
            return new SubjectGroupsInfo(in);
        }

        @Override
        public SubjectGroupsInfo[] newArray(int size) {
            return new SubjectGroupsInfo[size];
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
    public SubjectGroupsInfo clone() throws CloneNotSupportedException {
        return (SubjectGroupsInfo) super.clone();
    }
}
