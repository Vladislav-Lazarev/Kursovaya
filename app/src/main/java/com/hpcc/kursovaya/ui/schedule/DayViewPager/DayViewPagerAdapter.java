package com.hpcc.kursovaya.ui.schedule.DayViewPager;

import android.view.MotionEvent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class DayViewPagerAdapter extends FragmentStatePagerAdapter {
    public int pages_count = 24752;

    private boolean isScrollEnabled = true;

    public DayViewPagerAdapter(FragmentManager fm, int behaviorResumeOnlyCurrentFragment){
        super(fm,behaviorResumeOnlyCurrentFragment);
    }

    @Override
    public Fragment getItem(int position){
        return DayViewFragment.newInstance(position);
    }

    @Override
    public int getCount(){return pages_count;}
}
