package com.hpcc.kursovaya.dao.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Subject extends RealmObject implements EntityI, Parcelable, Cloneable {
    private static final String TAG = Subject.class.getSimpleName();

    protected static Pair<RealmList<Integer>, RealmList<Integer>> convert(Pair<RealmList<Integer>, RealmList<Integer>> pairResult,
                                                                           Map<Speciality, Integer> map) {
        int i = 0;
        pairResult.first.clear();
        pairResult.second.clear();
        for (Map.Entry<Speciality, Integer> entry : map.entrySet()) {
            pairResult.first.add(i, entry.getKey().getId());
            pairResult.second.add(i, entry.getValue());
            i++;
        }
        return pairResult;
    }
    protected static Map<Speciality, Integer> convert(Map<Speciality, Integer> mapResult,
                                                      Pair<RealmList<Integer>, RealmList<Integer>> pair) {
        mapResult.clear();
        for (int i = 0; i < pair.first.size() && i < pair.second.size(); i++){
            mapResult.put(DBManager.read(Speciality.class, ConstantEntity.ID, pair.first.get(i)), pair.second.get(i));
        }
        return mapResult;
    }

    @PrimaryKey
    private int id;// ID subject
    @Required
    private String name;// Name subject
    private RealmList<Integer> idSpecialityList;// Speciality ID List
    private RealmList<Integer> countHourList;// Load (hours) for speciality
    @Ignore
    private Map<Speciality, Integer> specialityCountHourMap;// Auxiliary field for Speciality ID List and Load (hours) for speciality
    private int numberCourse;// Number course
    private int color;// Color subject

    {
        id = 0;
        name = "";
        idSpecialityList = new RealmList<>();
        countHourList = new RealmList<>();
        specialityCountHourMap = new LinkedHashMap<>();
        numberCourse = 0;
        color = 0;
    }
    public Subject() {

    }
    public Subject(@NotNull String name,@NotNull Map<Speciality, Integer> specialityCountHourMap, int numberCourse, int color) {
        this();

        setName(name);
        setSpecialityCountHourMap(specialityCountHourMap);
        setNumberCourse(numberCourse);
        setColor(color);
    }

    public Subject initMap(){
        specialityCountHourMap = convert(specialityCountHourMap, new Pair<>(idSpecialityList, countHourList));
        return this;
    }

    public int getId() {
        return id;
    }
    private void setId(int id){
        if (id < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> setId(id = " + id + ")");
            throw new RuntimeException("setId(id = "+ id + ")");
        }
        this.id = id;
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

    public int sizeSpecialtyCountHour() {
        return specialityCountHourMap.size();
    }
    public boolean isEmptySpecialtyCountHour() {
        return specialityCountHourMap.isEmpty();
    }

    public boolean containsKeySpecialityCountHour(@NotNull Speciality key) {
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
        if(key.getId() < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> " + key.toString());
            throw new RuntimeException(key.toString());
        }

        if(value < ConstantEntity.ONE){
            Log.e(TAG, "Failed -> countHour = " + value);
            throw new RuntimeException("countHour = " + value);
        }

        if (!idSpecialityList.contains(key.getId())) {
            idSpecialityList.add(key.getId());
            countHourList.add(value);
        } else {
            int index = idSpecialityList.indexOf(key.getId());
            countHourList.set(index, value);
        }
        return specialityCountHourMap.put(key, value);
    }
    @Nullable
    public Integer removeSpecialityCountHour(@NotNull Speciality key) {
        int index = idSpecialityList.indexOf(key.getId());
        idSpecialityList.remove(index);
        countHourList.remove(index);
        return specialityCountHourMap.remove(key);
    }

    public Subject putAllSpecialityCountHour(@NotNull Map<Speciality, Integer> map) {
        if(map.isEmpty()){
            Log.e(TAG, "Failed -> map is empty");
            throw new RuntimeException("map is empty");
        }else{
            for(Map.Entry<Speciality, Integer> entry:map.entrySet()){
                if(entry.getKey().getId() < ConstantEntity.ONE || entry.getValue() < ConstantEntity.ONE){
                    Log.e(TAG, "Failed -> " + entry.getKey().toString() + ", countHour = " + entry.getValue());
                    throw new RuntimeException(entry.getKey().toString() + ", countHour = " + entry.getValue());
                }
            }
        }

        specialityCountHourMap.putAll(map);
        Pair<RealmList<Integer>, RealmList<Integer>> pair =
                convert(new Pair<>(idSpecialityList, countHourList), map);

        idSpecialityList.addAll(pair.first);
        countHourList.addAll(pair.second);
        return this;
    }
    public Subject clearSpecialityCountHour() {
        idSpecialityList.deleteAllFromRealm();
        countHourList.deleteAllFromRealm();
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

    @NotNull
    public RealmList<Speciality> getSpecialityList() {
        RealmList<Speciality> specialityList = new RealmList<>();
        for (int idSpeciality : idSpecialityList) {
            specialityList.add(DBManager.read(Speciality.class, ConstantEntity.ID, idSpeciality));
        }
        return specialityList;
    }
    private Subject setIdSpecialityList(@NotNull RealmList<Integer> idSpecialityList) {
        if(idSpecialityList.isEmpty()){
            Log.e(TAG, "Failed -> specialityList is empty");
            throw new RuntimeException("specialityList is empty");
        }else{
            for (int id: idSpecialityList){
                if(id < ConstantEntity.ONE){
                    Log.e(TAG, "Failed -> " + idSpecialityList.toString());
                    throw new RuntimeException(idSpecialityList.toString());
                }
            }
        }

        this.idSpecialityList = idSpecialityList;
        return this;
    }

    @NotNull
    public RealmList<Integer> getCountHourList() {
        return countHourList;
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

        this.countHourList =countHourList;
        return this;
    }

    @NotNull
    public Map<Speciality, Integer> getSpecialityCountHourMap() {
        return specialityCountHourMap;
    }
    public Subject setSpecialityCountHourMap(@NotNull Map<Speciality, Integer> specialityCountHourMap) {
        this.specialityCountHourMap = specialityCountHourMap;
        Pair<RealmList<Integer>, RealmList<Integer>> pair =
                convert(new Pair<>(idSpecialityList, countHourList), this.specialityCountHourMap);
        this.idSpecialityList = pair.first;
        this.countHourList = pair.second;
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
                idSpecialityList.equals(subject.idSpecialityList) &&
                countHourList.equals(subject.countHourList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, idSpecialityList, countHourList, numberCourse, color);
    }
    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", idSpecialityList=" + idSpecialityList.toString() +
                ", countHourList=" + countHourList.toString() +
                ", specialityCountHourMap=" + specialityCountHourMap.toString() +
                ", numberCourse=" + numberCourse +
                ", color=" + color +
                '}';
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // EntityI<Group>
    private static int countObj = 0;
    @Override
    public boolean createEntity() {
        if (id < ConstantEntity.ONE){
            try {
                setName(name);
                setSpecialityCountHourMap(initMap().specialityCountHourMap);
                setNumberCourse(numberCourse);
                setColor(color);
            } catch (RuntimeException ex) {
                return false;
            }

            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantEntity.ZERO)? ++maxID : ++countObj);
        }
        return true;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Parcelable
    protected Subject(Parcel in) {
        id = in.readInt();
        name = in.readString();
        in.readList(idSpecialityList, Integer.class.getClassLoader());
        in.readList(countHourList, Integer.class.getClassLoader());
        numberCourse = in.readInt();
        color = in.readInt();
        initMap();
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
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeList(idSpecialityList);
        dest.writeList(countHourList);
        dest.writeInt(numberCourse);
        dest.writeInt(color);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Cloneable
    @NonNull
    @Override
    public Subject clone() throws CloneNotSupportedException {
        return (Subject) super.clone();
    }
}