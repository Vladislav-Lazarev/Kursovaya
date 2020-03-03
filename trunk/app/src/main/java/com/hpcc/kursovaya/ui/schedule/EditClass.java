package com.hpcc.kursovaya.ui.schedule;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hpcc.kursovaya.R;

public class EditClass extends AppCompatActivity {
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
        setContentView(R.layout.activity_edit_class);
        groupName = findViewById(R.id.groupNameSuggestET);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_path_150));
        //here place for getting classDay, classHour and Group\Subject entities
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                setResult(1);
                finish();
            }
        });
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        Button btnAdd = findViewById(R.id.edit_class);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClass();
            }
        });
        TextView textCont = (TextView)findViewById(R.id.toolbar_title);
        textCont.setText(R.string.edit_class);
    }
    /*
    TestMethod
    Needs correction in future
     */
    private void editClass() {
        String groupNameStr = groupName.getText().toString();
        Intent intent = getIntent();
        intent.putExtra("groupName",groupNameStr);
        setResult(0,intent);
        finish();
    }
}