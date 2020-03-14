package com.hpcc.kursovaya.dao.my_type;

import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// PairSubjectGroup
public class PairSubjectGroup extends RealmObject {
    @PrimaryKey
    private int id;
    private Subject subject;
    private RealmList<Group> groupList;

    public PairSubjectGroup() {
        id = 0;
        subject = new Subject();
        groupList = new RealmList<>();
    }
    public PairSubjectGroup(int id, @NotNull Subject subject, @NotNull RealmList<Group> groupList) {
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
    private PairSubjectGroup setSubject(@NotNull Subject subject) {
        this.subject = subject;
        return this;
    }

    @NotNull
    public RealmList<Group> getGroupList() {
        return groupList;
    }
    private PairSubjectGroup setGroupList(@NotNull RealmList<Group> groupList) {
        // TODO setGroupList - проверка
        this.groupList = groupList;
        return this;
    }

    public PairSubjectGroup set(@NotNull Subject subject, @NotNull RealmList<Group> groupList){
        setSubject(subject);
        setGroupList(groupList);
        return this;
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        PairSubjectGroup pairSubjectGroup = (PairSubjectGroup)obj;
        return this.id == pairSubjectGroup.id && this.subject.equals(pairSubjectGroup.subject);
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