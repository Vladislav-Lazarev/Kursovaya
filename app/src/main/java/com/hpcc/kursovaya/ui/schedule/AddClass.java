package com.hpcc.kursovaya.ui.schedule;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.AlarmClassReceiver;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.Calendar;

public class AddClass extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String CHANNEL_ID = "Class";
    private static final int NOTIFY_ID = 1;
    private static final String TAG = AddClass.class.getSimpleName() ;
    DateTime dayOfWeek;
    DateTime timeOfRing = new DateTime();
    int numberOfLesson;
    int numberOfHalf;
    AutoCompleteTextView groupName;
    Spinner choosenSubject;
    boolean repeatForWeeks = false;
    Spinner repeatForWeeksContent;
    boolean notificationBefore = false;
    Spinner notificationBeforeContent;
    EditText classSummary;
    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        groupName = findViewById(R.id.groupNameSuggestET);
        Intent intent = getIntent();
        dayOfWeek =(DateTime) intent.getSerializableExtra("dayOfWeek");
        numberOfLesson = intent.getIntExtra("classHour",0);
        if((numberOfLesson+1)%2==0){
            numberOfHalf = 1;
        } else {
            numberOfHalf = 0;
        }
        numberOfLesson = numberOfLesson/2;
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_path_150));
        //here place for getting classDay and classHour
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                setResult(1);
                finish();
            }
        });
        classSummary = findViewById(R.id.description_editText);
        notificationBeforeContent = findViewById(R.id.spinnerNotificationBefore);
        notificationBeforeContent.setOnItemSelectedListener(this);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ImageButton btnAdd = findViewById(R.id.create_class);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                addClass();
            }
        });
        TextView textCont = (TextView)findViewById(R.id.toolbar_title);
        textCont.setText(R.string.add_class);
    }
    /*
    TestMethod
    Needs correction in future
     */
    private void addClass() {
        String groupNameStr = groupName.getText().toString();
        Intent intent = getIntent();
        intent.putExtra("groupName",groupNameStr);

        if(notificationBefore) {
            DateTime now = DateTime.now();
            Seconds difference = Seconds.secondsBetween(now,timeOfRing);
            Log.d(TAG,timeOfRing.toString());
            Log.d(TAG, difference.toString());
            Calendar calendar = Calendar.getInstance();
            //PT431762S
            calendar.add(Calendar.SECOND,difference.getSeconds());
            int hourOfDay = ConstantEntity.timeArray[numberOfLesson][numberOfHalf][0];
            int minuteOfHour = ConstantEntity.timeArray[numberOfLesson][numberOfHalf][1];
            Intent _intent = new Intent(this, AlarmClassReceiver.class);
            _intent.putExtra("groupName",groupNameStr);
            _intent.putExtra("yearOfNot",Integer.toString(dayOfWeek.getYear()));
            _intent.putExtra("monthOfYearNot",Integer.toString(dayOfWeek.getMonthOfYear()));
            _intent.putExtra("dayOfMonthNot",Integer.toString(dayOfWeek.getDayOfMonth()));
            _intent.putExtra("description",classSummary.getText().toString());
            _intent.putExtra("hourOfDay",hourOfDay);
            _intent.putExtra("minuteOfHour",minuteOfHour);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 228, _intent, 0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            // Remove any previous pending intent.
            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            /*DateTime now = DateTime.now();
            Seconds difference = Seconds.secondsBetween(now,dayOfWeek);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND,5);
            Intent intentAction = new Intent("class.action.DISPLAY_NOTIFICATION");
            PendingIntent broadcast = PendingIntent.getBroadcast(this,228,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),broadcast);
            /*NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(AddClass.this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentTitle("Дата N пара Y година")
                            .setContentText(classSummary.getText())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(AddClass.this);
            notificationManager.notify(NOTIFY_ID, builder.build());*/
        }
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int hourOfDay = ConstantEntity.timeArray[numberOfLesson][numberOfHalf][0];
        int minuteOfHour = ConstantEntity.timeArray[numberOfLesson][numberOfHalf][1];
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
                timeOfRing = dayOfWeek.minusDays(2);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
