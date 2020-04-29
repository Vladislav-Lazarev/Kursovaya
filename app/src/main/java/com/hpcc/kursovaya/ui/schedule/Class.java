package com.hpcc.kursovaya.ui.schedule;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import com.hpcc.kursovaya.AlarmClassReceiver;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.templates.GroupAutoCompleteAdapter;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public abstract class Class extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    protected AcademicHour currentAcademicHour;
    protected TemplateAcademicHour currentTemplateAcademicHour;
    protected ArrayList<AcademicHour> academicHourList;
    protected ArrayList<TemplateAcademicHour> templateAcademicHourList;

    private static final String TAG = AddClass.class.getSimpleName() ;
    protected final Context currentContext = this;
    Intent intent;
    DateTime dayOfWeek;
    DateTime timeOfRing = new DateTime();
    int classDay = 0;
    int classHour = 0;
    int numberOfLesson = 0;
    int numberOfHalf = 0;
    AutoCompleteTextView groupNameSuggest;
    RadioGroup radioGroup;
    Spinner subjectSpinner;
    boolean repeatForNextWeek = false;
    Spinner repeatForNextWeekContent;
    boolean notificationBefore = false;
    Spinner notificationBeforeContent;
    EditText classSummary;
    private long mLastClickTime = 0;

    int posSecondCell = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //intent = getIntent();
        setContentView(R.layout.activity_add_class);
        posSecondCell = secondCellShift(numberOfLesson);
        groupNameSuggest = findViewById(R.id.groupNameSuggestET);
        academicHourList = new ArrayList<>(Collections.singletonList(new AcademicHour()));
        templateAcademicHourList = new ArrayList<>(Collections.singletonList(new TemplateAcademicHour()));
        currentAcademicHour = academicHourList.get(ConstantApplication.ZERO);
        currentTemplateAcademicHour = templateAcademicHourList.get(ConstantApplication.ZERO);
        currentTemplateAcademicHour.setDayAndPair(Pair.create(classDay,classHour));
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_path_150));
        //here place for getting classDay, classHour and Group\Subject entities
        setSupportActionBar(toolbar);
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
        actionBar(new ActionBarI() {
            @Override
            public void superClass() {

            }
        });

        subjectSpinner = findViewById(R.id.spinnerSubject);
        listenerSpinnerSubject(subjectSpinner);

        groupNameSuggest = findViewById(R.id.groupNameSuggestET);
        List<Group> groupList = DBManager.copyObjectFromRealm(DBManager.readAll(Group.class));
        GroupAutoCompleteAdapter adapter = new GroupAutoCompleteAdapter(this,R.layout.group_auto, groupList);
        groupNameSuggest.setAdapter(adapter);
        groupNameSuggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strGroup = s.toString();
                Group group = DBManager.read(Group.class, ConstantApplication.NAME, strGroup);

                if (!ConstantApplication.checkUIGroup(strGroup) || group == null){
                    ConstantApplication.fillingSpinner(currentContext, subjectSpinner, new ArrayList<>());
                    return;
                }

                currentTemplateAcademicHour.setGroup(group);
                fillSpinnerByGroup(currentContext, subjectSpinner, group);

                if (templateAcademicHourList.size() == ConstantApplication.TWO){
                    templateAcademicHourList.set(ConstantApplication.ONE,
                            templateAcademicHourList.get(ConstantApplication.ONE).setGroup(group));
                }
            }
        });
        groupNameSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Group pressedGroup = (Group) adapterView.getItemAtPosition(i);
                currentTemplateAcademicHour.setGroup(pressedGroup);
                fillSpinnerByGroup(currentContext, subjectSpinner, pressedGroup);

                if (templateAcademicHourList.size() == ConstantApplication.TWO){
                    templateAcademicHourList.set(ConstantApplication.ONE,
                            templateAcademicHourList.get(ConstantApplication.ONE).setGroup(pressedGroup));
                }
            }
        });

        ((RadioButton)findViewById(R.id.duration_rgroup_short)).setChecked(true);
        radioGroup = findViewById(R.id.duration_rgroup);
        radioGroup.setOnCheckedChangeListener((rg, checkedId) -> {
            currentTemplateAcademicHour.setDayAndPair(Pair.create(classDay, classHour));
            final int posSecondCell = secondCellShift(classHour);
            switch (checkedId){
                case R.id.duration_rgroup_short:
                    if (templateAcademicHourList.size() == ConstantApplication.TWO) {
                        templateAcademicHourList.remove(ConstantApplication.ONE);
                        academicHourList.remove(ConstantApplication.ONE);
                    }
                    break;
                case R.id.duration_rgroup_full:
                    if (templateAcademicHourList.size() == ConstantApplication.ONE){
                        TemplateAcademicHour following = new TemplateAcademicHour();
                        if (currentTemplateAcademicHour.getGroup() != null){
                            following.setGroup(currentTemplateAcademicHour.getGroup())
                                    .setSubject(currentTemplateAcademicHour.getSubject())
                                    .setDayAndPair(Pair.create(classDay, classHour + posSecondCell));
                        }
                        templateAcademicHourList.add(following);
                        AcademicHour followingHour = new AcademicHour();
                        followingHour.setDate(currentAcademicHour.getDate())
                                     .setNote(currentAcademicHour.getNote());
                        academicHourList.add(followingHour);
                    } else if (templateAcademicHourList.size() == ConstantApplication.TWO) {
                        templateAcademicHourList.set(ConstantApplication.ONE,
                                templateAcademicHourList.get(ConstantApplication.ONE)
                                        .setDayAndPair(Pair.create(classDay, classHour + posSecondCell)));
                    }
                    break;
            }
        });
       classSummary = findViewById(R.id.description_editText);
       notificationBeforeContent = findViewById(R.id.spinnerNotificationBefore);
       repeatForNextWeekContent = findViewById(R.id.spinnerRepeatForWeeks);
       notificationBeforeContent.setOnItemSelectedListener(this);
       setHeader(R.string.super_class);
    }

    protected void fillSpinnerByGroup(Context context, Spinner spinner, Group group){
        List<Subject> subjectList = group.toSubjectList(group.getNumberCourse(), group.getSpecialty());
        List<String> stringList = new Subject().entityToNameList(subjectList);
        ConstantApplication.fillingSpinner(context, spinner, stringList);
    }

    private void listenerSpinnerSubject(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                Subject subject = DBManager.read(Subject.class, ConstantApplication.NAME, item);
                currentTemplateAcademicHour.setSubject(subject);

                if (templateAcademicHourList.size() == ConstantApplication.TWO){
                    templateAcademicHourList.set(ConstantApplication.ONE,
                            templateAcademicHourList.get(ConstantApplication.ONE).setSubject(subject));
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Название Активити
    protected void setHeader(int popup_super){
        // Name Head
        TextView textCont = findViewById(R.id.toolbar_title);
        textCont.setText(getResources().getString(popup_super));
    }

    interface ActionBarI{
        void superClass();
    }

    protected int secondCellShift(int currentCellPosition){
        return (currentCellPosition % ConstantApplication.TWO == ConstantApplication.ZERO) ? 1 : -1;
    }

    protected void actionBar(ActionBarI actionBarI){
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ImageButton btnAdd = findViewById(R.id.create_class);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                actionBarI.superClass();
            }
        });
    }

    protected void addClass() {
        if (!ConstantApplication.checkUIGroup(groupNameSuggest.getText().toString())){
            groupNameSuggest.setError(getString(R.string.toast_check));
            return;
        }
        if (subjectSpinner.getCount() == ConstantApplication.ZERO){
            Toast.makeText(this, R.string.toast_check_subject_menu_bar, Toast.LENGTH_LONG).show();
            return;
        }

        Date entityDate = dayOfWeek.toDate();
        String description = classSummary.getText().toString();
        String groupNameStr = groupNameSuggest.getText().toString();
        for (TemplateAcademicHour templateAcademicHour : templateAcademicHourList){
            try {
                if(templateAcademicHour.getNumberHalfPairButton()==-1){
                    final int posSecondCell = secondCellShift(classHour);
                    templateAcademicHour.setDayAndPair(Pair.create(classDay, (classHour + posSecondCell)));
                }
                DBManager.write(templateAcademicHour.createEntity());
                Log.d(TAG, "templateAcademicHour = " + templateAcademicHour.toString());
            } catch (Exception e) {
                // TODO Оповещение о не правильности\корректности
                e.printStackTrace();
            }
        }
        academicHourList.get(0).setNotificationBefore(notificationBeforeContent.getSelectedItemPosition());
        for(int i = 0; i<academicHourList.size();i++){
            academicHourList.get(i).setRepeatForNextWeek(repeatForNextWeekContent.getSelectedItemPosition());
            academicHourList.get(i).setNote(description);
            academicHourList.get(i).setDate(entityDate);
            academicHourList.get(i).setTemplateAcademicHour(templateAcademicHourList.get(i));
            try {
                DBManager.write(academicHourList.get(i).createEntity());
                Log.d(TAG, "templateAcademicHour = " + academicHourList.get(i).toString());
            } catch (Exception e) {
                // TODO Оповещение о не правильности\корректности
                e.printStackTrace();
            }
        }
        intent.putExtra("isTwoHour",false);
        intent.putExtra("firstHour",academicHourList.get(0));
        if(academicHourList.size()==2){
            intent.putExtra("isTwoHour",true);
            intent.putExtra("secondCellPosition",posSecondCell);
            intent.putExtra("secondHour",academicHourList.get(1));
        }

        DateTime now = DateTime.now();
        Seconds difference = Seconds.secondsBetween(now,timeOfRing);
        if(notificationBefore && difference.getSeconds()>0) {

            Log.d(TAG,timeOfRing.toString());
            Log.d(TAG, difference.toString());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND,difference.getSeconds());
            int hourOfDay = ConstantApplication.timeArray[numberOfLesson][numberOfHalf][0];
            int minuteOfHour = ConstantApplication.timeArray[numberOfLesson][numberOfHalf][1];
            Intent _intent = new Intent(getApplicationContext(), AlarmClassReceiver.class);
            _intent.putExtra("groupName",groupNameStr);
            _intent.putExtra("yearOfNot",Integer.toString(dayOfWeek.getYear()));
            _intent.putExtra("monthOfYearNot",Integer.toString(dayOfWeek.getMonthOfYear()));
            _intent.putExtra("dayOfMonthNot",Integer.toString(dayOfWeek.getDayOfMonth()));
            _intent.putExtra("description",description);
            _intent.putExtra("hourOfDay",hourOfDay);
            _intent.putExtra("minuteOfHour",minuteOfHour);
            String strFlag = dayOfWeek.getMonthOfYear()+""+ dayOfWeek.getDayOfMonth() + hourOfDay + minuteOfHour;
            final int id = Integer.valueOf(strFlag);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, _intent, 0);
            AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
