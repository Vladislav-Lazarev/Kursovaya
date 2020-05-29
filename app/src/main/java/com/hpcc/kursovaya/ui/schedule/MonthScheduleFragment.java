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
import com.hpcc.kursovaya.ui.schedule.MonthViewPager.MonthViewFragment;
import com.hpcc.kursovaya.ui.schedule.MonthViewPager.MonthViewPagerAdapter;
import com.hpcc.kursovaya.ui.schedule.WeekViewPager.WeekViewFragment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MonthScheduleFragment extends Fragment {
    private static String TAG = MonthScheduleFragment.class.getSimpleName();
    private ViewPager pager;
    private View root;
    private MonthViewPagerAdapter pagerAdapter;
    private int monthDifference;

    public MonthScheduleFragment(){
        super();
    }

    public MonthScheduleFragment(int monthDifference){
        super();
        this.monthDifference = monthDifference;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        MainActivity activity = (MainActivity) getActivity();
        switch (item.getItemId()){
            case R.id.action_importTemplates:
                activity.prepareActionImportTemplates();
                return true;
            case R.id.action_reportPeriod:
                if(DBManager.readAll(Speciality.class).size()!=0) {
                    activity.prepareReporDatePicker();
                } else {
                    Toast.makeText(getActivity(), R.string.toast_fragment_no_specialities, Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanseState) {
        root = inflater.inflate(R.layout.fragment_month_schedule,container,false);
        pager = root.findViewById(R.id.pager);
        pagerAdapter = new MonthViewPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pager.setAdapter(pagerAdapter);
        Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
        ImageButton toCurrentDayButton = toolbar.findViewById(R.id.toCurrentDay);
        toCurrentDayButton.setOnClickListener(v -> pager.setCurrentItem(monthDifference));
        pager.setCurrentItem(monthDifference);
        setActionBarTitle();
        return root;
    }

    public void setActionBarTitle(){
        if(!((MainActivity) getActivity()).isLanguageChanged()) {
            Log.d(TAG, "setActionBar title in schedule called");
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            DateTime from = formatter.parseDateTime("01/01/1990 00:00:00");
            StringBuilder title = new StringBuilder();
            title.append(monthNumberToString(from.plusMonths(monthDifference).getMonthOfYear()))
                    .append(", ")
                    .append(from.plusMonths(monthDifference).getYear());
            ((MainActivity) getActivity()).setActionBarTitle(title.toString());
            ((MainActivity) getActivity()).showOverflowMenu(true);
        } else {
            Log.d(TAG, "setActionBar title in schedule called (language changed true)");
            ((MainActivity) getActivity()).showOverflowMenu(false);
           // ((MainActivity) getActivity()).setLanguageChanged(false);
        }
    }

    public void invokeRefresh() {
        refreshGrid(pager.getCurrentItem());
    }
    public void refreshGrid(int page){
       FragmentStatePagerAdapter a = (FragmentStatePagerAdapter) pager.getAdapter();
       MonthViewFragment currentPage = (MonthViewFragment) a.instantiateItem(pager,page);
       currentPage.refreshGrid();
    }

}
