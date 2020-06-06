package com.hpcc.kursovaya.ui.schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;

import org.joda.time.DateTime;

public class AddEvent extends Event {
    protected static String TAG = AddEvent.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHeader(R.string.add_event);
        Intent intent = getIntent();
        dayOfWeek = (DateTime)intent.getSerializableExtra("dayOfWeek");
        numberDayOfWeek = intent.getIntExtra("classDay",0);
        numberHalfPair = intent.getIntExtra("classHour",0);
    }

    @Override
    protected void addEvent() {
        addEventToDB();
        setAlarm();
        Intent intent = getIntent();
        intent.putExtra("anotherEvent",anotherEvent);
        setResult(Activity.RESULT_OK,getIntent());
        finish();
    }
}
