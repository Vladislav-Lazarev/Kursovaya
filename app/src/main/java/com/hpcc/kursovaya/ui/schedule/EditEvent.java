package com.hpcc.kursovaya.ui.schedule;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;

import org.joda.time.DateTime;

public class EditEvent extends Event {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHeader(R.string.edit_event);
        Intent intent = getIntent();
        dayOfWeek = (DateTime)intent.getSerializableExtra("dayOfWeek");
        numberDayOfWeek = intent.getIntExtra("classDay",0);
        numberHalfPair = intent.getIntExtra("classHour",0);
        anotherEvent = intent.getParcelableExtra("currentEvent");
        eventNameTV.setText(anotherEvent.getTemplateAnotherEvent().getTitle());
        eventDescriptionTV.setText(anotherEvent.getNote());
        notificationBeforeContent.setSelection(anotherEvent.getNotificationBefore());
        colorPickButton = findViewById(R.id.pickColorBtn);
        GradientDrawable background = (GradientDrawable) colorPickButton.getBackground();
        background.setColor(anotherEvent.getTemplateAnotherEvent().getColor());
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
