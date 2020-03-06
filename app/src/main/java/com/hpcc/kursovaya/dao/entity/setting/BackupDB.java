package com.hpcc.kursovaya.dao.entity.setting;

import com.hpcc.kursovaya.dao.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BackupDB extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private String path;
    private Date date;

    public BackupDB() {
        id = 0;
        name = "";
        path = "";
        date = new Date();
    }
    public BackupDB(int id, @NotNull String name, @NotNull String path, @NotNull Date date) {
        this();
        setId(id);
        setName(name);
        setPath(path);
        setDate(date);
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

    public String getName() {
        return name;
    }
    public BackupDB setName(@NotNull String name) {
        // TODO setName - проверка
        this.name = name;
        return this;
    }

    public String getPath() {
        return path;
    }
    public BackupDB setPath(@NotNull String path) {
        // TODO setPath - проверка
        this.path = path;
        return this;
    }

    public Date getDate() {
        return date;
    }
    public BackupDB setDate(@NotNull Date date) {
        // TODO setRealmDateTime - проверка
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
