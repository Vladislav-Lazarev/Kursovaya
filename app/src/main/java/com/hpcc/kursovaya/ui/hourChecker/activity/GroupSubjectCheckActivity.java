package com.hpcc.kursovaya.ui.hourChecker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.ui.hourChecker.model.SubjectModel;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.util.List;

public class GroupSubjectCheckActivity extends AppCompatActivity {
    protected static List<AcademicHour> readHours;
    protected static List<AcademicHour> canceledHours;
    protected static List<AcademicHour> unreadHours;

    protected SubjectModel model;
    protected Group group;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.activity_group_subject_check);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v->finish());
        Intent intent = getIntent();
        model = intent.getParcelableExtra("model");
        group = intent.getParcelableExtra("group");
        String titleStr = new String(group.getName()+":");
        titleStr += model.getSubject().getName();
        title.setText(titleStr);
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(
                new GroupSubjectTabViewPager((getSupportFragmentManager()),GroupSubjectCheckActivity.this));
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        String[] tabTitles ;
        tabTitles = new String[] {getApplicationContext().getResources().getString(R.string.tab_unread),
                getApplicationContext().getString(R.string.tab_read),
                getApplicationContext().getString(R.string.tab_canceled)};
        for(int i=0; i<tabLayout.getTabCount();i++){
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_textview_tab_layout,null);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setText(tabTitles[i]);
            tabLayout.getTabAt(i).setCustomView(tv);
        }
        readHours = model.getReadList();
        canceledHours = model.getCanceledHours();
        unreadHours = model.getUnreadHours();
    }
}
