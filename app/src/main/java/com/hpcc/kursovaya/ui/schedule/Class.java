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

    protected void setHeader(int popup_super){
        // Name Head
        TextView textCont = findViewById(R.id.toolbar_title);
        textCont.setText(getResources().getString(popup_super));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //intent = getIntent();
        setContentView(R.layout.activity_add_class);

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

        if (academicHourList == null){
            academicHourList = new ArrayList<>(Collections.singletonList(new AcademicHour()));
            templateAcademicHourList = new ArrayList<>(Collections
                    .singletonList(new TemplateAcademicHour().setDayAndPair(Pair.create(classDay, classHour))));

            currentAcademicHour = academicHourList.get(ConstantApplication.ZERO);
            currentTemplateAcademicHour = templateAcademicHourList.get(ConstantApplication.ZERO);

            ((RadioButton)findViewById(R.id.duration_rgroup_short)).setChecked(true);
        } else {
            currentAcademicHour = academicHourList.get(ConstantApplication.ZERO);
            currentTemplateAcademicHour = currentAcademicHour.getTemplateAcademicHour();
            templateAcademicHourList.add(currentTemplateAcademicHour);

            switch (academicHourList.size()){
                case ConstantApplication.ONE:
                    ((RadioButton)findViewById(R.id.duration_rgroup_short)).setChecked(true);
                    break;
                case ConstantApplication.TWO:
                    AcademicHour secondCell = academicHourList.get(ConstantApplication.ONE);
                    TemplateAcademicHour secondCellTemplate = secondCell.getTemplateAcademicHour();

                    if(currentTemplateAcademicHour.getGroup().equals(secondCellTemplate.getGroup()) &&
                            currentTemplateAcademicHour.getSubject().equals(secondCellTemplate.getSubject()) &&
                            currentAcademicHour.getRepeatForNextWeek() == secondCell.getRepeatForNextWeek() &&
                            secondCell.getNote().equals(currentAcademicHour.getNote())){
                        templateAcademicHourList.add(secondCellTemplate);
                    }
                    ((RadioButton)findViewById(R.id.duration_rgroup_full)).setChecked(true);
                    break;
            }
        }

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
            String strGroup = s.toString();
            Group group = DBManager.read(Group.class, ConstantApplication.NAME, strGroup);

            if (!ConstantApplication.checkUI(ConstantApplication.PATTERN_GROUP, strGroup) || group == null){
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

        @Override
        public void afterTextChanged(Editable s) {

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

        subjectSpinner = findViewById(R.id.spinnerSubject);
        listenerSpinnerSubject(subjectSpinner);

        radioGroup = findViewById(R.id.duration_rgroup);
        posSecondCell = ConstantApplication.secondCellShift(numberOfLesson);
        radioGroup.setOnCheckedChangeListener((rg, checkedId) -> {
        switch (checkedId){
            case R.id.duration_rgroup_short:
                if (templateAcademicHourList.size() == ConstantApplication.TWO) {
                    AcademicHour deleteAcademicHour = academicHourList.get(ConstantApplication.ONE);
                    if (deleteAcademicHour.getId() != ConstantApplication.ZERO) {
                        DBManager.delete(AcademicHour.class, ConstantApplication.ID,
                                deleteAcademicHour.getId());
                        DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID,
                                deleteAcademicHour.getTemplateAcademicHour().getId());
                    }

                    academicHourList.remove(ConstantApplication.ONE);
                    templateAcademicHourList.remove(ConstantApplication.ONE);
                }
                break;
            case R.id.duration_rgroup_full:
                if (academicHourList.size() == ConstantApplication.TWO &&
                        academicHourList.get(ConstantApplication.ONE).getId() != ConstantApplication.ZERO) {
                    break;
                }

                TemplateAcademicHour following = new TemplateAcademicHour();
                if (currentTemplateAcademicHour.getGroup() != null) {
                    following.setGroup(currentTemplateAcademicHour.getGroup())
                            .setSubject(currentTemplateAcademicHour.getSubject())
                            .setDayAndPair(Pair.create(classDay, classHour + posSecondCell));
                }
                templateAcademicHourList.add(following);
                academicHourList.add(new AcademicHour());
                break;
        }
        });

        classSummary = findViewById(R.id.description_editText);
        notificationBeforeContent = findViewById(R.id.spinnerNotificationBefore);
        repeatForNextWeekContent = findViewById(R.id.spinnerRepeatForWeeks);
        notificationBeforeContent.setOnItemSelectedListener(this);
        setHeader(R.string.super_class);
    }

    protected void saveClass() {
        Date entityDate = dayOfWeek.toDate();
        String description = classSummary.getText().toString();
        String groupNameStr = groupNameSuggest.getText().toString();

        if (DBManager.read(Group.class, ConstantApplication.NAME, groupNameStr) == null){
            groupNameSuggest.setError(getString(R.string.toast_check));
            return;
        }
        if (subjectSpinner.getCount() == ConstantApplication.ZERO){
            Toast.makeText(this, R.string.toast_fragment_no_subjects, Toast.LENGTH_LONG).show();
            return;
        }

        int num = -1;

        academicHourList.get(ConstantApplication.ZERO)
                .setNotificationBefore(notificationBeforeContent.getSelectedItemPosition());
        for (int i = 0; templateAcademicHourList.size() == academicHourList.size() && i < academicHourList.size(); i++){
            TemplateAcademicHour currentTemplate = null;
            try {
                currentTemplate = templateAcademicHourList.get(i).createEntity();
                DBManager.write(currentTemplate);
                Log.d(TAG, "templateAcademicHour = " + currentTemplate.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            academicHourList.get(i).setRepeatForNextWeek(repeatForNextWeekContent.getSelectedItemPosition());
            academicHourList.get(i).setNote(description);
            ///
            int halfPosition = templateAcademicHourList.get(i).getNumberHalfPairButton();
            int hourOfDay = ConstantApplication.timeArray[halfPosition/2][((halfPosition+1) % 2 == 0)? 1 : 0][0];
            int minuteOfHour = ConstantApplication.timeArray[halfPosition/2][((halfPosition+1) % 2 == 0)? 1 : 0][1];
            DateTime time = dayOfWeek.withHourOfDay(hourOfDay).withMinuteOfHour(minuteOfHour);
            academicHourList.get(i).setDate(time.toDate());
            ///
            academicHourList.get(i).setTemplateAcademicHour(currentTemplate);
            try {
                DBManager.write(academicHourList.get(i).createEntity());
                Log.d(TAG, "templateAcademicHour = " + academicHourList.get(i).toString());
                academicHourList.get(i).refreshAllNumberPair(null);
                num = academicHourList.get(i).getNumberPair();
                Log.d("num = ", num + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        intent.putExtra("academicHourList", academicHourList);

        DateTime now = DateTime.now();
        Seconds difference = Seconds.secondsBetween(now,timeOfRing);
        if(notificationBefore && difference.getSeconds() > 0) {

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

    protected void fillSpinnerByGroup(Context context, Spinner spinner, Group group){
        List<Subject> subjectList = group.toSubjectList(group.getNumberCourse(), group.getSpecialty());
        List<String> stringList = Subject.entityToNameList(subjectList);
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

    interface ActionBarI{
        void superClass();
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
