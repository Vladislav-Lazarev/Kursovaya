package com.hpcc.kursovaya.ui.subjects;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Discipline;
import com.hpcc.kursovaya.dao.entity.Semester;
import com.hpcc.kursovaya.dao.entity.Specialty;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class SubjectsFragment extends Fragment {
    boolean isCreatedAlready = false;
    private View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(!isCreatedAlready) {
            root = inflater.inflate(R.layout.fragment_subjects, container, false);
            ListView listView = root.findViewById(R.id.subjectsLSV);
            FloatingActionButton button = root.findViewById(R.id.fab);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddSubjectActivity.class);
                    startActivity(intent);
                }
            });


            Specialty specialtyRPZ = new Specialty("РПЗ", 8);

            Discipline clarnet = new Discipline( "Уроки гри на кларнеті", 92, specialtyRPZ,
                    new RealmList<Semester>(new Semester(7, null, null)), 0);

            Discipline web = new Discipline( "В гостях у Гордон", 78, specialtyRPZ,
                    new RealmList<Semester>(new Semester(7, null, null)), 0);

            Discipline metro = new Discipline( "Метро", 97, specialtyRPZ,
                    new RealmList<Semester>(new Semester(7, null, null)), 0);

            List<Discipline> subjects = new ArrayList<>();
            subjects.add(clarnet);
            subjects.add(web);
            subjects.add(metro);

            SubjectListAdapter adapter = new SubjectListAdapter(getActivity(), R.layout.list_view_item_subject, subjects);
            listView.setAdapter(adapter);
            isCreatedAlready=true;
        }
        setActionBarTitle();
        return root;
    }
    public void setActionBarTitle(){
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.menu_subjects));
        ((MainActivity) getActivity()).showOverflowMenu(false);
    }
}