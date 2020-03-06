package com.hpcc.kursovaya.dao.entity.setting;

import com.hpcc.kursovaya.dao.ConstantEntity;
import com.hpcc.kursovaya.dao.my_type.date_time.maks.RealmDateTime;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BackupDB extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private String path;
    private RealmDateTime dateTime;

    {
        id = 0;
        name = "";
        path = "";
        dateTime = new RealmDateTime();
    }
    public BackupDB() {

    }
    public BackupDB(int id, @NotNull String name, @NotNull String path, @NotNull RealmDateTime dateTime) {
        setId(id);
        setName(name);
        setPath(path);
        setRealmDateTime(dateTime);
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

    public RealmDateTime getRealmDateTime() {
        return dateTime;
    }
    public BackupDB setRealmDateTime(@NotNull RealmDateTime dateTime) {
        // TODO setRealmDateTime - проверка
        this.dateTime = dateTime;
        return this;
    }

    @Override
    public String toString() {
        return "BackupDB{" +
                "id=" + id +
                ", name=" + name +
                ", path=" + path +
                ", dateTime=" + dateTime +
                '}';
    }
}
