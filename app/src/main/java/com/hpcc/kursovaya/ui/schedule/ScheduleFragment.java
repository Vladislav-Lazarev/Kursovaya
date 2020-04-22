package com.hpcc.kursovaya.ui.schedule;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;
import com.hpcc.kursovaya.ui.schedule.WeekViewPager.WeekViewFragment;
import com.hpcc.kursovaya.ui.schedule.WeekViewPager.WeekViewPagerAdapter;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class ScheduleFragment extends Fragment {
    private static final String TAG = ScheduleFragment.class.getSimpleName();
    private String title;
    private ViewPager pager;
    private View root;
    private WeekViewPagerAdapter pagerAdapter;
    boolean isCreatedAlready = false;

    private long mLastClickTime = 0;
    private int weekDifference;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("ScheduleFragment", "onCreateView is called");
        Log.d("ScheduleFragment", Boolean.toString(isCreatedAlready));
        LocaleManager.setLocale(getActivity());

        if(!isCreatedAlready) {

            root = inflater.inflate(R.layout.fragment_schedule, container, false);

            pager = root.findViewById(R.id.pager);
            pagerAdapter = new WeekViewPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            pager.setAdapter(pagerAdapter);
            Log.d("ScheduleFragment", "onCreateView is called");
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            final DateTime startDate = formatter.parseDateTime("01/01/1990 00:00:00");
            DateTime currentDate = DateTime.now();
            weekDifference = Weeks.weeksBetween(startDate.dayOfWeek().withMinimumValue().minusDays(1), currentDate.dayOfWeek().withMaximumValue().plusDays(1)).getWeeks() - 1;
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
            setCoupleHeaders();
            //setActionBarTitle();
        }
        return root;
    }

    public void setCoupleHeaders(){
        TextView firstHalfFirstCoupleHeader = root.findViewById(R.id.firstHalfFirstCoupleHeader);
        firstHalfFirstCoupleHeader.setText(getCoupleHeader(0,0));
        TextView secondHalfFirstCoupleHeader = root.findViewById(R.id.secondHalfFirstCoupleHeader);
        secondHalfFirstCoupleHeader.setText(getCoupleHeader(0,1));

        TextView firstHalfSecondCoupleHeader = root.findViewById(R.id.firstHalfSecondCoupleHeader);
        firstHalfSecondCoupleHeader.setText(getCoupleHeader(1,0));
        TextView secondHalfSecondCoupleHeader = root.findViewById(R.id.secondHalfSecondCoupleHeader);
        secondHalfSecondCoupleHeader.setText(getCoupleHeader(1,1));

        TextView firstHalfThirdCoupleHeader = root.findViewById(R.id.firstHalfThirdCoupleHeader);
        firstHalfThirdCoupleHeader.setText(getCoupleHeader(2,0));
        TextView secondHalfThirdCoupleHeader = root.findViewById(R.id.secondHalfThirdCoupleHeader);
        secondHalfThirdCoupleHeader.setText(getCoupleHeader(2,1));

        TextView firstHalfFourthCoupleHeader = root.findViewById(R.id.firstHalfFourthCoupleHeader);
        firstHalfFourthCoupleHeader.setText(getCoupleHeader(3,0));
        TextView secondHalfFourthCoupleHeader = root.findViewById(R.id.secondHalfFourthCoupleHeader);
        secondHalfFourthCoupleHeader.setText(getCoupleHeader(3,1));

        TextView firstHalfFifthCoupleHeader = root.findViewById(R.id.firstHalfFifthCoupleHeader);
        firstHalfFifthCoupleHeader.setText(getCoupleHeader(4,0));
        TextView secondHalfFifthCoupleHeader = root.findViewById(R.id.secondHalfFifthCoupleHeader);
        Log.d(TAG, getCoupleHeader(4,1));
        secondHalfFifthCoupleHeader.setText(getCoupleHeader(4,1));
    }

    private String getCoupleHeader(int couple,int half){
        StringBuilder hourTimeLabel = new StringBuilder();
        StringBuilder minuteTimeLabel = new StringBuilder();
        if(ConstantApplication.timeArray[couple][half][0]<10){
            hourTimeLabel.append("0");
        }
        hourTimeLabel.append(ConstantApplication.timeArray[couple][half][0]);
        if(ConstantApplication.timeArray[couple][half][1]<10){
            minuteTimeLabel.append("0");
        }
        minuteTimeLabel.append(ConstantApplication.timeArray[couple][half][1]);
        StringBuilder result = new StringBuilder(hourTimeLabel);
        result.append(":").append(minuteTimeLabel);
        return result.toString();
    }

    public void setCurrentWeek(int weekDiffernce){
        pager.setCurrentItem(weekDiffernce,false);
    }


    public void setActionBarTitle(){
        if(!((MainActivity) getActivity()).isLanguageChanged()) {
            Log.d(TAG, "setActionBar title in schedule called");
            ((MainActivity) getActivity()).setActionBarTitle(title);
            ((MainActivity) getActivity()).showOverflowMenu(true);
        } else {
            Log.d(TAG, "setActionBar title in schedule called (language changed true)");
            ((MainActivity) getActivity()).showOverflowMenu(false);
            ((MainActivity) getActivity()).setLanguageChanged(false);
        }
    }

    public void refreshGrid(DateTime from, DateTime to) {
        FragmentStatePagerAdapter a = (FragmentStatePagerAdapter) pager.getAdapter();
        WeekViewFragment currentPage = (WeekViewFragment) a.instantiateItem(pager, weekDifference);
        currentPage.refreshGrid(from, to);
    }
}