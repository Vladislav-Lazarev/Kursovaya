package com.hpcc.kursovaya.dao.entity.schedule.lesson.template;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.hpcc.kursovaya.dao.entity.EntityI;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class TemplateScheduleWeek extends RealmObject implements EntityI<TemplateScheduleWeek>, Parcelable, Cloneable {
    private static final String TAG = TemplateScheduleWeek.class.getSimpleName();

    protected static RealmList<Integer> convert(List<TemplateAcademicHour> templateScheduleDayList) {
        RealmList<Integer> result = new RealmList<>();

        for (TemplateAcademicHour templateAcademicHour : templateScheduleDayList){
            result.add(templateAcademicHour.getId());
        }
        return result;
    }
    protected static List<TemplateAcademicHour> convert(RealmList<Integer> idTemplateAcademicHourList) {
        List<TemplateAcademicHour> result = new RealmList<>();

        for (Integer id : idTemplateAcademicHourList){
            result.add(DBManager.read(TemplateAcademicHour.class, ConstantEntity.ID, id));
        }
        return result;
    }

    @PrimaryKey
    private int id;
    private String name;
    private RealmList<Integer> idTemplateAcademicHourList;

    {
        id = 0;
        name = "";
        idTemplateAcademicHourList = new RealmList<>();
    }
    public TemplateScheduleWeek() {

    }
    public TemplateScheduleWeek(@NotNull String name, @NotNull List<TemplateAcademicHour> templateAcademicHourList) {
        setName(name);
        setTemplateAcademicHourList(templateAcademicHourList);
    }

    private void setId(int id){
        if (id < ConstantEntity.ONE){
            throw new RuntimeException("Exception! setId()");
        }
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }
    public TemplateScheduleWeek setName(@NotNull String name) {
        if (name.isEmpty()) {
            throw new RuntimeException("Exception! setName()");
        }
        this.name = name;
        return this;
    }

    @NotNull
    public List<TemplateAcademicHour> getTemplateAcademicHourList() {
        return convert(idTemplateAcademicHourList);
    }
    public TemplateScheduleWeek setTemplateAcademicHourList(@NotNull List<TemplateAcademicHour> templateAcademicHourList) {
        if (templateAcademicHourList.size() > ConstantEntity.ZERO) {
            throw new RuntimeException("Exception! setTemplateScheduleDayList()");
        }
        this.idTemplateAcademicHourList = convert(templateAcademicHourList);
        return this;
    }

    @Override
    public String toString() {
        return "TemplateScheduleWeek{" +
                "id=" + id +
                ", name=" + name +
                ", idTemplateAcademicHourList=" + idTemplateAcademicHourList.toString() +
                '}';
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // EntityI
    private static int countObj = 0;


    public boolean existsEntity() {
        // TODO Пока коряво
        RealmResults<TemplateScheduleWeek> existingEntities =
                DBManager.readAll(TemplateScheduleWeek.class, ConstantEntity.NAME, this.getName());
        for (TemplateScheduleWeek entity : existingEntities) {
            if (this.equals(entity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEntity() {
        setName(name);
        setTemplateAcademicHourList(getTemplateAcademicHourList());

        return id > ConstantEntity.ZERO;
    }
    @Override
    public List<String> entityToNameList() {
        return entityToNameList(DBManager.readAll(TemplateScheduleWeek.class, ConstantEntity.NAME));
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Parcelable
    protected TemplateScheduleWeek(Parcel in) {
        id = in.readInt();
        in.readList(idTemplateAcademicHourList, TemplateScheduleWeek.class.getClassLoader());
    }
    public static final Creator<TemplateScheduleWeek> CREATOR = new Creator<TemplateScheduleWeek>() {
        @Override
        public TemplateScheduleWeek createFromParcel(Parcel in) {
            return new TemplateScheduleWeek(in);
        }

        @Override
        public TemplateScheduleWeek[] newArray(int size) {
            return new TemplateScheduleWeek[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeList(getTemplateAcademicHourList());
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Cloneable
    @NonNull
    @Override
    public TemplateScheduleWeek clone() throws CloneNotSupportedException {
        return (TemplateScheduleWeek) super.clone();
    }
}
