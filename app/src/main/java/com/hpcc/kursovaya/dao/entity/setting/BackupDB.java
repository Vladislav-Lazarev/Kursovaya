package com.hpcc.kursovaya.dao.entity.setting;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.hpcc.kursovaya.dao.constant.ConstantApplication;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class BackupDB {
    private File backupFile;

    public BackupDB(@NotNull String location, @NotNull String fileName) {
       this(location + ConstantApplication.DIR_DELIMITER + fileName);
    }

    public BackupDB(@NotNull String pathName){
        this.backupFile = new File(pathName);
    }

    public boolean createFile(){
        if(!backupFile.exists()) {
            try {
                return backupFile.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    public @NotNull String getLocation(){
        return Objects.requireNonNull(backupFile.getParent());
    }

    public BackupDB setFileName(@NotNull String fileName){
        if(backupFile.exists()){
            File file = new File(backupFile.getParent() + ConstantApplication.DIR_DELIMITER + fileName);
            backupFile.renameTo(file);
            backupFile = file;
            return this;
        }

        return null;
    }
    public @NotNull String getFileName() {
        return backupFile.getName();
    }

    public static @NotNull String getInvalidCharsBackupDB(){
        return FileManager.getInvalidCharsFileName() + ".";
    }

    public @NotNull boolean setContent(@NotNull Uri sourceUri, ContentResolver contentResolver){
        if(backupFile.exists()){
           backupFile.delete();
        }

        try {
            backupFile.createNewFile();
        } catch (IOException e) {

        }

        FileManager.copy(sourceUri, backupFile, contentResolver);

        return true;
    }


    public @NotNull Date getDateCreate() { return new Date(backupFile.lastModified()); }
    @SuppressLint("SimpleDateFormat")
    public @NotNull String getDateCreateToString(@NotNull String formatDate){
        return new SimpleDateFormat(formatDate).format(new Date(backupFile.lastModified()));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      if(obj == null){
          return false;
      }

      BackupDB backupDB = (BackupDB) obj;
      return this.backupFile.getPath().equals(backupDB.backupFile.getPath());
    }

    @Override
    public String toString() {
        return "BackupDB{" +
                " fileName = " + backupFile.getName() +
                ", dateCreate = " + getDateCreateToString("dd-MM-yyyy") +
                '}';
    }
}
