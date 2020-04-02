package com.hpcc.kursovaya.dao.entity.setting;

import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class BackupDB {
    private String name;
    private String fileName;
    private Date dateCreate;

    public BackupDB() {
        fileName = "";
        dateCreate = new Date();
    }
    public BackupDB(@NotNull String fileName, @NotNull Date dateCreate) {
       setFileName(fileName);
       setDateCreate(dateCreate);
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

    public String getFileName() {
        return fileName;
    }
    public BackupDB setFileName(@NotNull String fileName) {
        // TODO setFileName - проверка
        this.fileName = fileName;
        return this;
    }

    public Date getDateCreate() {
        return dateCreate;
    }
    public BackupDB setDateCreate(@NotNull Date dateCreate) {
        // TODO setDateCreate - проверка
        this.dateCreate = dateCreate;
        return this;
    }

    @Override
    public String toString() {
        return "BackupDB{" +
                " fileName = " + fileName +
                ", dateCreate = " + dateCreate +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
