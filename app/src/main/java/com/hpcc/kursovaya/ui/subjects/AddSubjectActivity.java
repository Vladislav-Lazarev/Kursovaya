package com.hpcc.kursovaya.ui.subjects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AddSubjectActivity extends AppCompatActivity {
    private static final String TAG = AddSubjectActivity.class.getSimpleName();
    private final Context currentContext = this;

    private Button colorPickButton;
    private EditText subjectEditText;
    private Spinner spinnerCourse;
    private int minCourseCount = 4;

    private Map<Speciality, EditText> map = new LinkedHashMap<>();
    private final List<Speciality> specialityList = DBManager.copyObjectFromRealm(
            DBManager.readAll(Speciality.class, ConstantApplication.ID));
    private Subject subject = new Subject();
    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.activity_add_subject);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_path_150));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        TextView textCont = (TextView)findViewById(R.id.toolbar_title);
        textCont.setText(R.string.activity_add_subject);

        colorPickButton = (Button) findViewById(R.id.pickColorBtn);
        GradientDrawable background = (GradientDrawable) colorPickButton.getBackground();
        background.setColor(Color.RED);
        subject.setColor(Color.RED);
        Log.d(TAG, Integer.toString(Color.RED));
        Log.d(TAG,"Subject default color"+ Integer.toString(subject.getColor()));
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

        LinearLayout parent = findViewById(R.id.spinnerSpeciality);
        ImageButton addButton = findViewById(R.id.create_subject);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                addSubject();
            }
        });

        for(int i = 0 ; i< specialityList.size();i++){
            LinearLayout specLayout = new LinearLayout(this);
            specLayout.setOrientation(LinearLayout.HORIZONTAL);
            specLayout.setWeightSum(10);

            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            specLayout.setLayoutParams(LLParams);
            final EditText hourEditTxt = new EditText(this);
            CheckBox checkSpecHour = new CheckBox(this);
            final Speciality finalSpeciality = specialityList.get(i);
            fillingCheckBox(checkSpecHour, finalSpeciality, hourEditTxt);

            LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,2);
            checkSpecHour.setLayoutParams(checkBoxParams);
            checkSpecHour.setWidth(0);
            checkSpecHour.setButtonTintList(getResources().getColorStateList(R.color.sideBar));

            TextView specUI = new TextView(this);
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,6);
            specUI.setWidth(0);
            specUI.setLayoutParams(textViewParams);
            specUI.setText(specialityList.get(i).getName());
            specUI.setTextColor(getResources().getColor(R.color.appDefaultBlack));
            specUI.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,2);
            hourEditTxt.setWidth(0);
            hourEditTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
            int maxLength = 3;
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            hourEditTxt.setFilters(fArray);
            hourEditTxt.setSingleLine(false);
            hourEditTxt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            hourEditTxt.setLayoutParams(etParams);
            hourEditTxt.setHint(R.string.enter_the_number_of_hours);
            hourEditTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            hourEditTxt.setEnabled(false);

            specLayout.addView(checkSpecHour);
            specLayout.addView(specUI);
            specLayout.addView(hourEditTxt);

            parent.addView(specLayout);

            subjectEditText = findViewById(R.id.editTextSubjectName);
        }

        spinnerCourse =
                ConstantApplication.fillingSpinner(currentContext, findViewById(R.id.spinnerCourse),
                        new ArrayList<>(Collections.singleton("Оберіть спеціальність")));
        spinnerCourse.setEnabled(false);
    }

    private void fillingCheckBox(CheckBox checkSpecHour, Speciality finalSpeciality, EditText hourEditTxt) {
        checkSpecHour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spinnerCourse.setEnabled(true);
                int currCountCourse = finalSpeciality.getCountCourse();
                if (isChecked) {
                    map.put(finalSpeciality, hourEditTxt);
                    if(currCountCourse<=minCourseCount){
                        minCourseCount = currCountCourse;
                    }
                } else {
                    map.remove(finalSpeciality);
                    currCountCourse = 4;
                    for (Speciality sp : map.keySet()){
                        if (sp.getCountCourse() <= currCountCourse){
                            currCountCourse = sp.getCountCourse();
                            break;
                        }
                    }
                    minCourseCount = currCountCourse;
                }
                if(map.size()!=0) {
                    spinnerCourse =
                            ConstantApplication.fillingSpinner(currentContext, findViewById(R.id.spinnerCourse),
                                    ConstantApplication.countCourse(minCourseCount));
                    listenerSpinnerCourse(spinnerCourse);
                } else {
                    spinnerCourse =
                            ConstantApplication.fillingSpinner(currentContext, findViewById(R.id.spinnerCourse),
                                    new ArrayList<>(Collections.singleton(getString(R.string.course_spinner_hint))));
                    spinnerCourse.setEnabled(false);
                    spinnerCourse.setOnItemSelectedListener(null);
                }



              /* // while (countCourse != 0 && ++countCourse <= 4) {
                    for (Speciality sp : map.keySet()){
                        if (countCourse >= sp.getCountCourse()){
                            spinnerCourse =
                                    ConstantApplication.fillingSpinner(currentContext, findViewById(R.id.spinnerCourse),
                                            ConstantApplication.countCourse(sp.getCountCourse()));
                            countCourse = sp.getCountCourse();
                            listenerSpinnerCourse(spinnerCourse);
                            break;
                        }
                    }
                //}*/

                hourEditTxt.setEnabled(isChecked);
                Log.d(TAG,"Filling out = " + map.toString());
            }

        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    private void checkSubject(String str){
        if (!ConstantApplication.checkUISubject(str)){
            subjectEditText.setError(getString(R.string.toast_check));
        }
    }
    private void addSubject(){
        String strSubject = subjectEditText.getText().toString();
        if (specialityList.isEmpty()){
            Toast.makeText(currentContext, R.string.toast_check_speciality_setting, Toast.LENGTH_LONG).show();
            checkSubject(strSubject);
            return;
        }
        if (map.isEmpty()){
            Toast.makeText(currentContext, R.string.toast_check_speciality_and_number_of_hours, Toast.LENGTH_LONG).show();
            checkSubject(strSubject);
            return;
        }

        for (EditText editTextHour : map.values()){
            if (!ConstantApplication.checkUISubjectHour(editTextHour.getText().toString())) {
                Toast.makeText(currentContext, R.string.toast_check_speciality_and_number_of_hours, Toast.LENGTH_LONG).show();
                checkSubject(strSubject);
                return;
            }
        }
        checkSubject(strSubject);

        Map<Speciality, Integer> resultMap = ConstantApplication.convertMapEditTextToMapInt(map);
        subject.setName(strSubject)
                .setSpecialityCountHourMap(resultMap);

        Intent intent = getIntent();
        intent.putExtra(String.valueOf(ConstantApplication.ACTIVITY_ADD), subject);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    public void openColorPicker(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, subject.getColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                subject.setColor(color);
                Log.d(TAG,"Subject changed color"+ Integer.toString(subject.getColor()));
                colorPickButton = (Button) findViewById(R.id.pickColorBtn);
                GradientDrawable background = (GradientDrawable) colorPickButton.getBackground();
                background.setColor(color);
                colorPickButton.setBackground(background);
            }
        });
        colorPicker.show();
    }

    private void listenerSpinnerCourse(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                int course = Integer.parseInt(item);
                subject.setNumberCourse(course);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}
