package com.hpcc.kursovaya.ui.hourChecker.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.ui.hourChecker.adapter.GroupSubjectAdapter;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.DayViewFragment;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class GroupSubjectTabFragment extends Fragment implements GroupSubjectAdapter.ItemClickListener{
    public static final String ARG_PAGE = "ARG_PAGE";

    //private List<AcademicHour> hours;
    private List<DayViewFragment.EventAgregator> events;
    private GroupSubjectAdapter adapter;

    public static GroupSubjectTabFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        GroupSubjectTabFragment fragment = new GroupSubjectTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            int page = getArguments().getInt(ARG_PAGE);
            List<AcademicHour> hours = new ArrayList<>();
            switch (page){
                case 0:
                    hours = GroupSubjectCheckActivity.unreadHours;
                    break;
                case 1:
                    hours = GroupSubjectCheckActivity.readHours;
                    break;
                case 2:
                    hours = GroupSubjectCheckActivity.canceledHours;
                    break;
            }
            events = new ArrayList<>();
            List<TemplateAcademicHour> templateAcademicHours = new ArrayList<>();
            for(AcademicHour academicHour : hours){
                DayViewFragment.EventAgregator eventAgregator = new DayViewFragment.EventAgregator();
                eventAgregator.anotherEvent=null;
                eventAgregator.academicHour=academicHour;
                templateAcademicHours.add(academicHour.getTemplateAcademicHour());
                events.add(eventAgregator);
            }
            Log.d("","");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_subject_tab_page,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.hourList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new GroupSubjectAdapter(getActivity(),events);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        return  view;
    }

    @Override
    public void onItemClick(View view, int position) {
        DayViewFragment.EventAgregator event = adapter.getItem(position);
        DateTime currentDate = DateTime.now();
        HandleGroupSubjectTabDialog handleDialog= null;
        if(event.academicHour!=null) {
            int secondCellHour = position + ((position % ConstantApplication.TWO == ConstantApplication.ZERO) ? 1 : -1);
            AcademicHour secondAcademicHour = (adapter.getItem(secondCellHour)==null) ? null : adapter.getItem(secondCellHour).academicHour;
            handleDialog = HandleGroupSubjectTabDialog.newInstance(getActivity(),currentDate.getDayOfWeek() - 1,position,currentDate, event.academicHour,
            secondCellHour,secondAcademicHour,adapter);
                    /*intent = new Intent(getActivity(), EditClass.class);
                    intent.putExtra("classDay", currentDate.getDayOfWeek() - 1);
                    intent.putExtra("classHour", position);
                    intent.putExtra("dayOfWeek", currentDate);
                    intent.putExtra("currentCell", event.academicHour);
                    intent.putExtra("secondClassHour", secondCellHour);
                    intent.putExtra("secondCell", secondAcademicHour);
                    startActivityForResult(intent, EDIT_CLASS);*/
        }
        handleDialog.setTargetFragment(GroupSubjectTabFragment.this,1);
        handleDialog.show(getFragmentManager(),"handleDialog");

    }

    @Override
    public void onLongItemClick(View view, int position) {

    }
}
