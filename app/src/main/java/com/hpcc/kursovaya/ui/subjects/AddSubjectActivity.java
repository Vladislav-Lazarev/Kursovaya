package com.hpcc.kursovaya.ui.subjects;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import yuku.ambilwarna.AmbilWarnaDialog;

public class AddSubjectActivity extends AppCompatActivity {
    int pickDefaultColor;
    Button colorPickButton;

    private Subject subjec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_subject);

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

        TextView textCont = (TextView)findViewById(R.id.toolbar_title);
        textCont.setText("Додавання предмету");

        ImageButton addButton = findViewById(R.id.create_subject);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubject();
            }
        });

        colorPickButton = (Button) findViewById(R.id.pickColorBtn);
        colorPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
        pickDefaultColor = getResources().getColor((R.color.sideBar));

    }

    private void addSubject(){
        finish();
    }

    private List<String> readSpecialityList(){
        RealmResults<Speciality> specialityList = DBManager.readAll(Speciality.class);
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

                /*Speciality speciality = DBManager.read(Speciality.class, ConstantEntity.NAME, item);
                List<PairSpecialityCountHours> pairList = DBManager.readAll(PairSpecialityCountHours.class);

                PairSpecialityCountHours pair = null;
                for (PairSpecialityCountHours valPair : pairList) {
                    if (pair.equals(speciality)){
                        pair = valPair;
                        break;
                    }
                }

                subjec.setPairSpecialityCountHoursList(new RealmList<>(pair));*/
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
                subjec.setCourse(Integer.parseInt(item));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void openColorPicker(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, pickDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                pickDefaultColor = color;
                colorPickButton = (Button) findViewById(R.id.pickColorBtn);
                GradientDrawable background = (GradientDrawable) colorPickButton.getBackground();
                background.setColor(color);
                colorPickButton.setBackground(background);
            }
        });
        colorPicker.show();
    }
}
