package com.hpcc.kursovaya.dao.entity.setting;

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
