package com.hpcc.kursovaya.dao.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Subject extends RealmObject implements Entity<Subject>, Parcelable {
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
        putAllSpecialityCountHourMap(specialityCountHourMap);
        setNumberCourse(numberCourse);
        setColor(color);
    }
    protected Subject(Parcel in) {
        id = in.readInt();
        name = in.readString();
        in.readList(specialityList, Speciality.class.getClassLoader());
        in.readList(countHourList, Integer.class.getClassLoader());
        initMap();
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
    public boolean isEntity() {
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
    public Subject newEntity() throws Exception {
        if (isEntity()){
            try {
                setName(name);
                setNumberCourse(numberCourse);
                setSpecialityList(specialityList);
                setCountHourList(countHourList);
                setColor(color);

                int maxID = DBManager.findMaxID(this.getClass());
                setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);

            }catch (RuntimeException ex){
                String error = "Failed -> " + ex.getMessage();
                throw new Exception(error,ex);
            }
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

    public int sizeSpecialtyCountHourMap() {
        return specialityCountHourMap.size();
    }

    public boolean containsKeySpecialityCountHourMap(@NotNull Object key) {
        return specialityCountHourMap.containsKey(key);
    }
    public boolean containsValue(@NotNull Object value) {
        return specialityCountHourMap.containsValue(value);
    }

    @Nullable
    public Integer getSpecialityCountHourMap(@NotNull Object key) {
        return specialityCountHourMap.get(key);
    }

    private Subject setSpecialityList(@NotNull RealmList<Speciality> specialityList) {
        if(specialityList.isEmpty()){
            Log.e(TAG, "Failed -> specialityList is empty");
            throw new RuntimeException("specialityList is empty");
        }else{
            for (Speciality speciality:specialityList){
                if(!speciality.isEntity()){
                    Log.e(TAG, "Failed -> " + speciality.toString());
                    throw new RuntimeException(speciality.toString());
                }
            }
        }

        this.specialityList = specialityList;
        return this;
    }
    private Subject setCountHourList(@NotNull RealmList<Integer> countHourList) {
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

        this.countHourList = countHourList;
        return this;
    }
    @Nullable
    public Integer putSpecialityCountHourMap(@NotNull Speciality key, @NotNull Integer value) {
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
            int index = specialityList.indexOf(key);
            countHourList.set(index, value);
        }
        return specialityCountHourMap.put(key, value);
    }

    @Nullable
    public Integer removeSpecialityCountHourMap(@NotNull Object key) {
        int index = specialityList.indexOf(key);
        specialityList.remove(index);
        countHourList.remove(index);
        return specialityCountHourMap.remove(key);
    }

    public Subject putAllSpecialityCountHourMap(@NotNull Map<Speciality, Integer> map) {
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

    public Subject clear() {
        specialityList.clear();
        countHourList.clear();
        specialityCountHourMap.clear();
        return this;
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
        if(color < ConstantEntity.ZERO){
            Log.e(TAG, "Failed -> setColor(color = " + color + ")");
            throw new RuntimeException("setColor(color = " + color + ")");
        }
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
                ", specialityCountHourMap=" + specialityCountHourMap.toString() +
                ", numberCourse=" + numberCourse +
                ", color=" + color +
                '}';
    }
}
