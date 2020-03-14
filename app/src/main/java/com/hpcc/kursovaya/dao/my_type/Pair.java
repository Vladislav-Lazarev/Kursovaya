package com.hpcc.kursovaya.dao.my_type;

import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// PairSubjectWithListGroup
public class Pair extends RealmObject {
    @PrimaryKey
    private int id;
    private Subject subject;
    private RealmList<Group> groupList;

    public Pair() {
        id = 0;
        subject = new Subject();
        groupList = new RealmList<>();
    }
    public Pair(int id, @NotNull Subject subject, @NotNull RealmList<Group> groupList) {
        this();
        setId(id);
        set(subject, groupList);
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
    public Subject getSubject() {
        return subject;
    }
    private Pair setSubject(@NotNull Subject subject) {
        this.subject = subject;
        return this;
    }

    @NotNull
    public RealmList<Group> getGroupList() {
        return groupList;
    }
    private Pair setGroupList(@NotNull RealmList<Group> groupList) {
        // TODO setGroupList - проверка
        this.groupList = groupList;
        return this;
    }

    public Pair set(@NotNull Subject subject, @NotNull RealmList<Group> groupList){
        setSubject(subject);
        setGroupList(groupList);
        return this;
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        Pair pair = (Pair)obj;
        return this.id == pair.id && this.subject.equals(pair.subject);
    }

    @Override
    public String toString() {
        return "PairSubjectListGroup{" +
                "id=" + id +
                ", subject=" + subject +
                ", groupList=" + groupList.toString() +
                '}';
    }
}