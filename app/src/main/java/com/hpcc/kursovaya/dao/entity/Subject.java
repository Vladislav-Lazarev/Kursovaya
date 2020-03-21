package com.hpcc.kursovaya.dao.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Subject extends RealmObject implements EntityI<Subject>, Parcelable, Cloneable {
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

    {
        id = 0;
        name = "";
        specialityList = new RealmList<>();
        countHourList = new RealmList<>();
        specialityCountHourMap = new LinkedHashMap<>();
        numberCourse = 0;
        color = 0;
    }
    public Subject() {
        id = 0;
        name = "";
        specialityList = new RealmList<>();
        countHourList = new RealmList<>();
        specialityCountHourMap = new LinkedHashMap<>();
        numberCourse = 0;
        color = 0;
    }
    public Subject(@NotNull String name,@NotNull Map<Speciality, Integer> specialityCountHourMap, int numberCourse, int color) {
        this();

        setName(name);
        setSpecialityCountHourMap(specialityCountHourMap);
        setNumberCourse(numberCourse);
        setColor(color);
        initMap();
        newEntity();
    }
    protected Subject(Parcel in) {
        id = in.readInt();
        name = in.readString();
        in.readList(specialityList, Speciality.class.getClassLoader());
        in.readList(countHourList, Integer.class.getClassLoader());
        numberCourse = (int) in.readValue(Integer.class.getClassLoader());
        color = in.readInt();
        initMap();
    }
    public Subject initMap(){
        for (int i = 0; i < specialityList.size() && i < countHourList.size(); i++){
            specialityCountHourMap.put(specialityList.get(i), countHourList.get(i));
        }
        return this;
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
    public boolean isEntity() {
        initMap();
        boolean isMapCorrect = true;
        if(specialityCountHourMap.isEmpty()){
            isMapCorrect = false;
        }
        else {
            for (Map.Entry<Speciality, Integer> entry : specialityCountHourMap.entrySet()) {
                if (!entry.getKey().isEntity() || entry.getValue() < ConstantEntity.ONE) {
                    isMapCorrect = false;
                }
            }
        }

        return !("".equals(name) || numberCourse < ConstantEntity.ONE || color < ConstantEntity.ZERO || isMapCorrect);
    }
    @Override
    public Subject newEntity() {
        if (isEntity()){
            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);
        }
        return this;
    }

    private void setId(int id){
        if (id < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setId(id = " + id + ")");
            throw new RuntimeException("setId(id = "+ id + ")");
        }
        this.id = id;
    }
    public int getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }
    public Subject setName(@NotNull String name) {
        if("".equals(name)){
            Log.e(TAG, "Failed -> setName(name = " + name +")");
            throw new RuntimeException("setName(name = " + name +")");
        }
        this.name = name;
        return this;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public int sizeSpecialtyCountHour() {
        return specialityCountHourMap.size();
    }

    public boolean isEmptySpecialtyCountHour() {
        return specialityCountHourMap.isEmpty();
    }

    public boolean containsKeySpecialityCountHour(@NotNull Speciality key) {
        Log.d(TAG, "Speciality key) = " + key.toString());
        Log.d(TAG, "specialityCountHourMap.containsKey(key) = " + specialityCountHourMap.containsKey(key));
        return specialityCountHourMap.containsKey(key);
    }
    public boolean containsValueSpecialityCountHour(@NotNull Integer value) {
        return specialityCountHourMap.containsValue(value);
    }

    @Nullable
    public Integer getSpecialityCountHour(@NotNull Speciality key) {
        return specialityCountHourMap.get(key);
    }

    @Nullable
    public Integer putSpecialityCountHour(@NotNull Speciality key, @NotNull Integer value) {
        if(!key.isEntity()){
            Log.e(TAG, "Failed -> " + key.toString());
            throw new RuntimeException(key.toString());
        }

        if(value < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> countHour = " + value);
            throw new RuntimeException("countHour = " + value);
        }

        if (!specialityList.contains(key)) {
            specialityList.add(key);
            countHourList.add(value);
        } else {
            int index = specialityList.indexOf(key.getId());
            countHourList.set(index, value);
        }
        return specialityCountHourMap.put(key, value);
    }

    @Nullable
    public Integer removeSpecialityCountHour(@NotNull Speciality key) {
        int index = specialityList.indexOf(key);
        specialityList.remove(index);
        countHourList.remove(index);
        return specialityCountHourMap.remove(key);
    }

    public Subject putAllSpecialityCountHour(@NotNull Map<Speciality, Integer> map) {
        if(map.isEmpty()){
            Log.e(TAG, "Failed -> map is empty");
            throw new RuntimeException("map is empty");
        }else{
            for(Map.Entry<Speciality, Integer> entry:map.entrySet()){
                if(!entry.getKey().isEntity() || entry.getValue() < ConstantEntity.ONE){
                    Log.e(TAG, "Failed -> " + entry.getKey().toString() + ", countHour = " + entry.getValue());
                    throw new RuntimeException(entry.getKey().toString() + ", countHour = " + entry.getValue());
                }
            }
        }

        this.specialityList.addAll(map.keySet());
        this.countHourList.addAll(map.values());
        specialityCountHourMap.putAll(map);
        return this;
    }

    public Subject clearSpecialityCountHour() {
        specialityList.clear();
        countHourList.clear();
        specialityCountHourMap.clear();
        return this;
    }

    @Nullable
    public Set<Speciality> keySetSpecialityCountHour() {
        return specialityCountHourMap.keySet();
    }

    @Nullable
    public Collection<Integer> valuesSpecialityCountHour() {
        return specialityCountHourMap.values();
    }

    @Nullable
    public Set<Map.Entry<Speciality, Integer>> entrySetSpecialityCountHour() {
        return specialityCountHourMap.entrySet();
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @NotNull
    public RealmList<Speciality> getSpecialityList() {
        return specialityList;
    }
    private Subject setSpecialityList(@NotNull List<Speciality> specialityList) {
        if(specialityList.isEmpty()){
            Log.e(TAG, "Failed -> specialityList is empty");
            throw new RuntimeException("specialityList is empty");
        }else{
            for (Speciality speciality: specialityList){
                if(!speciality.isEntity()){
                    Log.e(TAG, "Failed -> " + speciality.toString());
                    throw new RuntimeException(speciality.toString());
                }
            }
        }
        this.specialityList.clear();
        this.specialityList.addAll(specialityList);
        return this;
    }

    @NotNull
    public RealmList<Integer> getCountHourList() {
        return countHourList;
    }
    private Subject setCountHourList(@NotNull List<Integer> countHourList) {
        if(countHourList.isEmpty()){
            Log.e(TAG, "Failed -> countHourList is empty");
            throw new RuntimeException("countHourList is empty");
        }else{
            for(Integer countHour:countHourList){
                if(countHour < ConstantEntity.ONE){
                    Log.e(TAG, "Failed -> countHour = " + countHour);
                    throw new RuntimeException("countHour = " + countHour);
                }
            }
        }
        this.countHourList.clear();
        this.countHourList.addAll(countHourList);
        return this;
    }

    @NotNull
    public Map<Speciality, Integer> getSpecialityCountHourMap() {
        return specialityCountHourMap;
    }
    public Subject setSpecialityCountHourMap(@NotNull Map<Speciality, Integer> specialityCountHourMap) {
        setSpecialityList(new ArrayList<>(specialityCountHourMap.keySet()));
        setCountHourList(new ArrayList<>(specialityCountHourMap.values()));
        this.specialityCountHourMap = specialityCountHourMap;
        return this;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public int getNumberCourse() {
        return numberCourse;
    }
    public Subject setNumberCourse(int numberCourse) {
        if(numberCourse < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setNumberCourse(numberCourse = " + numberCourse + ")");
            throw new RuntimeException("setNumberCourse(numberCourse = " + numberCourse + ")");
        }
        this.numberCourse = numberCourse;
        return this;
    }

    public int getColor() {
        return color;
    }
    public Subject setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Subject subject = (Subject) o;
        return numberCourse == subject.numberCourse &&
                color == subject.color &&
                name.equals(subject.name) &&
                specialityList.equals(subject.specialityList) &&
                countHourList.equals(subject.countHourList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, specialityList, countHourList, numberCourse, color);
    }

    @NonNull
    @Override
    public Subject clone() throws CloneNotSupportedException {
        return (Subject) super.clone();
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
                ", specialityCountHourMap=" + specialityCountHourMap.toString() +
                ", numberCourse=" + numberCourse +
                ", color=" + color +
                '}';
    }
}