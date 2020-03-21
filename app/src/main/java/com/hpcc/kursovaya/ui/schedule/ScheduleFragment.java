package com.hpcc.kursovaya.ui.schedule;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.ui.schedule.WeekViewPager.WeekViewPagerAdapter;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class ScheduleFragment extends Fragment {
    private String title;
    private ViewPager pager;
    private View root;
    private WeekViewPagerAdapter pagerAdapter;
    boolean isCreatedAlready = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(!isCreatedAlready) {

            root = inflater.inflate(R.layout.fragment_schedule, container, false);
            pager = root.findViewById(R.id.pager);
            pagerAdapter = new WeekViewPagerAdapter(getChildFragmentManager());
            pager.setAdapter(pagerAdapter);
            Log.d("ScheduleFragment", "onCreateView is called");
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            final DateTime startDate = formatter.parseDateTime("01/01/1990 00:00:00");
            DateTime currentDate = DateTime.now();
            final int weekDifference = Weeks.weeksBetween(startDate.dayOfWeek().withMinimumValue().minusDays(1), currentDate.dayOfWeek().withMaximumValue().plusDays(1)).getWeeks() - 1;
            Thread t = new Thread() {
                public void run() {
                    StringBuilder titleSB = new StringBuilder();
                    DateTime currentDate = startDate.plusWeeks(weekDifference);
                    titleSB.append(monthNumberToString(currentDate.getMonthOfYear())).append(", ").append(currentDate.getYear());
                    title = titleSB.toString();

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
            };
            t.start();
            pager.setCurrentItem(weekDifference, false);
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isCreatedAlready = true;
        }
        setActionBarTitle();
        return root;
    }

    public void setCurrentWeek(int weekDiffernce){
        pager.setCurrentItem(weekDiffernce,false);
    }

    public void setActionBarTitle(){
        ((MainActivity) getActivity()).setActionBarTitle(title);
        ((MainActivity) getActivity()).showOverflowMenu(true);
    }

}