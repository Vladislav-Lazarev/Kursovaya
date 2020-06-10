package com.hpcc.kursovaya.ui.schedule.WeekViewPager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.hpcc.kursovaya.ClassesButton.ClassesButtonWrapper;
import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.AnotherEvent;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAnotherEvent;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.schedule.AddEventDialog;
import com.hpcc.kursovaya.ui.schedule.HandleClassDialog;
import com.hpcc.kursovaya.ui.schedule.HandleEventDialog;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class WeekViewFragment extends Fragment {
    public static final int ADD_CLASS = 1;
    public static final int EDIT_CLASS = 2;
    public static final int ADD_EVENT = 3;
    public static final int EDIT_EVENT = 4;

    private static final String TAG = "WeekViewFragment";
    public static final String ARGUMENT_WEEK_FROM_CURRENT="arg_week_from_current";
    private int weekFromCurrent;
    private DateTime firstDayOfWeek;
    private TextView[] dayHeaders;
    private ImageButton toCurrentDay;
    private TextView currentDayText;
    private static ImageButton cancelSelect;
    private static ImageButton cancelSelectCompleted;
    private static ImageButton cancelSelectCanceled;
    private View weekView;
    private List<List<ClassesButtonWrapper>> classes = new ArrayList<>();
    private static ArrayList<ClassesButtonWrapper> selectedButtons = new ArrayList<>();
    private boolean isCurrentWeek = false;


    public TextView[] getDayHeaders() {
        return dayHeaders;
    }

    public void setDayHeaders(TextView[] dayHeaders) {
        this.dayHeaders = dayHeaders;
    }

    public void setWeekFromCurrent(int weekFromCurrent) {
        this.weekFromCurrent = weekFromCurrent;
    }

    private long mLastClickTime = 0;


    public static WeekViewFragment newInstance(int weekFromCurrent) {
        WeekViewFragment pageFragment = new WeekViewFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_WEEK_FROM_CURRENT, weekFromCurrent);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    public int getWeekFromCurrent(){ return weekFromCurrent; }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        dayHeaders = new TextView[7];
        weekFromCurrent=getArguments().getInt(ARGUMENT_WEEK_FROM_CURRENT);
        firstDayOfWeek = DateTime.parse(ConstantApplication.MIN_DATE_TIME, DateTimeFormat.forPattern(ConstantApplication.PATTERN_DATE_TIME));
        //firstDayOfWeek = DateTime.now().withDayOfWeek(DateTimeConstants.MONDAY);
        //Log.d(TAG,"Created fragment with week from current "+weekFromCurrent);
        if(weekFromCurrent!=0){
            firstDayOfWeek = firstDayOfWeek.plusWeeks(weekFromCurrent);
            // Log.d(TAG,"First day of week "+firstDayOfWeek.toString()+" with weeksFromCurrent"+weekFromCurrent);
        }
    }

    private String monthNumberToString(int monthNumber){
        String monthStr = new String();
        Resources res = getResources();
        switch (monthNumber){
            case 1:
                monthStr=res.getString(R.string.January);
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

    private GridLayout getCurrentGrid(View view, int dayOfWeek){
        GridLayout layout = new GridLayout(getActivity());
        switch (dayOfWeek){
            case 1:
                layout = view.findViewById(R.id.monGrid);
                break;
            case 2:
                layout = view.findViewById(R.id.tueGrid);
                break;
            case 3:
                layout = view.findViewById(R.id.wedGrid);
                break;
            case 4:
                layout = view.findViewById(R.id.thuGrid);
                break;
            case 5:
                layout = view.findViewById(R.id.friGrid);
                break;
            case 6:
                layout = view.findViewById(R.id.satGrid);
                break;
            case 7:
                layout = view.findViewById(R.id.sunGrid);
                break;
        }
        return layout;
    }

    private TextView[] getCurrentDayHeader(View view,int dayOfWeek){
        TextView[] dayHeader = new TextView[2];
        switch(dayOfWeek){
            case 1:
                dayHeader[0] = view.findViewById(R.id.monHeader);
                dayHeader[1] = view.findViewById(R.id.day0);
                break;
            case 2:
                dayHeader[0] = view.findViewById(R.id.tueHeader);
                dayHeader[1] = view.findViewById(R.id.day1);
                break;
            case 3:
                dayHeader[0] = view.findViewById(R.id.wedHeader);
                dayHeader[1] = view.findViewById(R.id.day2);
                break;
            case 4:
                dayHeader[0] = view.findViewById(R.id.thuHeader);
                dayHeader[1] = view.findViewById(R.id.day3);
                break;
            case 5:
                dayHeader[0] = view.findViewById(R.id.friHeader);
                dayHeader[1] = view.findViewById(R.id.day4);
                break;
            case 6:
                dayHeader[0] = view.findViewById(R.id.satHeader);
                dayHeader[1] = view.findViewById(R.id.day5);
                break;
            default:
                dayHeader[0] = view.findViewById(R.id.sunHeader);
                dayHeader[1] = view.findViewById(R.id.day6);
        }
        return dayHeader;
    }

    public void cancelOnClick(Toolbar toolbar, Toolbar toolbar1){
        ((MainActivity) getActivity()).setSelectMode(false);
        toolbar1.setVisibility(View.GONE);

        toolbar.setVisibility(View.VISIBLE);
        for (ClassesButtonWrapper b: selectedButtons) {
            b.setUnselectBackground();
        }
        selectedButtons.clear();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        LocaleManager.setLocale(getActivity());
        final View view = inflater.inflate(R.layout.fragment_weekview, null);
        weekView = view;
        final Context cntxt = getContext();
        Thread prepareDayHeadersThread = new Thread(){
            public void run(){
                prepareDayHeaders(view);
            }
        };
        prepareDayHeadersThread.start();
        final Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
        toCurrentDay = toolbar.findViewById(R.id.toCurrentDay);
        currentDayText = toolbar.findViewById(R.id.currentDayText);
        final Toolbar toolbar1 = ((MainActivity)getActivity()).getToolbar1();
        final Toolbar toolbarCompleted = ((MainActivity)getActivity()).getToolbarCompleteClasses();
        final Toolbar toolbarCanceled = ((MainActivity) getActivity()).getToolbarCanceledClasses();
        cancelSelectCompleted = toolbarCompleted.findViewById(R.id.turnOff_complete);
        cancelSelectCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setSelectMode(false);
                toolbar.setVisibility(View.VISIBLE);
                toolbarCompleted.setVisibility(View.GONE);
                for (ClassesButtonWrapper b: selectedButtons) {
                    b.setUnselectBackground();
                }
                selectedButtons.clear();
            }
        });
        cancelSelectCanceled  = toolbarCanceled.findViewById(R.id.turnOff_cancel);
        cancelSelectCanceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setSelectMode(false);
                toolbar.setVisibility(View.VISIBLE);
                toolbarCanceled.setVisibility(View.GONE);
                for (ClassesButtonWrapper b: selectedButtons) {
                    b.setUnselectBackground();
                }
                selectedButtons.clear();
            }
        });

        if(cancelSelect!=null);
        {
            cancelSelect = toolbar1.findViewById(R.id.turnOff_editing);
            cancelSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOnClick(toolbar,toolbar1);
                }
            });
        }
        ImageButton deleteClasses = toolbar1.findViewById(R.id.delete_classes);
        ImageButton completedClasses = toolbarCompleted.findViewById(R.id.completed_classes);
        completedClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareCompletedClassesDialog();
            }
        });
        ImageButton cancelCompletedClasses = toolbarCompleted.findViewById(R.id.uncompleted_classes);
        cancelCompletedClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                prepareCancelCompletedClassesDialog();
            }
        });
        ImageButton cancelledClasses = toolbarCanceled.findViewById(R.id.canceled_classes);
        cancelledClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                prepareCancelledClassesDialog();
            }
        });
        ImageButton uncancelledClasses = toolbarCanceled.findViewById(R.id.uncancelled_classes);
        uncancelledClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                prepareUncancelledClasses();
            }
        });
        deleteClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                prepareDeleteDialog();
            }
        });
        Fragment thisFragment = this;
        Thread t = new Thread(){
            public void run(){

                for(int i = 0; i < ConstantApplication.MAX_COUNT_WEEK; i++){
                    List<ClassesButtonWrapper> classesButtonWrapperList = new ArrayList<>();
                    for(int j = 0; j < ConstantApplication.MAX_COUNT_HALF_PAIR; j++){
                        StringBuilder className = new StringBuilder("class");
                        className.append(j).append(i);
                        final int classDay = i;
                        final int classHour = j;
                        StringBuilder classNumber = new StringBuilder("class");
                        classNumber.append(j).append(i).append('n');

                        int classRes = getResources().getIdentifier(className.toString(),"id",getActivity().getPackageName());
                        int classNumRes = getResources().getIdentifier(classNumber.toString(),"id",getActivity().getPackageName());
                        TextView classNumberTextV = view.findViewById(classNumRes);
                        classesButtonWrapperList.add(new ClassesButtonWrapper((Button)view.findViewById(classRes),classNumberTextV,getContext()));
                        classesButtonWrapperList.get(classHour).getBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                if(!((MainActivity) getActivity()).isSelectMode()) {
                                    Intent intent;
                                    if(DBManager.readAll(Speciality.class).size()!=0) {
                                        if(DBManager.readAll(Group.class).size()!=0) {
                                            if(DBManager.readAll(Subject.class).size()!=0) {
                                                DateTime dayOfWeek = firstDayOfWeek.plusDays(classDay).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
                                                int hourOfDay = ConstantApplication.timeArray[classHour/2][((classHour+1) % 2 == 0)? 1 : 0][0];
                                                int minuteOfHour = ConstantApplication.timeArray[classHour/2][((classHour+1) % 2 == 0)? 1 : 0][1];
                                                dayOfWeek = dayOfWeek.withHourOfDay(hourOfDay).withMinuteOfHour(minuteOfHour);
                                                if (classesButtonWrapperList.get(classHour).getBtn().getText().equals("")) {
                                                    AddEventDialog eventDialog = AddEventDialog.newInstance(getActivity(),classDay,classHour,dayOfWeek);
                                                    eventDialog.setTargetFragment(WeekViewFragment.this,1);
                                                    eventDialog.show(getFragmentManager(),"addEvent");
                                                    /*intent = new Intent(getActivity(), AddClass.class);
                                                    intent.putExtra("classDay", classDay);
                                                    intent.putExtra("classHour", classHour);
                                                    intent.putExtra("dayOfWeek", firstDayOfWeek.plusDays(classDay).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0));
                                                    startActivityForResult(intent, ADD_CLASS);*/
                                                } else {
                                                    DialogFragment handleDialog= null;
                                                    if(classesButtonWrapperList.get(classHour).getEvent()==null){
                                                        int secondCellHour = classHour + ((classHour % ConstantApplication.TWO == ConstantApplication.ZERO) ? 1 : -1);
                                                        handleDialog = HandleClassDialog.newInstance(getActivity(),classDay,classHour,dayOfWeek, classes.get(classDay).get(classHour).getAcademicHour(),
                                                                secondCellHour,classes.get(classDay).get(secondCellHour).getAcademicHour(),classes.get(classDay).get(classHour));
                                                    } else if (classesButtonWrapperList.get(classHour).getAcademicHour()==null){
                                                        handleDialog = HandleEventDialog.newInstance(getActivity(),classHour,classDay,dayOfWeek,classesButtonWrapperList.get(classHour));
                                                    }
                                                    handleDialog.setTargetFragment(WeekViewFragment.this,1);
                                                    handleDialog.show(getFragmentManager(),"handleDialog");
                                                    //intent.putExtra("group",groupEnt);
                                                    //intent.putExtra("subject",subjectEnt);
                                                }
                                            }   else {
                                                    Toast.makeText(cntxt, R.string.toast_fragment_no_subjects,Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(cntxt, R.string.toast_fragment_no_groups,Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(cntxt, R.string.toast_fragment_no_specialities,Toast.LENGTH_LONG).show();
                                        }



                                } else if(!classesButtonWrapperList.get(classHour).getBtn().getText().equals("") &&
                                        !classesButtonWrapperList.get(classHour).isSelected()){
                                    classesButtonWrapperList.get(classHour).setSelectBackground();
                                    classesButtonWrapperList.get(classHour).setSelected(true);
                                    selectedButtons.add(classesButtonWrapperList.get(classHour));
                                } else if (classesButtonWrapperList.get(classHour).isSelected()){
                                    classesButtonWrapperList.get(classHour).setUnselectBackground();
                                    selectedButtons.remove(classesButtonWrapperList.get(classHour));
                                    if(selectedButtons.size()==0){
                                        cancelSelect.performClick();
                                        toolbarCanceled.setVisibility(View.GONE);
                                        toolbarCompleted.setVisibility(View.GONE);
                                        //((MainActivity) getActivity()).setSelectMode(false);
                                        //toolbar button placeholder
                                    }
                                }
                            }
                        });
                        classesButtonWrapperList.get(classHour).getBtn().setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if(!((MainActivity) getActivity()).isSelectMode() &&
                                        !classesButtonWrapperList.get(classHour).getBtn().getText().equals("")) {

                                    if(classesButtonWrapperList.get(classHour).isGroupNameShown() && classesButtonWrapperList.get(classHour).getEvent()==null){
                                        classesButtonWrapperList.get(classHour).getBtn().setText(classesButtonWrapperList.get(classHour).getAcademicHour().getTemplateAcademicHour().getSubject().getName());
                                        classesButtonWrapperList.get(classHour).setGroupNameShown(false);
                                    } else if(classesButtonWrapperList.get(classHour).getEvent()==null) {
                                        classesButtonWrapperList.get(classHour).getBtn().setText(classesButtonWrapperList.get(classHour).getAcademicHour().getTemplateAcademicHour().getGroup().getName());
                                        classesButtonWrapperList.get(classHour).setGroupNameShown(true);
                                    }
                                    /*  classesButtonWrapperList.get(classHour).setSelectBackground();
                                    selectedButtons.add(classesButtonWrapperList.get(classHour));
                                    classesButtonWrapperList.get(classHour).setSelected(true);
                                    ((MainActivity) getActivity()).setSelectMode(true);
                                    toolbar.setVisibility(View.GONE);
                                    toolbar1.setVisibility(View.VISIBLE);*/
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                    classes.add(classesButtonWrapperList);
                }
            }

        };
        t.start();
        try {
            t.join();
            prepareDayHeadersThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(firstDayOfWeek.equals(DateTime.now().withDayOfWeek(DateTimeConstants.MONDAY).withHourOfDay(0).withMinuteOfHour(0).withMillisOfDay(0))){
            toCurrentDay.setVisibility(View.GONE);
            currentDayText.setVisibility(View.VISIBLE);
            isCurrentWeek = true;
            int dayOfWeek = DateTime.now().getDayOfWeek();
            TextView dayOfWeekHeader[] = getCurrentDayHeader(view, dayOfWeek);
            GridLayout layout = getCurrentGrid(view,dayOfWeek);
            layout.setBackground(ContextCompat.getDrawable(cntxt,R.drawable.grid_today));
            dayOfWeekHeader[0].setTextColor(ContextCompat.getColor(cntxt, R.color.menuTextColor));
            dayOfWeekHeader[1].setTextColor(ContextCompat.getColor(cntxt, R.color.menuTextColor));
            if(dayOfWeek<7){
                for(int i = dayOfWeek+1; i<=7;i++){
                    Drawable background = ContextCompat.getDrawable(cntxt,R.drawable.future_days_grid);
                    GridLayout futureLayout = getCurrentGrid(view,i);
                    futureLayout.setBackground(background);
                }
            }
        }
        //Thread fillButtons = new Thread(){
        //public void run() {
     /*   List<AcademicHour> academicHours = AcademicHour.academicHourListFromPeriod(firstDayOfWeek.toDate(),firstDayOfWeek.plusDays(6).toDate());
        for(AcademicHour hour: academicHours){
            TemplateAcademicHour templateAcademicHour = hour.getTemplateAcademicHour();
            classes.get(templateAcademicHour.getNumberDayOfWeek()).get(templateAcademicHour.getNumberHalfPairButton()).setAcademicHour(hour);
        }*/
        //      }
        //    };
        //fillButtons.start();
        return view;
    }

    private void prepareDayHeaders(View view) {
        DateTime firstDayOfWeekCopy;

        for (int i = 0; i < 7; i++) {
            String viewDayHeader = "day" + i;
            firstDayOfWeekCopy = firstDayOfWeek.plusDays(i);
            int dayRes = getResources().getIdentifier(viewDayHeader, "id", getActivity().getPackageName());
            dayHeaders[i] = (TextView) view.findViewById(dayRes);
            String dayOfMonth = Integer.toString(firstDayOfWeekCopy.getDayOfMonth());
            String monthOfYear = Integer.toString(firstDayOfWeekCopy.getMonthOfYear());
            StringBuilder sb = new StringBuilder();
            sb = (dayOfMonth.length() == 1 ? sb.append("0").append(dayOfMonth) : sb.append(dayOfMonth));
            //sb = (monthOfYear.length()==1 ? sb.append("0").append(monthOfYear):sb.append(monthOfYear));
            dayHeaders[i].setText(sb.toString());
            if (firstDayOfWeek.isAfter(DateTime.now().withDayOfWeek(DateTimeConstants.MONDAY).withHourOfDay(0).withMinuteOfHour(0).withMillisOfDay(0))) {
                GridLayout layout = getCurrentGrid(weekView,i+1);
                layout.setBackground(getResources().getDrawable(R.drawable.future_days_grid));
            }
        }
    }

    private void prepareUncancelledClasses() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_cut_classes_template);
        builder.setMessage(R.string.popup_uncut_classes_content);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onUncancelledClassesClick();
            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void onUncancelledClassesClick() {
        for (ClassesButtonWrapper b : selectedButtons) {
            if(!b.getAcademicHour().hasCompleted()) {
                b.setCanceled(false);
            } else {
                Toast.makeText(getActivity(), R.string.class_cant_be_readed ,Toast.LENGTH_LONG).show();
            }
        }
        cancelSelectCanceled.performClick();
    }

    private void prepareCancelledClassesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_cut_classes_template);
        builder.setMessage(R.string.popup_cut_classes_content);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCancelClassesAcceptClick();
            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void onCancelClassesAcceptClick() {
        for (ClassesButtonWrapper b : selectedButtons) {
            if(!b.getAcademicHour().hasCompleted()) {
                b.setCanceled(true);
            } else {
                Toast.makeText(getActivity(), R.string.class_cant_be_eaded ,Toast.LENGTH_LONG).show();
            }
        }
        cancelSelectCanceled.performClick();
    }

    private void prepareCancelCompletedClassesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_complete_classes_template);
        builder.setMessage(R.string.popup_uncomplete_classes_content);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onCancelCompletedClassesAcceptClick();
            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void onCancelCompletedClassesAcceptClick() {
        for (ClassesButtonWrapper b : selectedButtons) {
            if(b.getAcademicHour().hasCompleted() && !b.getAcademicHour().hasCanceled()) {
                b.setCompleted(false);
            } else {
                Toast.makeText(getActivity(), R.string.class_cant_be_unread ,Toast.LENGTH_LONG).show();
            }
        }
        cancelSelectCompleted.performClick();
    }

    private void prepareCompletedClassesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_complete_classes_template);
        builder.setCancelable(false);
        builder.setMessage(R.string.popup_complete_classes_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onCompletedClassesAcceptClick();
            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void onCompletedClassesAcceptClick() {
        for (ClassesButtonWrapper b : selectedButtons) {
            if(!b.getAcademicHour().hasCanceled()) {
                b.setCompleted(true);
            } else {
                Toast.makeText(getActivity(), R.string.class_cant_be_read ,Toast.LENGTH_LONG).show();
            }
        }
        cancelSelectCompleted.performClick();
    }

    private void prepareDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_delete_classes_template);
        builder.setMessage(R.string.popup_delete_classes_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onDeleteClassesAcceptClick();
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void onDeleteClassesAcceptClick() {
        for (ClassesButtonWrapper b : selectedButtons) {
            b.clearButtonContent();
        }
        selectedButtons.clear();
        cancelSelect.performClick();
        refreshGrid(firstDayOfWeek.minusWeeks(1), firstDayOfWeek.minusWeeks(1).plusDays(6));
    }

    //handle data from activities here
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,resultCode+""+requestCode);
        if(resultCode== Activity.RESULT_OK){
            List<AcademicHour> academicHourList = data.getParcelableArrayListExtra("academicHourList");
            int classDay = data.getIntExtra("classDay", 0);
            int classHour = data.getIntExtra("classHour", 0);

            switch (requestCode){
                case ADD_CLASS:
                case EDIT_CLASS:
                    boolean isHourChanged = data.getBooleanExtra("clearSecondCell",false);
                    if(isHourChanged){
                        int pos = ConstantApplication.secondCellShift(classHour);
                        classes.get(classDay).get(classHour + pos).clearButtonContent();
                    }

                    int posSecondCell = 0;
                    for (int i = 0; i < academicHourList.size(); i++) {
                        academicHourList.get(i).refreshAllNumberPair(classes.get(classDay).get(classHour + posSecondCell).getAcademicHour());

                        classes.get(classDay).get(classHour + posSecondCell).setAcademicHour(academicHourList.get(i));
                        repeatForWeeks(academicHourList.get(i),classDay,classHour + posSecondCell,getActivity());

                        if (academicHourList.size() == ConstantApplication.TWO &&
                                i < academicHourList.size() - ConstantApplication.ONE){
                            posSecondCell = ConstantApplication.secondCellShift(classHour);
                            AcademicHour adjacentCellToDelete = classes.get(classDay).get(classHour + posSecondCell).getAcademicHour();

                            if (adjacentCellToDelete != null){
                                classes.get(classDay).get(classHour + posSecondCell).clearButtonContent();
                            }
                        }
                    }
                    break;
                case ADD_EVENT:
                case EDIT_EVENT:
                    AnotherEvent anotherEvent = data.getParcelableExtra("anotherEvent");
                    classes.get(classDay).get(classHour).setAnotherEvent(anotherEvent);
                    break;
                default:
                    return;
            }
        }
    }

    public static void repeatForWeeks(AcademicHour academicHour, int classDay, int classHour, Context context) {
        int numberOfWeeks = academicHour.getRepeatForNextWeek();
        DateTime start = new DateTime(academicHour.getDate());
        List<AcademicHour> academicHours =
                AcademicHour.academicHourListFromPeriod(start.plusDays(1).toDate(),start.plusWeeks(3).toDate());

        for(AcademicHour academicHourToDelete: academicHours){
            TemplateAcademicHour templateAcHour = academicHourToDelete.getTemplateAcademicHour();
            Group group = templateAcHour.getGroup();
            Subject subject = templateAcHour.getSubject();

            if(templateAcHour.getNumberDayOfWeek()==classDay && templateAcHour.getNumberHalfPairButton()==classHour
                    && group.equals(academicHour.getTemplateAcademicHour().getGroup())
                    && subject.equals(academicHour.getTemplateAcademicHour().getSubject())){

                DBManager.delete(AcademicHour.class,ConstantApplication.ID,academicHourToDelete.getId());
                academicHourToDelete.refreshAllNumberPair(null);
                DBManager.delete(TemplateAcademicHour.class,ConstantApplication.ID,templateAcHour.getId());
            }
        }
        if(numberOfWeeks!=0) {
            for (int i = 0; i < numberOfWeeks; i++) {
                Date date = academicHour.getDate();
                DateTime nextWeek = (date == null) ? null : new DateTime(date).plusWeeks(i + 1);
                TemplateAcademicHour templateAcademicHourSource = academicHour.getTemplateAcademicHour();
                TemplateAcademicHour templateAcademicHour = new TemplateAcademicHour();
                templateAcademicHour.setGroup(templateAcademicHourSource.getGroup());
                templateAcademicHour.setSubject(templateAcademicHourSource.getSubject());
                templateAcademicHour.setDayAndPair(templateAcademicHourSource.getDayAndPairButton());
                try {
                    DBManager.write(templateAcademicHour.createEntity());
                    Log.d(TAG, "repeatForWeeks = " + templateAcademicHour.toString());
                } catch (Exception e) {
                    // TODO Оповещение о не правильности\корректности
                    Log.d(TAG,"repeatForWeeks"+e.toString());
                }
                AcademicHour nextAcademicHour = new AcademicHour(0, templateAcademicHour, nextWeek.toDate()
                        , academicHour.getNote(), academicHour.getNotificationBefore(), 0, false, false, academicHour.getNumberPair());
                try {
                    DBManager.write(nextAcademicHour.createEntity());
                    Log.d(TAG, "repeatForWeeks = " + nextAcademicHour.toString());
                } catch (Exception e) {
                    // TODO Оповещение о не правильности\корректности
                    e.printStackTrace();
                }
                AcademicHour.setNotifaction(context.getApplicationContext(),nextAcademicHour);
            }
        }
    }

    public void refreshGrid(DateTime from, DateTime to){
        if(isCurrentWeek){
            toCurrentDay.setVisibility(View.GONE);
            currentDayText.setVisibility(View.GONE);
        } else {
            toCurrentDay.setVisibility(View.VISIBLE);
            currentDayText.setVisibility(View.VISIBLE);
        }
        List<AcademicHour> academicHours = DBManager.copyObjectFromRealm(
                AcademicHour.academicHourListFromPeriod(from.toDate(),to.toDate()));
        List<AnotherEvent> anotherEvents = DBManager.copyObjectFromRealm(AnotherEvent.anotherEventsFromPeriod(from.toDate(),to.toDate()));
        for(List<ClassesButtonWrapper> classs : classes ){
            for(ClassesButtonWrapper clazz : classs){
                if(clazz!=null) {
                    clazz.clearButtonContentWithoutDeleting();
                }
            }
        }
        for(AcademicHour hour: academicHours){
            TemplateAcademicHour templateAcademicHour = hour.getTemplateAcademicHour();
            classes.get(templateAcademicHour.getNumberDayOfWeek()).get(templateAcademicHour.getNumberHalfPairButton()).setAcademicHour(hour);
            classes.get(templateAcademicHour.getNumberDayOfWeek()).get(templateAcademicHour.getNumberHalfPairButton()).setTextNumber(Integer.toString(hour.getNumberPair()));
        }
        for(AnotherEvent event: anotherEvents){
            TemplateAnotherEvent templateAnotherEvent = event.getTemplateAnotherEvent();
            classes.get(templateAnotherEvent.getNumberDayOfWeek()).get(templateAnotherEvent.getNumberHalfPairButton()).setAnotherEvent(event);
        }
    }



    @Override
    public void onResume(){
        super.onResume();
        currentDayText.setVisibility(View.GONE);
        Log.d(TAG,Boolean.toString(((MainActivity)getActivity()).isLanguageChanged()));
        if (isResumed() && !((MainActivity)getActivity()).isLanguageChanged()) {
            ((MainActivity)getActivity()).setWeeksFromCurrent(weekFromCurrent);
            if(((MainActivity)getActivity()).isScheduleSelected()) {
                StringBuilder title = new StringBuilder();
                title.append(monthNumberToString(firstDayOfWeek.getMonthOfYear())).append(", ").append(firstDayOfWeek.getYear());
                Log.d(TAG, firstDayOfWeek.toString());
                refreshGrid(firstDayOfWeek,firstDayOfWeek.plusDays(6).withHourOfDay(23).withMinuteOfHour(59));
                ((MainActivity) getActivity()).setActionBarTitle(title.toString());
            }
        } else if (((MainActivity)getActivity()).isLanguageChanged()) {

        }
    }
   /* @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            StringBuilder title = new StringBuilder();
            title.append(monthNumberToString(firstDayOfWeek.getMonthOfYear())).append(", ").append(firstDayOfWeek.getYear());
            Log.d(TAG, firstDayOfWeek.toString());
            ((MainActivity)getActivity()).setActionBarTitle(title.toString());
        }
    }*/
}
