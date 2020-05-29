package com.hpcc.kursovaya.ui.schedule.MonthViewPager;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.ui.schedule.DayScheduleFragment;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;

public class MonthViewFragment extends Fragment implements MonthViewAdapter.ItemClickListener {
    private static String ARGUMENT_MONTH_FROM_CURRENT = "arg_month_from_current";
    private MonthViewAdapter adapter;
    private DateTime firstDay;
    int dayDifference;
    private View root;
    private ImageButton toCurrentDay;
    private TextView currentDayText;

    public static Fragment newInstance(int position) {
        MonthViewFragment pageFragment = new MonthViewFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_MONTH_FROM_CURRENT,position);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int monthFromCurrent =getArguments().getInt(ARGUMENT_MONTH_FROM_CURRENT);
        DateTime startDate = DateTime.parse(ConstantApplication.MIN_DATE_TIME, DateTimeFormat.forPattern(ConstantApplication.PATTERN_DATE_TIME));
        //firstDayOfWeek = DateTime.now().withDayOfWeek(DateTimeConstants.MONDAY);
        //Log.d(TAG,"Created fragment with week from current "+weekFromCurrent);
        firstDay = startDate.plusMonths(monthFromCurrent).withDayOfMonth(1).withDayOfWeek(1);
        dayDifference = Days.daysBetween(startDate,firstDay).getDays();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanseState) {
        super.onCreate(savedInstanseState);
        final Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
        toCurrentDay = toolbar.findViewById(R.id.toCurrentDay);
        currentDayText = toolbar.findViewById(R.id.currentDayText);
        root = inflater.inflate(R.layout.fragment_month,null);
        RecyclerView rv = root.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        RecyclerView rvHeader = root.findViewById(R.id.rvDayHeader);
        rvHeader.setHasFixedSize(true);
        rvHeader.setLayoutManager(new GridLayoutManager(getActivity(),7));
        DayHeaderAdapter headerAdapter = new DayHeaderAdapter(getActivity());
        rvHeader.setAdapter(headerAdapter);
        int[] data = new int[42];
        DateTime firstDayCopy = new DateTime(firstDay);
        for(int i=0;i<42;i++){
            data[i] = i+dayDifference;
            firstDayCopy = firstDayCopy.plusDays(1);
        }
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        adapter = new MonthViewAdapter(getActivity(),data);
        rv.setAdapter(adapter);
        adapter.setClickListener(this);
        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_top,R.anim.slide_out_bottom);
        ((MainActivity)getActivity()).setCurrentViewSelected(MainActivity.DAY);
        ft.replace(R.id.nav_host_fragment, new DayScheduleFragment(adapter.getItem(position)),MainActivity.DAY_TAG);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isResumed() && !((MainActivity)getActivity()).isLanguageChanged()) {
            //((MainActivity)getActivity()).setWeeksFromCurrent();
            if(((MainActivity)getActivity()).isScheduleSelected()) {
                StringBuilder title = new StringBuilder();
                title.append(monthNumberToString(firstDay.plusDays(42).getMonthOfYear()-1)).append(", ").append(firstDay.getYear());
                if (firstDay.getYear()==DateTime.now().getYear() && firstDay.plusDays(14).getMonthOfYear()==DateTime.now().getMonthOfYear()){
                    toCurrentDay.setVisibility(View.GONE);
                    currentDayText.setVisibility(View.GONE);
                } else {
                    toCurrentDay.setVisibility(View.VISIBLE);
                    currentDayText.setVisibility(View.VISIBLE);
                }
                //Log.d(TAG, firstDayOfWeek.toString());
                //refreshGrid(firstDayOfWeek,firstDayOfWeek.plusDays(6));
                ((MainActivity) getActivity()).setActionBarTitle(title.toString());
            }
        } else if (((MainActivity)getActivity()).isLanguageChanged()) {
            ((MainActivity) getActivity()).showOverflowMenu(false);
        }
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

    public void refreshGrid() {
        adapter.notifyDataSetChanged();
    }
}
