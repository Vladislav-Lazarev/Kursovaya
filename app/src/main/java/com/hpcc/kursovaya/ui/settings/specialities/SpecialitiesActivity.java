package com.hpcc.kursovaya.ui.settings.specialities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Speciality;


import java.util.ArrayList;


public class SpecialitiesActivity extends AppCompatActivity {
    private static final String TAG = "SpecialitiesActivity";
    FloatingActionButton addSpeciality;
    ListView specialityLSV;
    SpecialityListAdapter adapter;
    private View addSpecialityView;
    private View editSpecialityView;
    ArrayList<Speciality> specialitiesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_specialties);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        addSpeciality = findViewById(R.id.fab);
        addSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPrepareAddSpeciality();
            }
        });
        specialityLSV = findViewById(R.id.specialitiesLSV);
        Speciality specialityRPZ = new Speciality("РПЗ", 8);
        Speciality specialityDAUNY = new Speciality("КПЗ", 8);
        specialitiesList.add(specialityRPZ);
        specialitiesList.add(specialityDAUNY);

        adapter = new SpecialityListAdapter(this,R.layout.listview_item_specialties, specialitiesList);
        specialityLSV.setAdapter(adapter);
        specialityLSV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Speciality entry = (Speciality) parent.getItemAtPosition(position);
                onClickPrepareEditSpeciality(entry, position);
            }
        });
    }

    private void onClickPrepareEditSpeciality(final Speciality entry, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_edit_speciality);
        builder.setPositiveButton(R.string.popup_edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickAcceptEditGroup(dialog, which, entry, position);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        editSpecialityView = getLayoutInflater().inflate(R.layout.dialog_speciality, null);
        builder.setView(editSpecialityView);
        EditText specText = editSpecialityView.findViewById(R.id.speciality_name_text);
        specText.setText(entry.getName());
        Spinner courseSpinner = editSpecialityView.findViewById(R.id.courseSpinner);
        //please fix speciality entity!! coursecount is 0!
        courseSpinner.setSelection((entry.getCountCourse()/2)-1);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void onClickAcceptEditGroup(DialogInterface dialog, int which, Speciality entry, int position) {
        EditText specText = editSpecialityView.findViewById(R.id.speciality_name_text);
        Spinner courseSpinner = editSpecialityView.findViewById(R.id.courseSpinner);
        int countCourse = Integer.parseInt(courseSpinner.getSelectedItem().toString())*2;
        entry.setName(specText.getText().toString());
        entry.setCountCourse(countCourse);
        specialitiesList.set(position,entry);
        adapter.notifyDataSetChanged();
    }

    private void onClickPrepareAddSpeciality() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_add_speciality);
        builder.setPositiveButton(R.string.dialog_button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickAcceptAddGroup(dialog, which);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        addSpecialityView = getLayoutInflater().inflate(R.layout.dialog_speciality, null);
        builder.setView(addSpecialityView);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void onClickAcceptAddGroup(DialogInterface dialog, int which) {
        //не забудьте про добавление сущности в БД, ребзя
        EditText specText = addSpecialityView.findViewById(R.id.speciality_name_text);
        Spinner courseSpinner = addSpecialityView.findViewById(R.id.courseSpinner);
        int countCourse = Integer.parseInt(courseSpinner.getSelectedItem().toString())*2;
        Speciality newSpeciality = new Speciality(specText.getText().toString(),countCourse*2);
        specialitiesList.add(newSpeciality);
        adapter.notifyDataSetChanged();
    }
}
