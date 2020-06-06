package com.hpcc.kursovaya.ui.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import com.hpcc.kursovaya.AlarmClassReceiver;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.schedule.AnotherEvent;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAnotherEvent;
import com.hpcc.kursovaya.dao.query.DBManager;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.Calendar;

import yuku.ambilwarna.AmbilWarnaDialog;

public abstract class Event extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    protected static String TAG = Event.class.getSimpleName();

    private long mLastClickTime = 0;
    protected TextView title;
    protected TextView eventNameTV;
    protected TextView eventDescriptionTV;
    protected Button colorPickButton;
    protected DateTime dayOfWeek;
    protected int numberDayOfWeek;// Number day of Week
    protected int numberHalfPair;// Number half pair

    boolean notificationBefore = false;
    protected Spinner notificationBeforeContent;
    protected AnotherEvent anotherEvent;
    protected TemplateAnotherEvent templateAnotherEvent;
    private DateTime timeOfRing;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_path_150));
        //here place for getting classDay, classHour and Group\Subject entities
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                setResult(1);
                finish();
            }
        });
        ImageButton createEvent = findViewById(R.id.create_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                addEvent();
            }
        });
        eventNameTV = findViewById(R.id.nameSuggestET);
        eventDescriptionTV = findViewById(R.id.description_editText);
        colorPickButton = findViewById(R.id.pickColorBtn);
        GradientDrawable background = (GradientDrawable) colorPickButton.getBackground();
        background.setColor(Color.RED);
        templateAnotherEvent = new TemplateAnotherEvent();
        anotherEvent = new AnotherEvent();
        templateAnotherEvent.setColor(Color.RED);
        colorPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                openColorPicker();
            }
        });
        notificationBeforeContent = findViewById(R.id.spinnerNotificationBefore);
        notificationBeforeContent.setOnItemSelectedListener(this);
    }

    protected void addEventToDB(){
        String name = eventNameTV.getText().toString();
        templateAnotherEvent.setTitle(name);
        templateAnotherEvent.setDayAndPair(new Pair<>(numberDayOfWeek,numberHalfPair));
        String description = eventDescriptionTV.getText().toString();
        anotherEvent.setNote(description);
        anotherEvent.setDate(dayOfWeek.toDate());
        anotherEvent.setRepeatForNextWeek(0);
        anotherEvent.setNotificationBefore(notificationBeforeContent.getSelectedItemPosition());
        try {
            templateAnotherEvent.createEntity();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return;
        }
        anotherEvent.setTemplateAnotherEvent(templateAnotherEvent);
        try {
            DBManager.write(templateAnotherEvent);
            DBManager.write(anotherEvent.createEntity());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    protected void setAlarm(){
       DateTime now = DateTime.now();
        Seconds difference = Seconds.secondsBetween(now,timeOfRing);
        if(notificationBefore && difference.getSeconds()>0) {
            Calendar ringTime = Calendar.getInstance();
            ringTime.add(Calendar.SECOND,difference.getSeconds());
            int numberOfLesson = numberHalfPair/2;
            int numberOfHalf = ((numberHalfPair+1) % 2 == 0)? 1 : 0;
            int hourOfDay = ConstantApplication.timeArray[numberOfLesson][numberOfHalf][0];
            int minuteOfHour = ConstantApplication.timeArray[numberOfLesson][numberOfHalf][1];
            Intent _intent = new Intent(getApplicationContext(), AlarmClassReceiver.class);
            _intent.putExtra("groupName",eventNameTV.getText().toString());
            _intent.putExtra("yearOfNot",Integer.toString(dayOfWeek.getYear()));
            _intent.putExtra("monthOfYearNot",Integer.toString(dayOfWeek.getMonthOfYear()));
            _intent.putExtra("dayOfMonthNot",Integer.toString(dayOfWeek.getDayOfMonth()));
            _intent.putExtra("description",eventDescriptionTV.getText().toString());
            _intent.putExtra("hourOfDay",hourOfDay);
            _intent.putExtra("minuteOfHour",minuteOfHour);
            String strFlag = ringTime.get(Calendar.MONTH)+""+ringTime.get(Calendar.DAY_OF_MONTH)+""+ringTime.get(Calendar.HOUR_OF_DAY)+""+ringTime.get(Calendar.MINUTE);
            int id = Integer.valueOf(strFlag);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),id,_intent,0);
            AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP,ringTime.getTimeInMillis(),pendingIntent);
        }
    }

    protected abstract void addEvent();

    protected void setHeader(int titleId){
        title.setText(getResources().getString(titleId));
    }

    public void openColorPicker(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, templateAnotherEvent.getColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                templateAnotherEvent.setColor(color);
                Log.d(TAG,"TemplateEvent changed color"+ Integer.toString(templateAnotherEvent.getColor()));
                colorPickButton = (Button) findViewById(R.id.pickColorBtn);
                GradientDrawable background = (GradientDrawable) colorPickButton.getBackground();
                background.setColor(color);
                colorPickButton.setBackground(background);
            }
        });
        colorPicker.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int numberOfLesson = numberHalfPair/2;
        int numberOfHalf = ((numberOfLesson+1) % 2 == 0)? 1 : 0;
        int hourOfDay = ConstantApplication.timeArray[numberOfLesson][numberOfHalf][0];
        int minuteOfHour = ConstantApplication.timeArray[numberOfLesson][numberOfHalf][1];
        timeOfRing = new DateTime(dayOfWeek);
        switch(position) {
            case 0:
                notificationBefore = false;
                break;
            case 1:
                notificationBefore = true;
                timeOfRing =  dayOfWeek.withHourOfDay(hourOfDay/*<-- place for hour local variable*/-1).withMinuteOfHour(minuteOfHour/*minute of class*/);
                break;
            case 2:
                notificationBefore = true;
                timeOfRing =  dayOfWeek.withHourOfDay(hourOfDay/*<-- place for hour local variable*/-2).withMinuteOfHour(minuteOfHour);
                break;
            case 3:
                notificationBefore = true;
                timeOfRing =  dayOfWeek.withHourOfDay(hourOfDay/*<-- place for hour local variable*/-3).withMinuteOfHour(minuteOfHour);
                break;
            case 4:
                notificationBefore = true;
                timeOfRing =  dayOfWeek.withHourOfDay(hourOfDay).withMinuteOfHour(minuteOfHour).minusDays(1);
                break;
            case 5:
                notificationBefore = true;
                timeOfRing = dayOfWeek.withHourOfDay(hourOfDay).withMinuteOfHour(minuteOfHour).minusDays(2);
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
