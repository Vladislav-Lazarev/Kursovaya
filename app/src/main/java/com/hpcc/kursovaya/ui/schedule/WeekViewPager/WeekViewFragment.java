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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hpcc.kursovaya.ClassesButton.ClassesButtonWrapper;
import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.ui.schedule.AddClass;
import com.hpcc.kursovaya.ui.schedule.EditClass;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import top.defaults.drawabletoolbox.DrawableBuilder;


public class WeekViewFragment extends Fragment {
    private final String TAG = "WeekViewFragment";
    public static final String ARGUMENT_WEEK_FROM_CURRENT="arg_week_from_current";
    private int weekFromCurrent;
    private DateTime firstDayOfWeek;
    private TextView[] dayHeaders;
    private static Button cancelSelect;
    private ClassesButtonWrapper[][] classes = new ClassesButtonWrapper[7][10];
    private static ArrayList<ClassesButtonWrapper> selectedButtons = new ArrayList<>();



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
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        firstDayOfWeek = formatter.parseDateTime("01/01/1990 00:00:00");
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
        final View view = inflater.inflate(R.layout.fragment_weekview, null);
        final Context cntxt = getContext();

        final Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
        final Toolbar toolbar1 = ((MainActivity)getActivity()).getToolbar1();
        final Toolbar toolbarCompleted = ((MainActivity)getActivity()).getToolbarCompleteClasses();
        final Toolbar toolbarCanceled = ((MainActivity) getActivity()).getToolbarCanceledClasses();
        Button cancelSelectCompleted = toolbarCompleted.findViewById(R.id.turnOff_complete);
        cancelSelectCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ((MainActivity) getActivity()).setSelectMode(false);
                toolbar.setVisibility(View.VISIBLE);
                toolbarCompleted.setVisibility(View.GONE);
                for (ClassesButtonWrapper b: selectedButtons) {
                    b.setUnselectBackground();
                }
                selectedButtons.clear();
            }
        });

        toolbarCanceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
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
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    cancelOnClick(toolbar,toolbar1);
                }
            });
        }
        Button deleteClasses = toolbar1.findViewById(R.id.delete_classes);
        Button completedClasses = toolbarCompleted.findViewById(R.id.completed_classes);
        completedClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                prepareCompletedClassesDialog();
            }
        });
        Button cancelCompletedClasses = toolbarCompleted.findViewById(R.id.uncompleted_classes);
        cancelCompletedClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                prepareCancelCompletedClassesDialog();
            }
        });
        Button cancelledClasses = toolbarCanceled.findViewById(R.id.canceled_classes);
        cancelledClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                prepareCancelledClassesDialog();
            }
        });
        Button uncancelledClasses = toolbarCanceled.findViewById(R.id.uncancelled_classes);
        uncancelledClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                prepareUncancelledClasses();
            }
        });
        deleteClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                prepareDeleteDialog();
            }
        });
        Thread t = new Thread(){
            public void run(){


                for(int i = 0; i<7; i++){
                    for(int j=0; j<10; j++){
                        StringBuilder className = new StringBuilder("class");
                        className.append(j).append(i);
                        final int classDay = i;
                        final int classHour = j;
                        int classRes = getResources().getIdentifier(className.toString(),"id",getActivity().getPackageName());
                        classes[i][j] = new ClassesButtonWrapper((Button)view.findViewById(classRes),getContext());
                        classes[i][j].getBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                if(!((MainActivity) getActivity()).isSelectMode()) {
                                    Intent intent;
                                    if(classes[classDay][classHour].getBtn().getText().equals("")) {
                                        intent = new Intent(getActivity(), AddClass.class);

                                    } else{
                                        intent = new Intent(getActivity(), EditClass.class);
                                        //intent.putExtra("group",groupEnt);
                                        //intent.putExtra("subject",subjectEnt);
                                    }
                                    intent.putExtra("classDay", classDay);
                                    intent.putExtra("classHour", classHour);
                                    intent.putExtra("dayOfWeek",firstDayOfWeek.plusDays(classDay));
                                    startActivityForResult(intent,1);


                                } else if(!classes[classDay][classHour].getBtn().getText().equals("") && !classes[classDay][classHour].isSelected()){
                                    classes[classDay][classHour].setSelectBackground();
                                    classes[classDay][classHour].setSelected(true);
                                    selectedButtons.add(classes[classDay][classHour]);
                                } else if (classes[classDay][classHour].isSelected()){
                                    classes[classDay][classHour].setUnselectBackground();
                                    selectedButtons.remove(classes[classDay][classHour]);
                                    if(selectedButtons.size()==0){
                                        cancelSelect.performClick();
                                        //((MainActivity) getActivity()).setSelectMode(false);
                                        //toolbar button placeholder
                                    }
                                }
                            }
                        });
                        classes[i][j].getBtn().setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if(!((MainActivity) getActivity()).isSelectMode() && !classes[classDay][classHour].getBtn().getText().equals("")) {
                                    classes[classDay][classHour].setSelectBackground();
                                    selectedButtons.add(classes[classDay][classHour]);
                                    classes[classDay][classHour].setSelected(true);
                                    ((MainActivity) getActivity()).setSelectMode(true);
                                    toolbar.setVisibility(View.GONE);
                                    toolbar1.setVisibility(View.VISIBLE);
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                }
            }

        };
        t.start();

        if(firstDayOfWeek.equals(DateTime.now().withDayOfWeek(DateTimeConstants.MONDAY).withHourOfDay(0).withMinuteOfHour(0).withMillisOfDay(0))){
            TextView dayOfWeekHeader[] = getCurrentDayHeader(view, DateTime.now().getDayOfWeek());
            dayOfWeekHeader[0].setBackground(ContextCompat.getDrawable(cntxt,R.drawable.grid_today));
            dayOfWeekHeader[0].setTextColor(ContextCompat.getColor(cntxt,R.color.menuTextColor));
            dayOfWeekHeader[1].setBackground(ContextCompat.getDrawable(cntxt,R.drawable.grid_today));
            dayOfWeekHeader[1].setTextColor(ContextCompat.getColor(cntxt,R.color.menuTextColor));
        }
        DateTime firstDayOfWeekCopy;
        for(int i = 0; i<7;i++){
            String viewDayHeader = "day"+i;
            firstDayOfWeekCopy = firstDayOfWeek.plusDays(i);
            int dayRes = getResources().getIdentifier(viewDayHeader,"id",getActivity().getPackageName());
            dayHeaders[i] = (TextView) view.findViewById(dayRes);
            String dayOfMonth = Integer.toString(firstDayOfWeekCopy.getDayOfMonth());
            String monthOfYear = Integer.toString(firstDayOfWeekCopy.getMonthOfYear());
            StringBuilder sb = new StringBuilder();
            sb = (dayOfMonth.length()==1 ? sb.append("0").append(dayOfMonth):sb.append(dayOfMonth));
            //sb = (monthOfYear.length()==1 ? sb.append("0").append(monthOfYear):sb.append(monthOfYear));
            dayHeaders[i].setText(sb.toString());
        }
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void prepareUncancelledClasses() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_cut_classes_template);
        builder.setMessage(R.string.popup_uncut_classes_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                //onDeleteClassesAcceptClick();
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

    private void prepareCancelledClassesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_cut_classes_template);
        builder.setMessage(R.string.popup_cut_classes_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //onDeleteClassesAcceptClick();
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

    private void prepareCancelCompletedClassesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_complete_classes_template);
        builder.setMessage(R.string.popup_uncomplete_classes_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                //onDeleteClassesAcceptClick();
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

    private void prepareCompletedClassesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_complete_classes_template);
        builder.setMessage(R.string.popup_complete_classes_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                // onDeleteClassesAcceptClick();
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

    private void prepareDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_delete_classes_template);
        builder.setMessage(R.string.popup_delete_classes_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onDeleteClassesAcceptClick();
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

    private void onDeleteClassesAcceptClick() {
        for (ClassesButtonWrapper b : selectedButtons) {
            b.clearButtonContent();
        }
        selectedButtons.clear();
        cancelSelect.performClick();
    }

    //handle data from activities here
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,resultCode+""+requestCode);
        if(resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case 1:
                    String grpName = data.getStringExtra("groupName");
                    int classDay = data.getIntExtra("classDay",0);
                    int classHour = data.getIntExtra("classHour",0);
                    classes[classDay][classHour].getBtn().setText(grpName);
                    Drawable drawable = new DrawableBuilder().rectangle().solidColor(getResources().getColor(android.R.color.holo_green_light)).strokeColor(R.color.appDefaultBlack).strokeWidth(1).build();
                    //set your own background color here
                    classes[classDay][classHour].setSelectBackground(drawable);
                    Log.d(TAG,grpName+" "+classDay+" "+classHour);
                    break;
                default:
                    return;
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            StringBuilder title = new StringBuilder();
            title.append(monthNumberToString(firstDayOfWeek.getMonthOfYear())).append(", ").append(firstDayOfWeek.getYear());
            Log.d(TAG, firstDayOfWeek.toString());
            ((MainActivity)getActivity()).setActionBarTitle(title.toString());
        }
    }
}
