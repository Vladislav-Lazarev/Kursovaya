package com.hpcc.kursovaya.ui.groups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity {
    private Group group = new Group();
    private EditText groupEditText;

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
                //What to do on back clicked
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
                addGroup();
            }
        });

        Spinner spinnerSpeciality = (Spinner) findViewById(R.id.spinnerSpeciality);
        fillingSpinnerSpeciality(spinnerSpeciality);
        listenerSpinnerSpeciality(spinnerSpeciality);

        Spinner spinnerCourse = (Spinner) findViewById(R.id.spinnerCourse);
        listenerSpinnerCourse(spinnerCourse);

        groupEditText = findViewById(R.id.editTextGroupName);
    }

    private void addGroup(){
        //adding group logic
        group.setName(groupEditText.getText().toString());

        Group newGroup = new Group(group.getName(), group.getSpecialty(), group.getNumberCourse());
        DBManager.write(newGroup);

        Intent intent = getIntent();
        intent.putExtra("newGroup", newGroup);
        setResult(Activity.RESULT_OK, intent);
        int result = DBManager.write(newGroup);
        Log.i("addGroup", "write result = " + result);

        finish();
    }

    private List<String> readSpecialityList(){
        List<Speciality> specialityList = DBManager.readAll(Speciality.class);
        List<String> strSpecialityList = new ArrayList<>();

        for (Speciality speciality : specialityList){
            strSpecialityList.add(speciality.getName());
        }

        return strSpecialityList;
    }
    private void fillingSpinnerSpeciality(Spinner spinner) {
        spinner = (Spinner) findViewById(R.id.spinnerSpeciality);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, readSpecialityList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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

                Log.d("listenerSpinnerCourse", item);
                group.setNumberCourse(Integer.parseInt(item));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
