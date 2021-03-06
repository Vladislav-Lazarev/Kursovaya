package com.hpcc.kursovaya.ui.schedule.WeekViewPager;

import android.util.Log;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class WeekViewPagerAdapter extends FragmentStatePagerAdapter {
    private int pages_count = 3536;

    public WeekViewPagerAdapter(FragmentManager fm) {
        super(fm);
        //pages_count=3536;
    }

    @Override
    public Fragment getItem(int position) {

        return WeekViewFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return pages_count;
    }



}