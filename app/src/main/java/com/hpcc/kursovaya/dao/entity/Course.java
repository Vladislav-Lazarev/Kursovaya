package com.hpcc.kursovaya.dao.entity;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.my_type.PairSubjectGroup;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class Course extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private int number;// Номер курса
    private RealmList<PairSubjectGroup> pairSubjectGroupList;

    public Course() {
        id = 0;
        number = 0;
        pairSubjectGroupList = new RealmList<>();
    }
    public Course(int id, int number, @NotNull RealmList<PairSubjectGroup> pairSubjectGroupList) {
        this();
        setId(id);
        setNumber(number);
        setPairSubjectGroupList(pairSubjectGroupList);
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
    public RealmList<PairSubjectGroup> getPairSubjectGroupList() {
        return pairSubjectGroupList;
    }
    public void setPairSubjectGroupList(@NotNull RealmList<PairSubjectGroup> pairSubjectGroupList) {
        // TODO setPairSubjectListGroupList - сделать проверку
        this.pairSubjectGroupList = pairSubjectGroupList;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public int pairListSize() {
        return pairSubjectGroupList.size();
    }
    public boolean isPairListEmpty() {
        return pairSubjectGroupList.isEmpty();
    }

    public boolean containsPairListKey(Subject key) {
        for(PairSubjectGroup pairSubjectGroup : pairSubjectGroupList){
            if(pairSubjectGroup.getSubject().equals(key)){
                return true;
            }
        }

        return false;
    }

    public boolean containsPairListValue(RealmList<Group> value) {
        RealmResults<Group> sortValue = value.sort(ConstantEntity.NAME);

        for(PairSubjectGroup pairSubjectGroup : pairSubjectGroupList){
            boolean isSizeEqual = pairSubjectGroup.getGroupList().size() == value.size();
            RealmResults<Group> sort = pairSubjectGroup.getGroupList().sort(ConstantEntity.NAME);
            for (int i = 0; i < sort.size() && isSizeEqual; i++) {
                if(!pairSubjectGroup.getGroupList().get(i).equals(sortValue.get(i))){
                    return false;
                }
            }
        }

        return true;
    }

    public RealmList<Group> pairListGet( Subject key) {
        for(PairSubjectGroup pairSubjectGroup : pairSubjectGroupList){
            if(pairSubjectGroup.getSubject().equals(key)){
                return pairSubjectGroup.getGroupList();
            }
        }

        return null;
    }

    public RealmList<Group> pairListPut( PairSubjectGroup pairSubjectGroup) {
        if (!containsPairListKey(pairSubjectGroup.getSubject())){
            pairSubjectGroupList.add(pairSubjectGroup);
        }
        else {
            int index = pairSubjectGroupList.indexOf(pairSubjectGroup);
            pairSubjectGroupList.get(index).set(pairSubjectGroup.getSubject(), pairSubjectGroup.getGroupList());
        }
        return pairListGet(pairSubjectGroup.getSubject());
    }

    public RealmList<Group> pairListRemove( Subject key) {
        for (PairSubjectGroup pairSubjectGroup : pairSubjectGroupList){
            if (key.equals(pairSubjectGroup.getSubject())){
                pairSubjectGroupList.remove(pairSubjectGroup);
                return pairSubjectGroup.getGroupList();
            }
        }
        return null;
    }

    public void pairListClear() {
        pairSubjectGroupList.clear();
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", number=" + number +
                ", pairSubjectListGroupList=" + pairSubjectGroupList.toString() +
                '}';
    }
}