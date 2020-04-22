package com.hpcc.kursovaya.dao.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.query.DBManager;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateScheduleWeek;

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
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Subject extends RealmObject implements EntityI<Subject>, Parcelable, Cloneable {
    private static final String TAG = Subject.class.getSimpleName();

    protected static Pair<RealmList<Integer>, RealmList<Integer>> convert(Map<Speciality, Integer> map) {
        Pair<RealmList<Integer>, RealmList<Integer>> pairResult = Pair.create(new RealmList<>(), new RealmList<>());
        for (Map.Entry<Speciality, Integer> entry : map.entrySet()) {
            pairResult.first.add(entry.getKey().getId());
            pairResult.second.add(entry.getValue());
        }
        return pairResult;
    }
    protected static Map<Speciality, Integer> convert(Pair<RealmList<Integer>, RealmList<Integer>> pair) {
        Map<Speciality, Integer> mapResult = new LinkedHashMap<>();
        for (int i = 0; i < pair.first.size() && i < pair.second.size(); i++){
            mapResult.put(DBManager.read(Speciality.class, ConstantApplication.ID, pair.first.get(i)), pair.second.get(i));
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
        setName(name);
        setSpecialityCountHourMap(specialityCountHourMap);
        setNumberCourse(numberCourse);
        setColor(color);
    }

    public Subject initMap(){
        specialityCountHourMap = convert(new Pair<>(idSpecialityList, countHourList));
        return this;
    }

    private void setId(int id){
        if (id < ConstantApplication.ONE){
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
        if(key.getId() < ConstantApplication.ONE){
            Log.e(TAG, "Failed -> " + key.toString());
            throw new RuntimeException(key.toString());
        }

        if(value < ConstantApplication.ONE){
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
                if(entry.getKey().getId() < ConstantApplication.ONE || entry.getValue() < ConstantApplication.ONE){
                    Log.e(TAG, "Failed -> " + entry.getKey().toString() + ", countHour = " + entry.getValue());
                    throw new RuntimeException(entry.getKey().toString() + ", countHour = " + entry.getValue());
                }
            }
        }

        specialityCountHourMap.putAll(map);
        Pair<RealmList<Integer>, RealmList<Integer>> pair = convert(map);

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
            specialityList.add(DBManager.read(Speciality.class, ConstantApplication.ID, idSpeciality));
        }
        return specialityList;
    }
    private Subject setSpecialityList(@NotNull RealmList<Integer> idSpecialityList) {
        if(idSpecialityList.isEmpty()){
            Log.e(TAG, "Failed -> specialityList is empty");
            throw new RuntimeException("specialityList is empty");
        } else {
            for (int id: idSpecialityList){
                if(id < ConstantApplication.ONE){
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
                if(countHour < ConstantApplication.ONE){
                    Log.e(TAG, "Failed -> countHour = " + countHour);
                    throw new RuntimeException("countHour = " + countHour);
                }
            }
        }

        this.countHourList = countHourList;
        return this;
    }

    @NotNull
    public Map<Speciality, Integer> getSpecialityCountHourMap() {
        return specialityCountHourMap;
    }
    public Subject setSpecialityCountHourMap(@NotNull Map<Speciality, Integer> specialityCountHourMap) {
        this.specialityCountHourMap = specialityCountHourMap;
        Pair<RealmList<Integer>, RealmList<Integer>> pair = convert(this.specialityCountHourMap);
        this.idSpecialityList = pair.first;
        this.countHourList = pair.second;
        return this;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public int getNumberCourse() {
        return numberCourse;
    }
    public Subject setNumberCourse(int numberCourse) {
        if(numberCourse < ConstantApplication.ONE){
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        Subject subject = (Subject) obj;

        if (this.initMap().sizeSpecialtyCountHour() != subject.initMap().sizeSpecialtyCountHour()){
            return false;
        }

        for (Map.Entry<Speciality, Integer> entry : subject.getSpecialityCountHourMap().entrySet()){
            if (!this.containsKeySpecialityCountHour(entry.getKey())){
                return false;
            }
        }

        return numberCourse == subject.numberCourse &&
                color == subject.color &&
                name.equals(subject.name);
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

    // Specific query
    public List<Group> toGroupList(Integer countCourse, List<Speciality> specialityList){
        // TODO Реалтзовать readAll что бы можно было сортиорвать по множеству занчений
        List<Group> result = new ArrayList<>();

        if (countCourse != null && specialityList == null){
            result = DBManager.readAll(Group.class,
                    ConstantApplication.NUMBER_COURSE, this.getNumberCourse(),
                    ConstantApplication.NUMBER_COURSE);
            return result;
        }

        result = DBManager.copyObjectFromRealm(DBManager.readAll(Group.class,
                ConstantApplication.NUMBER_COURSE, this.getNumberCourse(),
                "idSpeciality"));

        for (int i = 0; i < result.size(); i++){
            if (!specialityList.contains(result.get(i).getSpecialty())){
                result.remove(i);
            }
        }

        return result;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // EntityI
    private static int countObj = 0;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean existsEntity() {
        RealmResults<Speciality> existingEntities =
                DBManager.readAll(Speciality.class, ConstantApplication.NAME, this.getName());
        for (Speciality entity : existingEntities) {
            if (this.equals(entity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEntity() {
        return id > ConstantApplication.ZERO;
    }
    @Override
    public void checkEntity() throws Exception {
        try {
            setName(name);
            setSpecialityCountHourMap(initMap().specialityCountHourMap);
            setNumberCourse(numberCourse);
            setColor(color);
        } catch(RuntimeException ex) {
            throw new Exception("Entity = ", ex);
        }
    }
    @Override
    public Subject createEntity() throws Exception {
        if (!isEntity()){
            checkEntity();
            int maxID = DBManager.findMaxID(this.getClass());
            setId((maxID > ConstantApplication.ZERO)? ++maxID : ++countObj);
        }
        return this;
    }

    public static List<String> entityToNameList(List<Subject> entityList) {
        List<String> result = new ArrayList<>();

        for (Subject subject : entityList){
            result.add(subject.getName());
        }
        return result;
    }
    @Override
    public List<String> entityToNameList() {
        return entityToNameList(DBManager.readAll(Subject.class, this.getName()));
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

    public void deleteAllLinks() {
        //удаление полупар из расписания, которые имеют такой же предмет
        List<AcademicHour> academicHourList = DBManager.copyObjectFromRealm(DBManager.readAll(AcademicHour.class));
        for(AcademicHour academicHour : academicHourList){
            if(academicHour.getTemplateAcademicHour()!=null){
                TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                if(templateAcademicHour.getSubject()!=null){
                    Subject subject = templateAcademicHour.getSubject();
                    if(subject!=null && subject.getId()==getId()){
                        DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID, templateAcademicHour.getId());
                        DBManager.delete(AcademicHour.class, ConstantApplication.ID, academicHour.getId());
                    }
                }
            }
        }
        //удаление шаблонов
        List<TemplateScheduleWeek> templateScheduleWeeks = DBManager.copyObjectFromRealm(DBManager.readAll(TemplateScheduleWeek.class));
        for(TemplateScheduleWeek templateScheduleWeek : templateScheduleWeeks){
            List<TemplateAcademicHour> templateAcademicHourList = templateScheduleWeek.getTemplateAcademicHourList();
            if(templateAcademicHourList!=null) {
                for (TemplateAcademicHour templateAcademicHour : templateAcademicHourList) {
                    Subject subject = templateAcademicHour.getSubject();
                    if (subject != null && subject.getId()==getId()) {
                        templateScheduleWeek.deleteTemplateAcademicHour(templateAcademicHour.getId());
                    }
                }
            }
        }

        // Удаление дисциплины
        DBManager.delete(Subject.class, ConstantApplication.ID, getId());


    }
}