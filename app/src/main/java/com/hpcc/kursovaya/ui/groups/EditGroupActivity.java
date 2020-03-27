package com.hpcc.kursovaya.ui.groups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class EditGroupActivity extends AppCompatActivity {
    private static final String TAG = EditGroupActivity.class.getSimpleName();
    private Group group = new Group();
    private EditText groupEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_path_150));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        TextView textCont = (TextView)findViewById(R.id.toolbar_title);
        textCont.setText("Редагування групи");

        Intent intent = getIntent();
        group = intent.getParcelableExtra("editGroup");

        Spinner spinnerSpeciality =
                ConstantEntity.fillingSpinner(this, (Spinner) findViewById(R.id.spinnerSpeciality),
                        new Speciality().entityToNameList());
        listenerSpinnerSpeciality(spinnerSpeciality);
        ConstantEntity.setSpinnerText(spinnerSpeciality, group.getSpecialty().getName());

        Spinner spinnerCourse = findViewById(R.id.spinnerCourse);
        listenerSpinnerCourse(spinnerCourse);
        spinnerCourse.setSelection(group.getNumberCourse() - ConstantEntity.ONE);

        groupEditText = findViewById(R.id.editTextGroupName);
        groupEditText.setText(group.getName());

        ImageButton editButton = findViewById(R.id.edit_group);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editGroup();
            }
        });
    }

    private void editGroup(){
        group.setName(groupEditText.getText().toString());

        if (group.isEntity()){
            Intent intent = getIntent();
            intent.putExtra("editGroup", group);
            setResult(Activity.RESULT_OK, intent);
        } else {
            // Оповещение о ее неправильности
            Log.d(TAG, "editGroup = " + group);
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
                group.setNumberCourse(Integer.parseInt(item));
                Log.d("listenerSpinnerCourse", item);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
