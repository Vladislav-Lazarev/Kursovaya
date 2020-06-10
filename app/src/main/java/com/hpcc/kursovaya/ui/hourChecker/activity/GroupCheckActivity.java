package com.hpcc.kursovaya.ui.hourChecker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.hourChecker.adapter.GroupAdapter;
import com.hpcc.kursovaya.ui.hourChecker.model.GroupModel;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class GroupCheckActivity extends AppCompatActivity implements GroupAdapter.ItemClickListener {

    List<GroupModel> groups = new ArrayList<>();
    GroupAdapter adapter;
    List<AcademicHour> academicHours;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.activity_group_check);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v->finish());
        RecyclerView recyclerView = findViewById(R.id.groupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupAdapter(recyclerView,this,groups);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        academicHours = DBManager.copyObjectFromRealm(DBManager.readAll(AcademicHour.class));
        final List<TemplateAcademicHour> templateAcademicHours = new ArrayList<>();
        List<Group> allGroups = new ArrayList<>();
        List<Speciality> allSpecialites = new ArrayList<>();
        List<Subject> allSubjects = new ArrayList<>();
        for(AcademicHour academicHour:academicHours){
            TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
            if(templateAcademicHour!=null){
                allGroups.add(templateAcademicHour.getGroup());
                allSpecialites.add(templateAcademicHour.getGroup().getSpecialty());
                allSubjects.add(templateAcademicHour.getSubject());
            }
            templateAcademicHours.add(templateAcademicHour);
        }
        allGroups = new ArrayList<>(new LinkedHashSet<>(allGroups));
        allSpecialites = new ArrayList<>(new LinkedHashSet<>(allSpecialites));
        allSubjects = new ArrayList<>(new LinkedHashSet<>(allSubjects));
        List<Group> finalAllGroups = allGroups;
        List<Speciality> finalAllSpecialitites = allSpecialites;
        List<Subject> finalAllSubjects = allSubjects;
        final Map<Integer,Map<Integer,Integer>> countHour = new HashMap<Integer,Map<Integer,Integer>>();
        for(Subject subject : finalAllSubjects){
            Map<Integer,Integer> specToHour = new HashMap<>();
            for(Map.Entry<Speciality,Integer> entry: subject.getSpecialityCountHourMap().entrySet()){
                int entryID = entry.getKey().getId();
                int hours = entry.getValue();
                specToHour.put(entryID,hours);
            }
            countHour.put(subject.getId(),specToHour);
        }
        adapter.setLoadMore(()->{
            if(groups.size() < finalAllGroups.size()){
                //groups.add(null);
                adapter.notifyItemInserted(groups.size()-1);
                List<Thread> threads = new ArrayList<>();
                for(Group group:finalAllGroups){
                    Thread t = new Thread(){
                        @Override
                        public void run() {
                            boolean someThingWrong = false;
                            for(Subject subject:finalAllSubjects){
                                Integer planHours = countHour.get(subject.getId()).get(group.getIdSpeciality());
                                if(planHours!=null) {
                                    List<AcademicHour> allClasses = new ArrayList<>();
                                    someThingWrong = false;
                                    for (int i = 0; i < academicHours.size(); i++) {
                                        TemplateAcademicHour templateAcademicHour = templateAcademicHours.get(i);
                                        if (templateAcademicHour.getIdSubject() == subject.getId() && templateAcademicHour.getIdGroup() == group.getId()) {
                                            if(!academicHours.get(i).hasCanceled()) {
                                                allClasses.add(academicHours.get(i));
                                            } else {
                                                --planHours;
                                            }
                                            if (allClasses.size() > planHours) {
                                                someThingWrong = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (allClasses.size() < planHours) {
                                        someThingWrong = true;
                                    }
                                    if (someThingWrong) {
                                        break;
                                    }
                                }
                            }
                            GroupModel model = new GroupModel(group,true);
                            if(someThingWrong){
                                model = new GroupModel(group,false);
                            }
                            groups.add(model);
                        }
                    };
                    t.start();
                    threads.add(t);
                }
                for(Thread t: threads){
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                adapter.setLoaded();
                adapter.notifyDataSetChanged();
            }

        });
        adapter.loadItems();
    }

    public Subject getSubjectById(int id, List<Subject> subjects){
        Subject returnValue = null;
        for(Subject subject : subjects){
            if(subject.getId()==id){
                return subject;
            }
        }
        return  returnValue;
    }

    public Speciality getSpecialityById(int id, List<Speciality> specialities){
        Speciality returnValue = null;
        for(Speciality speciality : specialities){
            if(speciality.getId()==id){
                return speciality;
            }
        }
        return  returnValue;
    }

    public Group getGroupById(int id, List<Group> groups){
        Group returnValue = null;
        for(Group group : groups){
            if(group.getId()==id){
                return group;
            }
        }
        return  returnValue;
    }

    @Override
    public void onItemClick(View view, int position) {
        Group model = groups.get(position).getGroup();
        Intent intent = new Intent(GroupCheckActivity.this,SubjectCheckActivity.class);
        intent.putExtra("group",model);
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(View view, int position) {

    }
}
