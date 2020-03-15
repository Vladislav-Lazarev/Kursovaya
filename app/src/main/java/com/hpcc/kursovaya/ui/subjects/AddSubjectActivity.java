package com.hpcc.kursovaya.ui.subjects;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.Objects;

import io.realm.RealmResults;
import yuku.ambilwarna.AmbilWarnaDialog;

public class AddSubjectActivity extends AppCompatActivity {
    int pickDefaultColor;
    Button colorPickButton;
    static class SpecialityWHours{
        Speciality speciality;
        EditText hourEdtxt;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SpecialityWHours that = (SpecialityWHours) o;
            return Objects.equals(speciality, that.speciality) &&
                    Objects.equals(hourEdtxt, that.hourEdtxt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(speciality, hourEdtxt);
        }
    }

    private ArrayList<SpecialityWHours> specialityList = new ArrayList<>();

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

        LinearLayout parent = findViewById(R.id.spinnerSpeciality);

        final ArrayList<Speciality> specialities = new ArrayList<>();
        Speciality rpz = new Speciality("РПЗ", 4);
        Speciality rpn = new Speciality("Дело Влада", 4);
        Speciality ghost = new Speciality("Инженерия призрачного дела", 4);
        Speciality ghost2 = new Speciality("Инженерия непризрачного дела", 4);
        specialities.add(rpz);
        specialities.add(rpn);
        specialities.add(ghost);
        specialities.add(ghost2);
        for(int i = 0 ; i< specialities.size();i++){
            LinearLayout specLayout = new LinearLayout(this);
            specLayout.setOrientation(LinearLayout.HORIZONTAL);
            specLayout.setWeightSum(10);

            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            specLayout.setLayoutParams(LLParams);
            final EditText et1 = new EditText(this);
            CheckBox check1 = new CheckBox(this);
            final Context cont = this;
            final int getPos = i;
            check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SpecialityWHours object = new SpecialityWHours();
                    object.hourEdtxt = et1;
                    object.speciality = specialities.get(getPos);
                    if (isChecked){
                        if(!specialityList.contains(object)) {
                            specialityList.add(object);
                            Log.d("AddSubjectActivity",object.speciality.getName()+" "+object.hourEdtxt.getText().toString());
                            Log.d("AddSubjectActivity",Integer.toString(specialityList.size()));
                        }
                        et1.setEnabled(true);
                    } else{
                        if(specialityList.contains(object)){
                            specialityList.remove(object);
                            Log.d("AddSubjectActivity",object.speciality.getName()+" "+object.hourEdtxt.getText().toString());
                            Log.d("AddSubjectActivity",Integer.toString(specialityList.size()));
                        }
                        et1.setEnabled(false);
                    }
                }
            });
            LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,2);
            check1.setLayoutParams(checkBoxParams);
            check1.setWidth(0);
            check1.setButtonTintList(getResources().getColorStateList(R.color.sideBar));
            TextView spec1 = new TextView(this);
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,6);
            spec1.setWidth(0);
            spec1.setLayoutParams(textViewParams);
            spec1.setText(specialities.get(i).getName());
            spec1.setTextColor(getResources().getColor(R.color.appDefaultBlack));
            spec1.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,2);
            et1.setWidth(0);
            et1.setLayoutParams(etParams);
            et1.setHint("Введіть кількість годин");
            et1.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            et1.setEnabled(false);

            specLayout.addView(check1);
            specLayout.addView(spec1);
            specLayout.addView(et1);

            parent.addView(specLayout);

        }


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
