package com.hpcc.kursovaya.ui.hourChecker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.hourChecker.adapter.SubjectAdapter;
import com.hpcc.kursovaya.ui.hourChecker.model.SubjectModel;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class SubjectCheckActivity extends AppCompatActivity implements SubjectAdapter.ItemClickListener {
    private static final String TAG = SubjectCheckActivity.class.getSimpleName();
    Group group;
    List<SubjectModel> subjects;
    SubjectAdapter adapter;
    List<AcademicHour> academicHours;
    List<AcademicHour> academicHoursOfGroups;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.activity_subject_check);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v->finish());
        Intent intent = getIntent();
        group = intent.getParcelableExtra("group");
        title.setText(title.getText().toString()+" "+group.getName());
        academicHours = DBManager.copyObjectFromRealm(DBManager.readAll(AcademicHour.class));
        RecyclerView recyclerView = findViewById(R.id.subjectList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjects = new ArrayList<>();
        adapter = new SubjectAdapter(SubjectCheckActivity.this,subjects);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.subjects.size()==0){
            List<Subject> subjects = new ArrayList<>();
            academicHoursOfGroups = new ArrayList<>();
            Map<AcademicHour,Integer> subjectIDByHour = new HashMap<>();
            for(AcademicHour academicHour: academicHours){
                TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                if(templateAcademicHour.getGroup().equals(group)){
                    academicHoursOfGroups.add(academicHour);
                    subjectIDByHour.put(academicHour,academicHour.getTemplateAcademicHour().getSubject().getId());
                    subjects.add(templateAcademicHour.getSubject());
                }
            }
            subjects = new ArrayList<>(new LinkedHashSet<>(subjects));

            for(int i=0; i<subjects.size();i++){
                subjects.get(i).initMap();
                final int planHours = subjects.get(i).getSpecialityCountHour(group.getSpecialty());
                final List<AcademicHour> readList = new ArrayList<>();
                final List<AcademicHour> canceledList = new ArrayList<>();
                final List<AcademicHour> unreadHours = new ArrayList<>();
                List<Subject> finalSubjects = subjects;
                int finalI = i;
                runOnUiThread(()->{
                    for(AcademicHour academicHour: academicHoursOfGroups){
                        int currentSubjectId = subjectIDByHour.get(academicHour);
                        if(academicHour.hasCompleted() && !academicHour.hasCanceled() && currentSubjectId ==finalSubjects.get(finalI).getId()){
                            readList.add(academicHour);
                        } else if(academicHour.hasCanceled() && currentSubjectId ==finalSubjects.get(finalI).getId()){
                            canceledList.add(academicHour);
                        } else if(currentSubjectId ==finalSubjects.get(finalI).getId()){
                            unreadHours.add(academicHour);
                        }
                    }
                    SubjectModel subjectModel = new SubjectModel(finalSubjects.get(finalI),planHours,readList,canceledList,unreadHours);
                    this.subjects.add(subjectModel);
                    adapter.notifyDataSetChanged();
                });
               // t.start();
            }
        } else {
            subjects.clear();
            academicHours = DBManager.copyObjectFromRealm(DBManager.readAll(AcademicHour.class));
            onResume();
        }

        Log.d(TAG,"stop");
    }

    @Override
    public void onItemClick(View view, int position) {
        SubjectModel model = subjects.get(position);
        Intent intent = new Intent(SubjectCheckActivity.this,GroupSubjectCheckActivity.class);
        intent.putExtra("model",model);
        intent.putExtra("group",group);
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(View view, int position) {

    }
}
