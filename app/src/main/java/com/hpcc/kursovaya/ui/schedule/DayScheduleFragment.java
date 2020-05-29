package com.hpcc.kursovaya.ui.schedule;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.CustomViewPager;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.DayViewFragment;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.DayViewPagerAdapter;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DayScheduleFragment extends Fragment {
    private static final String TAG = DayScheduleFragment.class.getSimpleName();
    private CustomViewPager pager;
    private View root;
    private DayViewPagerAdapter pagerAdapter;
    private int dayDifference;

    public DayScheduleFragment(){
        super();
    }

    public DayScheduleFragment(int dayDifference){
        super();
        this.dayDifference = dayDifference;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        activity.setCurrentViewSelected(MainActivity.DAY);
        setHasOptionsMenu(true);
        activity.invalidateOptionsMenu();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanseState){
        //LocaleManager.setLocale(getActivity());
        root = inflater.inflate(R.layout.fragment_day_schedule,container,false);
        pager = root.findViewById(R.id.pager);
        pagerAdapter = new DayViewPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pager.setAdapter(pagerAdapter);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime startDate = formatter.parseDateTime("01/01/1990 00:00:00");
        /*DateTime currentDate = DateTime.now();
        dayDifference = Days.daysBetween(startDate,currentDate).getDays();*/
        Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
        ImageButton toCurrentDayButton = toolbar.findViewById(R.id.toCurrentDay);
        toCurrentDayButton.setOnClickListener(v -> pager.setCurrentItem(Days.daysBetween(startDate,DateTime.now()).getDays()));
        pager.setCurrentItem(dayDifference);
        setActionBarTitle();
        return root;
    }

    private String monthNumberToString(int monthNumber) {
        String monthStr = new String();
        Resources res = getResources();
        switch (monthNumber) {
            case 1:
                monthStr = res.getString(R.string.January);
                break;
            case 2:
                monthStr = res.getString(R.string.February);
                break;
            case 3:
                monthStr = res.getString(R.string.March);
                break;
            case 4:
                monthStr = res.getString(R.string.April);
                break;
            case 5:
                monthStr = res.getString(R.string.May);
                break;
            case 6:
                monthStr = res.getString(R.string.June);
                break;
            case 7:
                monthStr = res.getString(R.string.July);
                break;
            case 8:
                monthStr = res.getString(R.string.August);
                break;
            case 9:
                monthStr = res.getString(R.string.September);
                break;
            case 10:
                monthStr = res.getString(R.string.October);
                break;
            case 11:
                monthStr = res.getString(R.string.November);
                break;
            default:
                monthStr = res.getString(R.string.December);
        }
        return monthStr;
    }

    public void setActionBarTitle(){
        if(!((MainActivity) getActivity()).isLanguageChanged()) {
            Log.d(TAG, "setActionBar title in schedule called");
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            DateTime from = formatter.parseDateTime("01/01/1990 00:00:00");
            StringBuilder title = new StringBuilder();
            title.append(monthNumberToString(from.plusDays(dayDifference).getMonthOfYear()))
                    .append(", ")
                    .append(from.plusDays(dayDifference).getYear());
            ((MainActivity) getActivity()).setActionBarTitle(title.toString());
            ((MainActivity) getActivity()).showOverflowMenu(true);
        } else {
            Log.d(TAG, "setActionBar title in schedule called (language changed true)");
            ((MainActivity) getActivity()).showOverflowMenu(false);
            // ((MainActivity) getActivity()).setLanguageChanged(false);
        }
    }

    public CustomViewPager getViewPager() {
        return pager;
    }

    public void invokeRefresh() {
        refreshGrid(pager.getCurrentItem());
    }
    public void refreshGrid(int page){
        FragmentStatePagerAdapter a = (FragmentStatePagerAdapter) pager.getAdapter();
        DayViewFragment currentPage = (DayViewFragment) a.instantiateItem(pager,page);
        currentPage.refreshGrid();
    }
}
