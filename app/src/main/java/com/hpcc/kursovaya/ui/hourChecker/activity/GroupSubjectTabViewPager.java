package com.hpcc.kursovaya.ui.hourChecker.activity;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hpcc.kursovaya.ui.schedule.DayViewPager.DayViewFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupSubjectTabViewPager extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;



    public GroupSubjectTabViewPager(@NonNull FragmentManager fm, Context context) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return GroupSubjectTabFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
