package com.hpcc.kursovaya.ui.groups;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

public class EditGroupActivity extends AppCompatActivity {
    private static final String TAG = EditGroupActivity.class.getSimpleName();
    private final Context currentContext = this;

    private Group group = new Group();
    private EditText groupEditText;
    private long mLastClickTime = 0;
    Spinner spinnerCourse;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.activity_edit_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        textCont.setText(R.string.activity_edit_group);

        Intent intent = getIntent();
        group = intent.getParcelableExtra(String.valueOf(ConstantApplication.ACTIVITY_EDIT));

        Spinner spinnerSpeciality =
                ConstantApplication.fillingSpinner(this, (Spinner) findViewById(R.id.spinnerSpeciality),
                        new Speciality().entityToNameList());
        listenerSpinnerSpeciality(spinnerSpeciality);
        ConstantApplication.setSpinnerText(spinnerSpeciality, group.getSpecialty().getName());

        spinnerCourse = findViewById(R.id.spinnerCourse);
        listenerSpinnerCourse(spinnerCourse);
        spinnerCourse.setSelection(group.getNumberCourse() - ConstantApplication.ONE);

        groupEditText = findViewById(R.id.editTextGroupName);
        groupEditText.setText(group.getName());

        ImageButton editButton = findViewById(R.id.edit_group);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                editGroup();
            }
        });
    }

    private void editGroup(){
        String strGroup = groupEditText.getText().toString();
        if (!ConstantApplication.checkUISpeciality(strGroup)){
            Toast.makeText(this, R.string.toast_check_speciality_setting, Toast.LENGTH_LONG).show();
            return;
        }
        if (!ConstantApplication.checkUIGroup(strGroup)){
            groupEditText.setError(getString(R.string.toast_check_name));
            return;
        }

        group.setName(strGroup);

        Intent intent = getIntent();
        intent.putExtra(String.valueOf(ConstantApplication.ACTIVITY_EDIT), group);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    private void listenerSpinnerSpeciality(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                Speciality speciality = DBManager.read(Speciality.class, ConstantApplication.NAME, item);

                spinnerCourse =
                        ConstantApplication.fillingSpinner(currentContext, findViewById(R.id.spinnerCourse),
                                ConstantApplication.countCourse(speciality.getCountCourse()));
                listenerSpinnerCourse(spinnerCourse);

                group.setSpecialty(speciality);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void listenerSpinnerCourse(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                group.setNumberCourse(Integer.parseInt(item));
                Log.d("listenerSpinnerCourse", item);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
