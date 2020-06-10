package com.hpcc.kursovaya.ui.hourChecker.model;

import com.hpcc.kursovaya.dao.entity.Group;

public class GroupModel {
    private Group group;
    private boolean isFull = false;

    public GroupModel(Group group, boolean isFull) {
        this.group = group;
        this.isFull = isFull;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

}
