package com.hpcc.kursovaya.ui.schedule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hpcc.kursovaya.ClassesButton.ClassesButtonWrapper;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.DayClassAdapter;
import com.hpcc.kursovaya.ui.schedule.WeekViewPager.WeekViewFragment;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class HandleClassDialog extends DialogFragment {
    private static String TAG = HandleClassDialog.class.getSimpleName();

    protected Context context;
    protected int classDay;
    protected int classHour;
    protected DateTime dayOfWeek;
    protected long mLastClickTime = 0;
    protected AcademicHour currentCell;
    protected int secondClassHour;
    protected AcademicHour secondCell;
    protected ClassesButtonWrapper classesButton;
    protected DayClassAdapter dayClassAdapter;
    private ArrayList<AcademicHour> academicHourList = new ArrayList<>();

    public static HandleClassDialog newInstance(Context context, int classDay, int classHour, DateTime dayOfWeek,
                                                AcademicHour currentCell, int secondClassHour, AcademicHour secondCell, ClassesButtonWrapper classesButtonWrapper) {
        Bundle args = new Bundle();
        HandleClassDialog fragment = new HandleClassDialog();
        fragment.context = context;
        fragment.classHour = classHour;
        fragment.classDay = classDay;
        fragment.dayOfWeek = dayOfWeek;
        fragment.currentCell = currentCell;
        fragment.secondClassHour = secondClassHour;
        fragment.secondCell = secondCell;
        fragment.classesButton = classesButtonWrapper;

        fragment.academicHourList.add(currentCell);
        if (secondCell != null){
            fragment.academicHourList.add(secondCell);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public static HandleClassDialog newInstance(Context context, int classDay, int classHour, DateTime dayOfWeek,
                                                AcademicHour currentCell, int secondClassHour, AcademicHour secondCell, DayClassAdapter adapter) {
        Bundle args = new Bundle();
        HandleClassDialog fragment = new HandleClassDialog();
        fragment.context = context;
        fragment.classHour = classHour;
        fragment.classDay = classDay;
        fragment.dayOfWeek = dayOfWeek;
        fragment.currentCell = currentCell;
        fragment.secondClassHour = secondClassHour;
        fragment.secondCell = secondCell;
        fragment.dayClassAdapter = adapter;

        fragment.academicHourList.add(currentCell);
        if (secondCell != null){
            fragment.academicHourList.add(secondCell);
        }

        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.handle);
        View view = View.inflate(context,R.layout.handle_class_dialog,null);
        ListView listView = view.findViewById(R.id.lsv);
        final String[] HANDLE_CLASS = { getResources().getString(R.string.details),
                getResources().getString(R.string.edit_class),
                getResources().getString(R.string.check_uncheck_as_read),
                getResources().getString(R.string.check_uncheck_as_canceled),
                getResources().getString(R.string.delete_class),
                getResources().getString(R.string.substitution)};
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, HANDLE_CLASS);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent;
                switch (position){
                    case 0:
                        DialogFragment dialogFragment = AboutClassDialog.newInstance(context, academicHourList.get(ConstantApplication.ZERO));
                        dialogFragment.show(getParentFragmentManager(),"info");
                        break;
                    case 1:
                        intent = new Intent(getActivity(), EditClass.class);
                        intent.putExtra("classDay", classDay);
                        intent.putExtra("classHour", classHour);
                        intent.putExtra("dayOfWeek", dayOfWeek);
                        intent.putParcelableArrayListExtra("academicHourList", academicHourList);
                        startActivityForResult(intent, WeekViewFragment.EDIT_CLASS);
                        break;
                    case 2:
                        if(classesButton.getAcademicHour().hasCompleted()){
                            prepareCancelCompletedClassesDialog();
                        } else if(!classesButton.getAcademicHour().hasCanceled()) {
                            prepareCompletedClassesDialog();
                        } else {
                            Toast.makeText(getActivity(), R.string.class_cant_be_read ,Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 3:
                        if(classesButton.getAcademicHour().hasCanceled()){
                            prepareUncancelledClasses();
                        } else if(!classesButton.getAcademicHour().hasCompleted()){
                            prepareCancelledClassesDialog();
                        } else {
                            Toast.makeText(getActivity(), R.string.class_cant_be_eaded ,Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 4:
                        prepareDeleteDialog();
                        break;

                    default:
                }
                if(position!=0){
                    dismiss();
                }
                String selectedItem = HANDLE_CLASS[position];
                Log.i(TAG, selectedItem);
            }
        });
        builder.setView(view);
        return  builder.create();
    }

    protected void prepareUncancelledClasses() {
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
                if(!classesButton.getAcademicHour().hasCompleted()) {
                    classesButton.setCanceled(false);
                } else {
                    Toast.makeText(getActivity(), R.string.class_cant_be_readed ,Toast.LENGTH_LONG).show();
                }
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

    protected void prepareCancelledClassesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_cut_classes_single_title);
        builder.setMessage(R.string.popup_cut_classes_single_content);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!classesButton.getAcademicHour().hasCompleted()) {
                    classesButton.setCanceled(true);
                } else {
                    Toast.makeText(getActivity(), R.string.class_cant_be_eaded ,Toast.LENGTH_LONG).show();
                }
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

    protected void prepareCancelCompletedClassesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_uncomplete_single_title);
        builder.setMessage(R.string.popup_uncomplete_single_content);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(classesButton.getAcademicHour().hasCompleted() && !classesButton.getAcademicHour().hasCanceled()) {
                    classesButton.setCompleted(false);
                } else {
                    Toast.makeText(getActivity(), R.string.class_cant_be_unread ,Toast.LENGTH_LONG).show();
                }
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

    protected void prepareCompletedClassesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_complete_single_title);
        builder.setCancelable(false);
        builder.setMessage(R.string.popup_complete_single_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(!classesButton.getAcademicHour().hasCanceled()) {
                    classesButton.setCompleted(true);
                } else {
                    Toast.makeText(getActivity(), R.string.class_cant_be_read ,Toast.LENGTH_LONG).show();
                }
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

    protected void prepareDeleteDialog() {
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
                classesButton.clearButtonContent();
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

}
