package com.hpcc.kursovaya.dao.entity;

import com.hpcc.kursovaya.dao.ConstantEntity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Course extends RealmObject {
    // TODO Course
    @PrimaryKey
    private int id;// Индентификатор
    private int number;// Номер курса
    // TODO должно быть так Map<List<Subject>, List<Group>>
    private RealmList<Subject> subjectList;// Список дисциплин проходящие в этом семестре
    private RealmList<Group> groupList;// Список групп проходящие(учащие) в этом семестре

    {
        id = 0;
        number = 0;
        subjectList = new RealmList<>();
        groupList = new RealmList<>();
    }
    public Course() {

    }
    public Course(int number, RealmList<Subject> subjectList, RealmList<Group> groupList) {
        setNumber(number);
        setSubjectList(subjectList);
        setGroupList(groupList);
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }
    public Course setNumber(int number) {
        try {
            if (ConstantEntity.MIN_COUNT_COURSE < number && number > ConstantEntity.MAX_COUNT_COURSE){
                throw new Exception("Exception! setNumber()");
            }
            this.number = number;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    /*public int getNumberCourse(){
        int result = number / ConstantEntity.TWO;
        if (result == 0 || number % ConstantEntity.TWO != 0){
            ++result;
        }
        return result;
    }
    public Course setNumberCourse(int number){
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
    }*/

    public RealmList<Subject> getSubjectList() {
        return subjectList;
    }
    public Course setSubjectList(RealmList<Subject> subjectList) {
        try {
            if (subjectList.size() < ConstantEntity.ONE) {
                throw new Exception("Exception! setDisciplineList()");
            }
            this.subjectList = subjectList;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return this;
    }

    public RealmList<Group> getGroupList() {
        return groupList;
    }
    public Course setGroupList(RealmList<Group> groupList) {
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
                ", disciplineList=" + subjectList +
                ", groupList=" + groupList +
                '}';
    }
}
