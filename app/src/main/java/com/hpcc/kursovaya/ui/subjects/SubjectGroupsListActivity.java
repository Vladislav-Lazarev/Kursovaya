package com.hpcc.kursovaya.ui.subjects;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.SubjectGroupsInfo;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class SubjectGroupsListActivity extends AppCompatActivity {
    private static final String TAG = SubjectGroupsListActivity.class.getSimpleName();
    ListView subjectGroupsLSV;
    SubjectGroupsAdapter adapter;
    List<SubjectGroupsInfo> subjectGroupsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.activity_subject_groups_list);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        Subject subject = intent.getParcelableExtra("entry");
        TextView header = toolbar.findViewById(R.id.toolbar_title);
        header.setText(header.getText() +" "+ subject.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        subjectGroupsLSV = findViewById(R.id.groupsLSV);
        List<AcademicHour> academicHours = new ArrayList<>();
        List<Group> groups = new ArrayList<>();
        for(AcademicHour academicHour : DBManager.readAll(AcademicHour.class)){
            TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
            if(templateAcademicHour!=null){
                Group groupHour = templateAcademicHour.getGroup();
                Subject subjectHour = templateAcademicHour.getSubject();
                if(groupHour!=null && subjectHour!=null && subject.equals(subjectHour)){
                    groups.add(groupHour);
                    academicHours.add(academicHour);
                }
            }
        }
        groups = new ArrayList<>(new LinkedHashSet<>(groups));
        Map<Group,List<AcademicHour>> academicHoursOfGroup = new HashMap<>();
        for(Group group : groups){
            List<AcademicHour> groupAcademicHours = new ArrayList<>();
            for(AcademicHour academicHour : academicHours){
                TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                if(templateAcademicHour.getGroup()!=null && group.equals(templateAcademicHour.getGroup())){
                    groupAcademicHours.add(academicHour);
                }
            }
            academicHoursOfGroup.put(group,groupAcademicHours);
        }

        for(Group group : groups){
            subjectGroupsList.add(group.toSubjectGroupsInfo(subject,academicHoursOfGroup.get(group)));
        }


        adapter = new SubjectGroupsAdapter(this,R.layout.listview_item_subject_groups,subjectGroupsList);
        subjectGroupsLSV.setAdapter(adapter);

        subjectGroupsLSV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        subjectGroupsLSV.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = subjectGroupsLSV.getCheckedItemCount();
                mode.setTitle(checkedCount +" "+getResources().getString(R.string.cab_select_text));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                toolbar.setVisibility(View.GONE);
                mode.getMenuInflater().inflate(R.menu.activity_listview, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        prepareDeleteDialog(mode);
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                toolbar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }


    private void prepareDeleteDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.popup_delete_group_title);
        builder.setMessage(R.string.popup_delete_subject_groups_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SparseBooleanArray positionDel = subjectGroupsLSV.getCheckedItemPositions();
                for (int i = 0; i < positionDel.size(); i++) {
                    int key = positionDel.keyAt(i);
                    if (positionDel.get(key)){
                        Log.d(TAG, "entity = " + subjectGroupsList.get(key));
                        adapter.delete(subjectGroupsList.get(key));
                    }
                }
              //  adapter.update(ConstantApplication.NAME);
/*
                if (positionDel.size() == ConstantApplication.ONE){
                    Toast.makeText(this, R.string.toast_del_entity, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.toast_del_many_entity, Toast.LENGTH_SHORT).show();
                }*/

                mode.finish();
            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mode.finish();
            }
        });
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
}
