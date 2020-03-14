package com.hpcc.kursovaya.ui.schedule;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.hpcc.kursovaya.AlarmClassReceiver;
import com.hpcc.kursovaya.R;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.Calendar;

public class AddClass extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String CHANNEL_ID = "Class";
    private static final int NOTIFY_ID = 1;
    DateTime dayOfWeek;
    int numberOfHour;
    AutoCompleteTextView groupName;
    Spinner choosenSubject;
    boolean repeatForWeeks = false;
    Spinner repeatForWeeksContent;
    boolean notificationBefore = false;
    Spinner notificationBeforeContent;
    EditText classSummary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        groupName = findViewById(R.id.groupNameSuggestET);
        Intent intent = getIntent();
        dayOfWeek =(DateTime) intent.getSerializableExtra("dayOfWeek");
        numberOfHour = intent.getIntExtra("classHour",0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_path_150));
        //here place for getting classDay and classHour
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
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
            Seconds difference = Seconds.secondsBetween(now,dayOfWeek);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND,5);
            Intent _intent = new Intent(this, AlarmClassReceiver.class);
            _intent.putExtra("groupName",groupNameStr);
            _intent.putExtra("yearOfNot",Integer.toString(dayOfWeek.getYear()));
            _intent.putExtra("monthOfYearNot",Integer.toString(dayOfWeek.getMonthOfYear()));
            _intent.putExtra("dayOfMonthNot",Integer.toString(dayOfWeek.getDayOfMonth()));
            _intent.putExtra("description",classSummary.getText().toString());
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
        switch(position) {
            case 0:
                notificationBefore = false;
                break;
            case 1:
                notificationBefore = true;
                dayOfWeek =  dayOfWeek.withHourOfDay(8/*<-- place for hour local variable*/-1).withMinuteOfHour(0/*minute of class*/);
                break;
            case 2:
                notificationBefore = true;
                dayOfWeek =  dayOfWeek.withHourOfDay(8/*<-- place for hour local variable*/-2).withMinuteOfHour(0);
                break;
            case 3:
                notificationBefore = true;
                dayOfWeek =  dayOfWeek.withHourOfDay(8/*<-- place for hour local variable*/-3).withMinuteOfHour(0);
                break;
            case 4:
                notificationBefore = true;
                //handle 1 day before excercise
                break;
            case 5:
                notificationBefore = true;
                dayOfWeek = dayOfWeek.minusDays(1);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
