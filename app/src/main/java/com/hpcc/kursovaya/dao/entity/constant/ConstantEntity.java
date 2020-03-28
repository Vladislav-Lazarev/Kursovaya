package com.hpcc.kursovaya.dao.entity.constant;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.hpcc.kursovaya.dao.entity.Speciality;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConstantEntity {

    public static int[][][] timeArray = { {{8,0},{8,50}},
                                          {{9,45},{10,35}},
                                          {{11,55},{12,45}},
                                          {{13,40},{14,30}},
                                          {{15,25},{16,20}} };

    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // QUERY
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String NUMBER_COURSE = "numberCourse";
    public static final String COUNT_COURSE = "countCourse";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static final int MIN_COUNT_ACADEMIC_HOUR = ONE;// Максимальной(наибольшее) количестов чатов в паре
    public static final int MAX_COUNT_ACADEMIC_HOUR = TWO;// Максимальной(наибольшее) количестов чатов в паре

    public static final int MIN_COUNT_LESSON = 3;// Максимальной(наибольшее) количестов пар в день
    public static final int MAX_COUNT_LESSON = 5;// Максимальной(наибольшее) количестов пар в день

    public static final int MIN_DAY_OF_WEEK = ZERO;
    public static final int MAX_DAY_OF_WEEK = 6;

    public static final int MIN_COUNT_WEEK = MIN_DAY_OF_WEEK + ONE;
    public static final int MAX_COUNT_WEEK = MAX_DAY_OF_WEEK + ONE;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static final int ACTIVITY_ADD = 1;
    public static final int ACTIVITY_EDIT = 2;

    public static Map<Speciality, Integer> convertMapEditTextToMapInt(Map<Speciality, EditText> mapEdit){
        Map<Speciality, Integer> mapInt = new LinkedHashMap<>();
        for (Map.Entry<Speciality, EditText> set : mapEdit.entrySet()){
            mapInt.put(set.getKey(), Integer.parseInt(set.getValue().getText().toString()));
        }
        return mapInt;
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
            if(spin.getAdapter().getItem(i).toString().contains(str)) {
                spin.setSelection(i);
                return true;
            }
        }
        return false;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
