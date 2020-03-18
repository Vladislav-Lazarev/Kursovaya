package com.hpcc.kursovaya.dao.entity.my_type;

public class PairSpecialityCountHour{

}
/*
package com.hpcc.kursovaya.dao.entity.my_type;

import android.os.Parcel;
import android.os.Parcelable;

import com.hpcc.kursovaya.dao.entity.Entity;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PairSpecialityCountHour extends RealmObject implements Entity, Parcelable {
    private static int countObj;// Кол-во объктов

    static {
        countObj = 0;
    }

    @PrimaryKey
    private int id;// Индентификатор
    private Speciality speciality;// Ключ
    private int countHour;// Значение

    public PairSpecialityCountHour() {
        id = 0;
        speciality = new Speciality();
        countHour = 0;
    }
    public PairSpecialityCountHour(@NotNull Speciality speciality, int countHour) {
        this();

        set(speciality, countHour);

        newEntity();
    }
    public PairSpecialityCountHour(@NotNull PairSpecialityCountHour pair) {
        this(pair.getSpeciality(), pair.getCountHour());
    }
    protected PairSpecialityCountHour(Parcel in) {
        id = in.readInt();
        speciality = in.readParcelable(Speciality.class.getClassLoader());
        countHour = in.readInt();
    }

    public static final Creator<PairSpecialityCountHour> CREATOR = new Creator<PairSpecialityCountHour>() {
        @Override
        public PairSpecialityCountHour createFromParcel(Parcel in) {
            return new PairSpecialityCountHour(in);
        }

        @Override
        public PairSpecialityCountHour[] newArray(int size) {
            return new PairSpecialityCountHour[size];
        }
    };

    @Override
    public boolean hasEntity() {
        return !(speciality.getId() < ConstantEntity.ONE && countHour < ConstantEntity.ZERO);
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

    private PairSpecialityCountHour setId(int id){
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
        return this;
    }
    public int getId() {
        return id;
    }

    @NotNull
    public Speciality getSpeciality() {
        return speciality;
    }
    PairSpecialityCountHour setSpeciality(@NotNull Speciality speciality) {
        // TODO setSpeciality
        this.speciality = speciality;
        return this;
    }

    @NotNull
    public int getCountHour() {
        return countHour;
    }
    private PairSpecialityCountHour setCountHour(int countHour) {
        // TODO setCountHours - проверка
        this.countHour = countHour;
        return this;
    }

    public PairSpecialityCountHour set(@NotNull Speciality speciality, int countHours){
        setSpeciality(speciality);
        setCountHour(countHours);
        return this;
    }
    public PairSpecialityCountHour set(@NotNull PairSpecialityCountHour pair){
        set(pair.getSpeciality(), pair.getCountHour());
        return this;
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        PairSpecialityCountHour pair = (PairSpecialityCountHour)obj;
        return this.speciality.equals(pair.speciality) && this.countHour == pair.countHour;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(speciality, flags);
        dest.writeInt(countHour);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "id=" + id +
                ", speciality=" + speciality +
                ", countHours=" + countHour +
                '}';
    }
}
*/
