package com.hpcc.kursovaya.ui.hourChecker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.ui.hourChecker.model.SubjectModel;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.CustomViewPager;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.DayViewFragment;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.util.ArrayList;
import java.util.List;

public class GroupSubjectCheckActivity extends AppCompatActivity {
    private static final String TAG = GroupSubjectCheckActivity.class.getSimpleName();
    protected static List<AcademicHour> readHours;
    protected static List<AcademicHour> canceledHours;
    protected static List<AcademicHour> unreadHours;
    private GroupSubjectTabViewPager adapter;
    private  TabLayout tabLayout;
    protected SubjectModel model;
    protected Group group;
    private CustomViewPager viewPager;
    Toolbar toolbar;


    public static void setReadHoursFromAgregator(List<DayViewFragment.EventAgregator> hours) {
        List<AcademicHour> academicHours = new ArrayList<>();
        for(DayViewFragment.EventAgregator eventAgregator : hours){
            if(eventAgregator!=null && eventAgregator.academicHour!=null){
                academicHours.add(eventAgregator.academicHour);
            }
        }
        readHours = academicHours;
    }

    public static void setUnReadHoursFromAgregator(List<DayViewFragment.EventAgregator> hours) {
        List<AcademicHour> academicHours = new ArrayList<>();
        for(DayViewFragment.EventAgregator eventAgregator : hours){
            if(eventAgregator!=null && eventAgregator.academicHour!=null){
                academicHours.add(eventAgregator.academicHour);
            }
        }
        unreadHours = academicHours;
    }

    public static void setCancelledHoursFromAgregator(List<DayViewFragment.EventAgregator> hours) {
        List<AcademicHour> academicHours = new ArrayList<>();
        for(DayViewFragment.EventAgregator eventAgregator : hours){
            if(eventAgregator!=null && eventAgregator.academicHour!=null){
                academicHours.add(eventAgregator.academicHour);
            }
        }
        canceledHours = academicHours;
    }

    public static void setReadHours(List<AcademicHour> readHours) {
        GroupSubjectCheckActivity.readHours = readHours;
    }

    public static void setCanceledHours(List<AcademicHour> canceledHours) {
        GroupSubjectCheckActivity.canceledHours = canceledHours;
    }

    public static void setUnreadHours(List<AcademicHour> unreadHours) {
        GroupSubjectCheckActivity.unreadHours = unreadHours;
    }

    public static List<AcademicHour> getReadHours() {
        return readHours;
    }

    public static List<AcademicHour> getCanceledHours() {
        return canceledHours;
    }

    public static List<AcademicHour> getUnreadHours() {
        return unreadHours;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.activity_group_subject_check);
        toolbar = findViewById(R.id.toolbar);
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
        viewPager = findViewById(R.id.viewPager);
        adapter = new GroupSubjectTabViewPager((getSupportFragmentManager()), GroupSubjectCheckActivity.this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                FragmentPagerAdapter a = (FragmentPagerAdapter) viewPager.getAdapter();
                GroupSubjectTabFragment fragment = (GroupSubjectTabFragment) a.instantiateItem(viewPager,position);
                List<DayViewFragment.EventAgregator> events = new ArrayList<>();
                List<AcademicHour> hours= null;
                switch (position){
                    case 0:
                        hours = unreadHours;
                        break;
                    case 1:
                        hours = readHours;
                        break;
                    case 2:
                        hours = canceledHours;
                        break;
                }
                for(AcademicHour academicHour: hours){
                    DayViewFragment.EventAgregator eventAgregator = new DayViewFragment.EventAgregator();
                    eventAgregator.anotherEvent=null;
                    eventAgregator.academicHour=academicHour;
                    events.add(eventAgregator);
                }
                fragment.setEvents(events);
                fragment.updateAdapter();

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabLayout = findViewById(R.id.sliding_tabs);
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

        Log.d(TAG,"ttt");
    }

    Toolbar getToolbar() {
        return toolbar;
    }

    public CustomViewPager getViewPager() {
        return viewPager;
    }
}
