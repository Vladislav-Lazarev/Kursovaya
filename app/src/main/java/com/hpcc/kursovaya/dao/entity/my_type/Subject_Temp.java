package com.hpcc.kursovaya.dao.entity.my_type;

public class Subject_Temp{

}
/*
package com.hpcc.kursovaya.dao.entity.my_type;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.hpcc.kursovaya.dao.entity.Entity;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Subject_Temp extends RealmObject implements Entity, Parcelable {
    private static final String TAG = "Subject_Temp";
    private static int countObj;

    static {
        countObj = 0;
    }

    @PrimaryKey
    private int id;// Индентификатор
    private String name;// Название дисциплины
    private RealmList<PairSpecialityCountHour> pairSpecHourList;
    private int numberCourse;// Номер курса
    private int color;// Цвет дисциплины

    public Subject_Temp() {
        id = 0;
        name = "";
        pairSpecHourList = new RealmList<>();
        numberCourse = 0;
        color = 0;
    }
    public Subject_Temp(@NotNull String name, @NotNull RealmList<PairSpecialityCountHour> pairSpecHourList, int numberCourse, int color) {
        this();

        setName(name);
        setPairSpecHourList(pairSpecHourList);
        setNumberCourse(numberCourse);
        setColor(color);

        newEntity();
    }
    protected Subject_Temp(Parcel in) {
        id = in.readInt();
        name = in.readString();
        in.readList(pairSpecHourList, PairSpecialityCountHour.class.getClassLoader());
        numberCourse = in.readInt();
        color = in.readInt();
    }

    public static final Creator<Subject_Temp> CREATOR = new Creator<Subject_Temp>() {
        @Override
        public Subject_Temp createFromParcel(Parcel in) {
            return new Subject_Temp(in);
        }

        @Override
        public Subject_Temp[] newArray(int size) {
            return new Subject_Temp[size];
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
    public Subject_Temp setName(@NotNull String name) {
        // TODO setName - сделать проверку
        this.name = name;
        return this;
    }

    @NotNull
    public RealmList<PairSpecialityCountHour> getPairSpecHourList() {
        return pairSpecHourList;
    }
    public Subject_Temp setPairSpecHourList(@NotNull RealmList<PairSpecialityCountHour> pairSpecHourList) {
        // TODO setPairSpecialityCountHoursList
        this.pairSpecHourList = pairSpecHourList;
        return this;
    }

    public int getNumberCourse() {
        return numberCourse;
    }
    public Subject_Temp setNumberCourse(int numberCourse) {
        // TODO setCourse
        this.numberCourse = numberCourse;
        return this;
    }

    public int getColor() {
        return color;
    }
    public Subject_Temp setColor(int color) {
        // TODO setColor - проверку
        this.color = color;
        return this;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public int pairListSize() {
        return pairSpecHourList.size();
    }
    public boolean isPairListEmpty() {
        return pairSpecHourList.isEmpty();
    }

    public boolean containsPairListKey(@NotNull Speciality key) {
        try {
            if (key.getId() < ConstantEntity.ONE){
                throw new Exception("Exception! containsPairListKey()");
            }

            for(PairSpecialityCountHour pair : pairSpecHourList){
                if(key.equals(pair.getSpeciality())){
                    return true;
                }
            }
        } catch(Exception ex) {
            Log.e(TAG, "catch: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }
    public boolean containsPairListValue(int value) {
        for(PairSpecialityCountHour pair : pairSpecHourList){
            if(pair.getCountHour() == value){
                return true;
            }
        }

        return false;
    }

    public int pairListGet(@NotNull Speciality key) {
        // TODO pairListGet как в containsPairListKey
        for(PairSpecialityCountHour pair : pairSpecHourList){
            if(key.equals(pair.getSpeciality())){
                return pair.getCountHour();
            }
        }
        return ConstantEntity.ZERO;
    }

    public int pairListPut(PairSpecialityCountHour pair) {
        // TODO pairListPut

        try {
            if (!containsPairListKey(pair.getSpeciality())){
                pairSpecHourList.add(pair);
            }
            else {
                int index = pairSpecHourList.indexOf(pair);
                pairSpecHourList.get(index).set(pair);
            }
            return pair.getCountHour();
        } catch (Exception ex){

        }
        return ConstantEntity.ZERO;
    }

    public int pairListRemove(@NotNull Speciality key) {
        for (PairSpecialityCountHour pair : pairSpecHourList){
            if (key.equals(pair.getSpeciality())){
                pairSpecHourList.remove(pair);
                return pair.getCountHour();
            }
        }
        return ConstantEntity.ZERO;
    }

    public void pairListClear() {
        pairSpecHourList.clear();
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public boolean equals(@NotNull Object obj) {
        Subject_Temp subject = (Subject_Temp)obj;
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
        dest.writeList(pairSpecHourList);
        dest.writeInt(numberCourse);
        dest.writeInt(color);
    }

    @Override
    public String toString() {
        return "Subject_Temp{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pairSpecHourList=" + pairSpecHourList.toString() +
                ", numberCourse=" + numberCourse +
                ", color=" + color +
                '}';
    }
}
*/
