package com.hpcc.kursovaya.dao.entity.schedule.lesson;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.hpcc.kursovaya.dao.entity.EntityI;
import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class ScheduleWeek extends RealmObject implements EntityI<ScheduleWeek>, Parcelable, Cloneable {
    private static final String TAG = ScheduleWeek.class.getSimpleName();

    protected static RealmList<Integer> convert(List<AcademicHour> templateScheduleDayList) {
        RealmList<Integer> result = new RealmList<>();

        for (AcademicHour AcademicHour : templateScheduleDayList){
            result.add(AcademicHour.getId());
        }
        return result;
    }
    protected static List<AcademicHour> convert(RealmList<Integer> idAcademicHourList) {
        List<AcademicHour> result = new RealmList<>();

        for (Integer id : idAcademicHourList){
            result.add(DBManager.read(AcademicHour.class, ConstantApplication.ID, id));
        }
        return result;
    }

    @PrimaryKey
    private int id;
    private String name;
    private RealmList<Integer> idAcademicHourList;

    {
        id = 0;
        name = "";
        idAcademicHourList = new RealmList<>();
    }
    public ScheduleWeek() {

    }
    public ScheduleWeek(@NotNull String name, @NotNull List<AcademicHour> AcademicHourList) {
        setName(name);
        setAcademicHourList(AcademicHourList);
    }

    private void setId(int id){
        if (id < ConstantApplication.ONE){
            throw new RuntimeException("Exception! setId()");
        }
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }
    public ScheduleWeek setName(@NotNull String name) {
        if (name.isEmpty()) {
            throw new RuntimeException("Exception! setName()");
        }
        this.name = name;
        return this;
    }

    @NotNull
    public List<AcademicHour> getAcademicHourList() {
        return convert(idAcademicHourList);
    }
    public ScheduleWeek setAcademicHourList(@NotNull List<AcademicHour> AcademicHourList) {
        if (AcademicHourList.size() > ConstantApplication.ZERO) {
            throw new RuntimeException("Exception! setTemplateScheduleDayList()");
        }
        this.idAcademicHourList = convert(AcademicHourList);
        return this;
    }

    @Override
    public String toString() {
        return "ScheduleWeek{" +
                "id=" + id +
                ", name=" + name +
                ", idAcademicHourList=" + idAcademicHourList.toString() +
                '}';
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // EntityI
    private static int countObj = 0;

    @Override
    public int getId() {
        return id;
    }

    public boolean existsEntity() {
        // TODO Пока коряво
        RealmResults<ScheduleWeek> existingEntities =
                DBManager.readAll(ScheduleWeek.class, ConstantApplication.NAME, this.getName());
        for (ScheduleWeek entity : existingEntities) {
            if (this.equals(entity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEntity() {
        setName(name);
        setAcademicHourList(getAcademicHourList());

        return id > ConstantApplication.ZERO;
    }
    @Override
    public void checkEntity() throws Exception {
        try {
            setName(getName());
            setAcademicHourList(getAcademicHourList());
        } catch(RuntimeException ex) {
            throw new Exception("Entity = ", ex);
        }
    }
    @Override
    public ScheduleWeek createEntity() throws Exception {
        if (!isEntity()){
            checkEntity();
            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantApplication.ZERO)? ++maxID : ++countObj);
        }

        return this;
    }

    public static List<String> entityToNameList(List<ScheduleWeek> entityList) {
        List<String> result = new ArrayList<>();

        for (ScheduleWeek scheduleWeek : entityList){
            result.add(scheduleWeek.getName());
        }
        return result;
    }
    @Override
    public List<String> entityToNameList() {
        return entityToNameList(DBManager.readAll(ScheduleWeek.class, ConstantApplication.NAME));
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Parcelable
    protected ScheduleWeek(Parcel in) {
        id = in.readInt();
        in.readList(idAcademicHourList, ScheduleWeek.class.getClassLoader());
    }
    public static final Creator<ScheduleWeek> CREATOR = new Creator<ScheduleWeek>() {
        @Override
        public ScheduleWeek createFromParcel(Parcel in) {
            return new ScheduleWeek(in);
        }

        @Override
        public ScheduleWeek[] newArray(int size) {
            return new ScheduleWeek[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeList(getAcademicHourList());
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Cloneable
    @NonNull
    @Override
    public ScheduleWeek clone() throws CloneNotSupportedException {
        return (ScheduleWeek) super.clone();
    }
}
