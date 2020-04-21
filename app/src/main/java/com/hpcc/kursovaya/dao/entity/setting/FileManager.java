package com.hpcc.kursovaya.dao.entity.setting;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.RegEx;

import kotlin.text.Regex;

public class FileManager {

    public enum TypeFile{DICTIONARY, FILE};

    public static boolean create(@NotNull String fileName, @NotNull String location, @NotNull TypeFile type){
        File file = new File(location, fileName);
        return create(file, type);
    }
    public static boolean create(@NotNull String pathName, @NotNull TypeFile type){
        File file = new File(pathName);
        return create(file, type);
    }
    public static boolean create(@NotNull File file, @NotNull TypeFile type){
        boolean isCreate = false;

        if(!file.exists()){
            if(type == TypeFile.FILE){
                try {
                    isCreate = file.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            }else{
                isCreate = file.mkdir();
            }
        }

        return isCreate;
    }

    public static boolean copy(File sourceFile, File targetFile){
        InputStream sourceStream = null;
        OutputStream targetStream = null;
        try {
            sourceStream = new FileInputStream(sourceFile);
            targetStream = new FileOutputStream(targetFile);
            boolean isCopy = FileManager.copy(sourceStream, targetStream);
            return isCopy;
        } catch (FileNotFoundException e) {
            return false;
        }
        finally {
            try {
                sourceStream.close();
                targetStream.close();
            } catch (IOException e) {
                return false;
            }
        }
    }
    public static boolean copy(Uri sourceUri, File targetFile, ContentResolver contentResolver){
        FileManager.create(targetFile.getParent(), TypeFile.DICTIONARY);
        FileManager.create(targetFile, TypeFile.FILE);

        InputStream sourceStream = null;
        OutputStream targetStream = null;
        try {
            sourceStream = contentResolver.openInputStream(sourceUri);
            targetStream = new FileOutputStream(targetFile);
            boolean isCopy = FileManager.copy(sourceStream, targetStream);
            return isCopy;
        } catch (FileNotFoundException e) {
            return false;
        }
        finally {
            try {
                sourceStream.close();
                targetStream.close();
            } catch (IOException e) {
                return false;
            }
        }
    }
    public static boolean copy(@NotNull InputStream sourceStream, @NotNull OutputStream targetStream){
        final int sizeBuffer = 1024;
        byte[] buffer = new byte[sizeBuffer];

        try {
            int length = sourceStream.read(buffer);
            while (length > 0){
                targetStream.write(buffer, 0, length);
                length = sourceStream.read(buffer);
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static boolean remove(@NotNull String fileName, @NotNull String location){
        return remove(location + ConstantApplication.DIR_DELIMITER + fileName);
    }
    public static boolean remove(@NotNull String pathFile){
        File removeFile = new File(pathFile);

        if(removeFile.exists()){
            return removeFile.delete();
        }

        return false;
    }

    public static String getFileName(@NotNull Uri uri){
        String pathFile = uri.getLastPathSegment();
        String fileName = pathFile.substring(
                pathFile.lastIndexOf(ConstantApplication.DIR_DELIMITER)+1,
                pathFile.length()
        );

        return fileName;
    }

    public static boolean exists(@NotNull String pathName){
        File file = new File(pathName);

        return file.exists();
    }

    public static boolean isValidFileName(@NotNull String fileName, @NotNull String invalidChars){
       invalidChars = "[" + getInvalidCharsFileName() + "]";
       Pattern pattern = Pattern.compile(invalidChars);
       Matcher matcher = pattern.matcher(fileName);

       return !matcher.find();
    }

    public static String getInvalidCharsFileName(){
        return "\\*/:<>?\"|";
    }

    public static List<File> getFiles(@NotNull String pathNameDir){
        File dir = new File(pathNameDir);

        if(dir.exists()){
            return Arrays.asList(dir.listFiles());
        }

        return new ArrayList<File>();
    }
}
