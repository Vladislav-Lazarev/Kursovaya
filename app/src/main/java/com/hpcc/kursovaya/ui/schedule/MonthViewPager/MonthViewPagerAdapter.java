package com.hpcc.kursovaya.ui.schedule.MonthViewPager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MonthViewPagerAdapter extends FragmentStatePagerAdapter {
    public int pages_count = 814;

    public MonthViewPagerAdapter(FragmentManager fm, int behaviorResumeOnlyCurrentFragment) {
        super(fm, behaviorResumeOnlyCurrentFragment);
        //pages_count=814;
    }

    @Override
    public Fragment getItem(int position) {
        return MonthViewFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return pages_count;
    }
}
