package com.hpcc.kursovaya.dao.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Subject extends RealmObject implements Entity, Parcelable {
    private static final String TAG = Subject.class.getSimpleName();
    private static int countObj;

    static {
        countObj = 0;
    }

    @PrimaryKey
    private int id;// Индентификатор
    @Required
    private String name;// Название дисциплины
    private RealmList<Speciality> specialityList;
    private RealmList<Integer> countHourList;
    @Ignore
    private Map<Speciality, Integer> specialityCountHourMap;
    private int numberCourse;// Номер курса
    private int color;// Цвет дисциплины

    public Subject() {
        id = 0;
        name = "";
        specialityList = new RealmList<>();
        countHourList = new RealmList<>();
        specialityCountHourMap = new HashMap<>();
        numberCourse = 0;
        color = 0;
    }
    public Subject(@NotNull String name, Map<Speciality, Integer> specialityCountHourMap, int numberCourse, int color) {
        this();

        setName(name);
        putAll(specialityCountHourMap);
        setNumberCourse(numberCourse);
        setColor(color);

        newEntity();
    }
    protected Subject(Parcel in) {
        id = in.readInt();
        name = in.readString();
        in.readList(specialityList, Speciality.class.getClassLoader());
        in.readList(countHourList, Integer.class.getClassLoader());
        numberCourse = in.readInt();
        color = in.readInt();
    }
    private void initMap(){
        for (int i = 0; i < specialityList.size() && i < countHourList.size(); i++){
            specialityCountHourMap.put(specialityList.get(i), countHourList.get(i));
        }
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    @Override
    public boolean hasEntity() {
        // TODO hasEntity
        return !("".equals(name) && numberCourse < ConstantEntity.ONE && color < ConstantEntity.ZERO);
    }
    @Override
    public boolean newEntity() {
        if (hasEntity()){
            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);
            return true;
        }
        return false;
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
    public int getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }
    public Subject setName(@NotNull String name) {
        // TODO setName - сделать проверку
        this.name = name;
        return this;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public int size() {
        return specialityCountHourMap.size();
    }

    public boolean isEmpty() {
        return specialityCountHourMap.isEmpty();
    }

    public boolean containsKey(@NotNull Object key) {
        return specialityCountHourMap.containsKey(key);
    }
    public boolean containsValue(@NotNull Object value) {
        return specialityCountHourMap.containsValue(value);
    }

    @Nullable
    public Integer get(@NotNull Object key) {
        return specialityCountHourMap.get(key);
    }

    @Nullable
    public Integer put(@NotNull Speciality key, @NotNull Integer value) {
        specialityList.add(key);
        countHourList.add(value);
        return specialityCountHourMap.put(key, value);
    }

    @Nullable
    public Integer remove(@NotNull Object key) {
        int result = specialityList.indexOf(key);
        specialityList.remove(result);
        countHourList.remove(result);
        return specialityCountHourMap.remove(key);
    }

    public void putAll(@NotNull Map<? extends Speciality, ? extends Integer> m) {
        this.specialityList.addAll(specialityCountHourMap.keySet());
        this.countHourList.addAll(specialityCountHourMap.values());
        specialityCountHourMap.putAll(m);
    }

    public void clear() {
        specialityList.clear();
        countHourList.clear();
        specialityCountHourMap.clear();
    }

    @NotNull
    public RealmList<Speciality> getSpecialityList() {
        return specialityList;
    }
    @Nullable
    public Set<Speciality> keySet() {
        return specialityCountHourMap.keySet();
    }

    @NotNull
    public RealmList<Integer> getCountHourList() {
        return countHourList;
    }
    @Nullable
    public Collection<Integer> values() {
        return specialityCountHourMap.values();
    }

    @NotNull
    public Map<Speciality, Integer> getSpecialityCountHourMap() {
        initMap();
        return specialityCountHourMap;
    }
    @Nullable
    public Set<Map.Entry<Speciality, Integer>> entrySet() {
        return specialityCountHourMap.entrySet();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public int getNumberCourse() {
        return numberCourse;
    }
    public Subject setNumberCourse(int numberCourse) {
        // TODO setCourse
        this.numberCourse = numberCourse;
        return this;
    }

    public int getColor() {
        return color;
    }
    public Subject setColor(int color) {
        // TODO setColor - проверку
        this.color = color;
        return this;
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        Subject subject = (Subject)obj;
        return this.name.equals(subject.name);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeList(specialityList);
        dest.writeList(countHourList);
        dest.writeInt(numberCourse);
        dest.writeInt(color);
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", specialityList=" + specialityList.toString() +
                ", countHourList=" + countHourList.toString() +
                ", numberCourse=" + numberCourse +
                ", color=" + color +
                '}';
    }
}
