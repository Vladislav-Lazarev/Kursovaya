package com.hpcc.kursovaya.dao.entity;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.my_type.pair_subject_list_group.Pair;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Course extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private int number;// Номер курса
    private RealmList<Pair> pairList;

    public Course() {
        id = 0;
        number = 0;
        pairList = new RealmList<>();
    }
    public Course(int id, int number, @NotNull RealmList<Pair> pairList) {
        this();
        setId(id);
        setNumber(number);
        setPairList(pairList);
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

    public int getNumber() {
        return number;
    }
    public Course setNumber(int number) {
        try {
            if (number < ConstantEntity.MIN_COUNT_COURSE  || number > ConstantEntity.MAX_COUNT_COURSE){
                throw new Exception("Exception! setNumber()");
            }
            this.number = number;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    @NotNull
    public RealmList<Pair> getPairList() {
        return pairList;
    }
    public void setPairList(@NotNull RealmList<Pair> pairList) {
        // TODO setPairSubjectListGroupList - сделать проверку
        this.pairList = pairList;
    }

    public int pairListSize() {
        return pairList.size();
    }
    public boolean isPairListEmpty() {
        return pairList.isEmpty();
    }

    public boolean containsPairListKey(Subject key) {
        for(Pair pair:pairList){
            if(pair.getSubject().equals(key)){
                return true;
            }
        }

        return false;
    }
    public boolean containsPairListValue(RealmList<Group> value) {
        for(Pair pair:pairList){
            boolean isSizeEqual = pair.getGroupList().size() == value.size();
            for (int i = 0; i < pair.getGroupList().size() && isSizeEqual; i++) {
                if(!pair.getGroupList().get(i).equals(value.get(i))){
                    return false;
                }
            }
        }

        return true;
    }

    public RealmList<Group> pairListGet( Subject key) {
        for(Pair pair:pairList){
            if(pair.getSubject().equals(key)){
                return pair.getGroupList();
            }
        }

        return null;
    }

    public RealmList<Group> pairListPut( Pair pair) {
        if (!containsPairListKey(pair.getSubject())){
            pairList.add(pair);
        }
        else {
            int index = pairList.indexOf(pair);
            pairList.get(index).set(pair.getSubject(), pair.getGroupList());
        }
        return pairListGet(pair.getSubject());
    }

    public RealmList<Group> pairListRemove( Subject key) {
        for (Pair pair : pairList){
            if (key.equals(pair.getSubject())){
                pairList.remove(pair);
                return pair.getGroupList();
            }
        }
        return null;
    }

    public void pairListClear() {
        pairList.clear();
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", number=" + number +
                ", pairSubjectListGroupList=" + pairList.toString() +
                '}';
    }
}