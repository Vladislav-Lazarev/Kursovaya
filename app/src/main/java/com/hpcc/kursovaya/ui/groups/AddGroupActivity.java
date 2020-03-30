package com.hpcc.kursovaya.ui.groups;

import android.app.Activity;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;
import com.hpcc.kursovaya.ui.subjects.AddSubjectActivity;

public class AddGroupActivity extends AppCompatActivity {
    private static final String TAG = AddSubjectActivity.class.getSimpleName();
    private Group group = new Group();
    private EditText groupEditText;
    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_path_150));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        TextView textCont = (TextView)findViewById(R.id.toolbar_title);
        textCont.setText("Додавання групи");

        ImageButton addButton = findViewById(R.id.create_group);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                addGroup();
            }
        });

        Spinner spinnerSpeciality =
                ConstantEntity.fillingSpinner(this, findViewById(R.id.spinnerSpeciality),
                        new Speciality().entityToNameList());
        listenerSpinnerSpeciality(spinnerSpeciality);

        Spinner spinnerCourse = (Spinner) findViewById(R.id.spinnerCourse);
        listenerSpinnerCourse(spinnerCourse);

        groupEditText = findViewById(R.id.editTextGroupName);
    }

    private void addGroup(){
        group.setName(groupEditText.getText().toString());

        if (group.createEntity()){
            Intent intent = getIntent();
            intent.putExtra("addGroup", group);
            setResult(Activity.RESULT_OK, intent);
        } else {
            // Оповещение о ее неправильности
            Log.d(TAG, "addGroup = " + group);
        }
        finish();
    }

    private void listenerSpinnerSpeciality(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                Speciality speciality = DBManager.read(Speciality.class, ConstantEntity.NAME, item);
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
