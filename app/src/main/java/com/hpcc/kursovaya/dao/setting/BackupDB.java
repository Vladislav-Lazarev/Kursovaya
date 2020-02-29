package com.hpcc.kursovaya.dao.setting;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BackupDB extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private String path;
    private Date date;

    {
        id = 0;
        name = "";
        path = "";
        date = new Date(0);
    }
    public BackupDB() {

    }
    public BackupDB(String name, String path, Date date) {
        setName(name);
        setPath(path);
        setDate(date);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public BackupDB setName(String name) {
        this.name = name;
        return this;
    }

    public String getPath() {
        return path;
    }
    public BackupDB setPath(String path) {
        this.path = path;
        return this;
    }

    public Date getDate() {
        return date;
    }
    public BackupDB setDate(Date date) {
        this.date = date;
        return this;
    }

    @Override
    public String toString() {
        return "BackupDB{" +
                "id=" + id +
                ", name=" + name +
                ", path=" + path +
                ", date=" + date +
                '}';
    }
}
