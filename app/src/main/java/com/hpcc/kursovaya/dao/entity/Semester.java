package com.hpcc.kursovaya.dao.entity;

import com.hpcc.kursovaya.dao.ConstantEntity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Semester extends RealmObject {
    @PrimaryKey
    private int id;// Индентификатор
    private int number;// Номер семестра
    private RealmList<Discipline> disciplineList;// Список дисциплин проходящие в этом семестре
    private RealmList<Group> groupList;// Список групп проходящие(учащие) в этом семестре

    {
        id = 0;
        number = 0;
        disciplineList = new RealmList<>();
        groupList = new RealmList<>();
    }
    public Semester() {

    }
    public Semester(int number, RealmList<Discipline> disciplineList, RealmList<Group> groupList) {
        setNumber(number);
        setDisciplineList(disciplineList);
        setGroupList(groupList);
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }
    public Semester setNumber(int number) {
        try {
            if (ConstantEntity.MIN_COUNT_SEMESTER < number && number > ConstantEntity.MAX_COUNT_SEMESTER){
                throw new Exception("Exception! setNumber()");
            }
            this.number = number;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    public int getNumberCourse(){
        int result = number / ConstantEntity.TWO;
        if (result == 0 || number % ConstantEntity.TWO != 0){
            ++result;
        }
        return result;
    }
    public Semester setNumberCourse(int number){
        try {
            if (ConstantEntity.MIN_COUNT_COURSE < number && number > ConstantEntity.MAX_COUNT_COURSE){
                throw new Exception("Exception! setNumberCourse()");
            }
            this.number = number * ConstantEntity.TWO;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    public RealmList<Discipline> getDisciplineList() {
        return disciplineList;
    }
    public Semester setDisciplineList(RealmList<Discipline> disciplineList) {
        try {
            if (disciplineList.size() < ConstantEntity.ONE) {
                throw new Exception("Exception! setDisciplineList()");
            }
            this.disciplineList = disciplineList;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    public RealmList<Group> getGroupList() {
        return groupList;
    }
    public Semester setGroupList(RealmList<Group> groupList) {
        try {
            if (groupList.size() < ConstantEntity.ONE) {
                throw new Exception("Exception! course()");
            }
            this.groupList = groupList;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return "Semester{" +
                "id=" + id +
                ", number=" + number +
                ", disciplineList=" + disciplineList +
                ", groupList=" + groupList +
                '}';
    }
}
