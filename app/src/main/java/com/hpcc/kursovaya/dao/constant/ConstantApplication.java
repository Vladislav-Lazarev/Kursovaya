package com.hpcc.kursovaya.dao.constant;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.hpcc.kursovaya.dao.entity.Speciality;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ConstantApplication {

    public static final int ALARM_FLAG = 67;
    public static int[][][] timeArray = { {{8,0}, {8,50}},
                                          {{9,45}, {10,35}},
                                          {{11,55}, {12,45}},
                                          {{13,40}, {14,30}},
                                          {{15,25}, {16,15}} };

    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int FOUR = 4;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // QUERY
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String NUMBER_COURSE = "numberCourse";
    public static final String COUNT_COURSE = "countCourse";
    public static final String ID_SPECIALITY = "idSpeciality";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static final String DIR_BACKUP = "backup";
    public static final String DIR_DB = "database";
    public static final String DB_NAME = "DB.realm";
    public static final String DIR_DELIMITER = File.separator;
    public static final String DB_EXTENSION = ".realm";

    public static final int MIN_COUNT_HALF_PAIR = ONE;// Максимальной(наибольшее) количестов полупар в день
    public static final int MAX_COUNT_HALF_PAIR = 10;// Максимальной(наибольшее) количестов полупар в день

    public static final int MIN_COUNT_LESSON = 3;// Максимальной(наибольшее) количестов пар в день
    public static final int MAX_COUNT_LESSON = MAX_COUNT_HALF_PAIR / TWO;// Максимальной(наибольшее) количестов пар в день

    public static final int MIN_COUNT_ACADEMIC_HOUR = ONE;// Максимальной(наибольшее) количестов часов в паре
    public static final int MAX_COUNT_ACADEMIC_HOUR = TWO;// Максимальной(наибольшее) количестов часов в паре

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static final int MIN_DAY_OF_WEEK = ZERO;
    public static final int MAX_DAY_OF_WEEK = 6;

    public static final int MIN_COUNT_WEEK = MIN_DAY_OF_WEEK + ONE;
    public static final int MAX_COUNT_WEEK = MAX_DAY_OF_WEEK + ONE;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // UI

    public static final int ACTIVITY_ADD = 1;
    public static final int ACTIVITY_EDIT = 2;

    public static final int CLICK_TIME = 500;

    public static Map<Speciality, Integer> convertMapEditTextToMapInt(Map<Speciality, EditText> mapEdit){
        Map<Speciality, Integer> mapInt = new LinkedHashMap<>();
        for (Map.Entry<Speciality, EditText> set : mapEdit.entrySet()){
            mapInt.put(set.getKey(), Integer.parseInt(set.getValue().getText().toString()));
        }
        return mapInt;
    }
    public static List<String> countCourse(int countCourse){
        List<String> result = new ArrayList<>();
        for (int i = 1; i <= countCourse; i++) {
            result.add(String.valueOf(i));
        }
        return result;
    }

    public static Spinner fillingSpinner(Context context, Spinner spinner, List<String> stringList){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, stringList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        return spinner;
    }
    public static boolean setSpinnerText(Spinner spin, String str) {
        for(int i= 0; i < spin.getAdapter().getCount(); i++) {
            if(String.valueOf(spin.getAdapter().getItem(i)).contains(str)) {
                spin.setSelection(i);
                return true;
            }
        }
        return false;
    }

    public static int secondCellShift(int currentCellPosition){
        return (currentCellPosition % ConstantApplication.TWO == ConstantApplication.ZERO) ? 1 : -1;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // UI

    public static final String PATTERN_DATE_TIME = "dd/MM/yyyy HH:mm:ss";
    public static final String MIN_DATE_TIME = "01/01/1990 00:00:00";

    public static final String PATTERN_SPECIALITY = "^(\\p{Upper}\\p{Lower}+[.]?)(([\\s-])(\\p{Alpha}{2,})[.]?)*|(\\p{Upper}{2,})$";
    public static final String PATTERN_GROUP = "^[\\p{Alnum}-]{2,}$";
    public static final String PATTERN_SUBJECT = PATTERN_SPECIALITY;
    public static final String PATTERN_TEMPLATE = "^[\\p{Alnum}\\p{Punct}][\\s\\p{Alnum}\\p{Punct}]*$";

    // checkUI
    public static boolean checkUI(@NotNull String pattern, @NotNull String str){
        return Pattern.matches(pattern, str);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
