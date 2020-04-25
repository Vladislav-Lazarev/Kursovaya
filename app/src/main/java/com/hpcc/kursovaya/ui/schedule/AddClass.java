package com.hpcc.kursovaya.ui.schedule;

import android.os.Bundle;

import com.hpcc.kursovaya.R;

import org.joda.time.DateTime;

public class AddClass extends Class {
    private static final String TAG = AddClass.class.getSimpleName() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        intent = getIntent();
        dayOfWeek = (DateTime)intent.getSerializableExtra("dayOfWeek");
        numberOfLesson = intent.getIntExtra("classHour",0);
        classHour = intent.getIntExtra("classHour",0);
        classDay = intent.getIntExtra("classDay",0);
        super.onCreate(savedInstanceState);
        numberOfHalf = ((numberOfLesson+1) % 2 == 0)? 1 : 0;
        numberOfLesson = numberOfLesson/2;
        String str = "";
    }

    @Override
    protected void setHeader(int popup_super){
        super.setHeader(R.string.add_class);
    }

    protected void actionBar(ActionBarI actionBarI){
        super.actionBar(this::addClass);
    }




}