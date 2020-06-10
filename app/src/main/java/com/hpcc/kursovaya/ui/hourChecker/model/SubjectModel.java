package com.hpcc.kursovaya.ui.hourChecker.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SubjectModel implements Parcelable {
    private boolean isFull = false;
    private Subject subject;
    private int planHours;
    private int totalInSchedule;
    private int restHours;
    private List<AcademicHour> readList;
    private List<AcademicHour> canceledHours;
    private List<AcademicHour> unreadHours;
    private List<TemplateAcademicHour> templateAcademicHours;

    public SubjectModel(Subject subject, int planHours, List<AcademicHour> readList, List<AcademicHour> canceledHours, List<AcademicHour> unreadHours) {
        this.subject = subject;
        this.planHours = planHours;
        this.readList = readList;
        this.canceledHours = canceledHours;
        this.unreadHours = unreadHours;
        totalInSchedule = readList.size()+canceledHours.size()+unreadHours.size();
        restHours = planHours - (readList.size()+canceledHours.size());
        if(planHours==totalInSchedule){
            isFull = true;
        }
        order(readList);
        order(canceledHours);
        order(unreadHours);
    }

    private void order(List<AcademicHour> hours) {

        Collections.sort(hours, (Comparator<AcademicHour>) (o1, o2) -> {

            Date x1 = (o1).getDate();
            Date x2 = ( o2).getDate();
            return x1.compareTo(x2);
        });
    }

    public SubjectModel(Parcel in) {
        readList = new ArrayList<>();
        canceledHours = new ArrayList<>();
        unreadHours = new ArrayList<>();
        isFull = in.readByte() != 0;
        subject = in.readParcelable(Subject.class.getClassLoader());
        subject = subject.initMap();
        planHours = in.readInt();
        totalInSchedule = in.readInt();
        restHours = in.readInt();
        in.readList(readList,AcademicHour.class.getClassLoader());
        in.readList(canceledHours,AcademicHour.class.getClassLoader());
        in.readList(unreadHours,AcademicHour.class.getClassLoader());
    }

    public static final Creator<SubjectModel> CREATOR = new Creator<SubjectModel>() {
        @Override
        public SubjectModel createFromParcel(Parcel in) {
            return new SubjectModel(in);
        }

        @Override
        public SubjectModel[] newArray(int size) {
            return new SubjectModel[size];
        }
    };

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<AcademicHour> getReadList() {
        return readList;
    }

    public void setReadList(List<AcademicHour> readList) {
        this.readList = readList;
    }

    public List<AcademicHour> getCanceledHours() {
        return canceledHours;
    }

    public void setCanceledHours(List<AcademicHour> canceledHours) {
        this.canceledHours = canceledHours;
    }

    public List<AcademicHour> getUnreadHours() {
        return unreadHours;
    }

    public void setUnreadHours(List<AcademicHour> unreadHours) {
        this.unreadHours = unreadHours;
    }

    public int getPlanHours() {
        return planHours;
    }

    public void setPlanHours(int planHours) {
        this.planHours = planHours;
    }

    public int getTotalInSchedule() {
        return totalInSchedule;
    }

    public void setTotalInSchedule(int totalInSchedule) {
        this.totalInSchedule = totalInSchedule;
    }

    public int getRestHours() {
        return restHours;
    }

    public void setRestHours(int restHours) {
        this.restHours = restHours;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) ((isFull)? 1:0));
        dest.writeParcelable(subject,flags);
        dest.writeInt(planHours);
        dest.writeInt(totalInSchedule);
        dest.writeInt(restHours);
        dest.writeList(readList);
        dest.writeList(canceledHours);
        dest.writeList(unreadHours);
    }
}
