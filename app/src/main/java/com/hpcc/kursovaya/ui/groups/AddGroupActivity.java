package com.hpcc.kursovaya.ui.groups;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.hpcc.kursovaya.ui.subjects.AddSubjectActivity;

public class AddGroupActivity extends AppCompatActivity {
    private static final String TAG = AddSubjectActivity.class.getSimpleName();
    private final Context currentContext = this;

    private Group group = new Group();
    private EditText groupEditText;
    private Spinner spinnerSpeciality;
    private long mLastClickTime = 0;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.activity_add_group);

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
        textCont.setText(R.string.activity_add_group);

        ImageButton addButton = findViewById(R.id.create_group);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                addGroup();
            }
        });

        spinnerSpeciality =
                ConstantApplication.fillingSpinner(currentContext, findViewById(R.id.spinnerSpeciality),
                        new Speciality().entityToNameList());
        listenerSpinnerSpeciality(spinnerSpeciality);

        groupEditText = findViewById(R.id.editTextGroupName);
    }

    private void addGroup(){
        String strGroup = groupEditText.getText().toString().trim();

        if (spinnerSpeciality.getCount() == ConstantApplication.ZERO){
            Toast.makeText(this, R.string.toast_fragment_no_specialities, Toast.LENGTH_LONG).show();
            return;
        }

        if (!ConstantApplication.checkUI(ConstantApplication.PATTERN_GROUP, strGroup)){
            groupEditText.setError(getString(R.string.toast_check));
            return;
        }

        group.setName(strGroup.toLowerCase());

        Intent intent = getIntent();
        intent.putExtra(String.valueOf(ConstantApplication.ACTIVITY_ADD), group);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    private void listenerSpinnerSpeciality(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                Speciality speciality = DBManager.read(Speciality.class, ConstantApplication.NAME, item);

                Spinner spinnerCourse =
                        ConstantApplication.fillingSpinner(currentContext, findViewById(R.id.spinnerCourse),
                                ConstantApplication.countCourse(speciality.getCountCourse()));
                listenerSpinnerCourse(spinnerCourse);
                ConstantApplication.setSpinnerText(spinnerCourse, String.valueOf(group.getNumberCourse()));

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
                int course = Integer.parseInt(item);
                group.setNumberCourse(course);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
