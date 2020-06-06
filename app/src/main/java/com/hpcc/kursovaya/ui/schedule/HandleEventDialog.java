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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hpcc.kursovaya.ClassesButton.ClassesButtonWrapper;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.schedule.AnotherEvent;
import com.hpcc.kursovaya.ui.schedule.WeekViewPager.WeekViewFragment;

import org.joda.time.DateTime;

public class HandleEventDialog extends DialogFragment {
    private static String TAG = HandleEventDialog.class.getSimpleName();
    private Context context;
    private int classHour;
    private int classDay;
    private DateTime dayOfWeek;
    private AnotherEvent event;
    private ClassesButtonWrapper classesButton;
    private long mLastClickTime = 0;


    public static HandleEventDialog newInstance(Context context, int classDay, int classHour, DateTime dayOfWeek, ClassesButtonWrapper classesButtonWrapper) {
        Bundle args = new Bundle();
        HandleEventDialog fragment = new HandleEventDialog();
        fragment.context = context;
        fragment.classHour = classHour;
        fragment.classDay = classDay;
        fragment.dayOfWeek = dayOfWeek;
        fragment.event = classesButtonWrapper.getEvent();
        fragment.classesButton = classesButtonWrapper;
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
                getResources().getString(R.string.edit_event),
                getResources().getString(R.string.delete_event)};
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, HANDLE_CLASS);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent;
                switch (position){
                    case 0:
                        AboutEventDialog eventDialog = AboutEventDialog.newInstance(context,event.getTemplateAnotherEvent().getTitle(),
                                event.getNote());
                        eventDialog.show(getParentFragmentManager(),"info");
                        break;
                    case 1:
                        intent = new Intent(getActivity(), EditEvent.class);
                        intent.putExtra("classDay", classDay);
                        intent.putExtra("classHour", classHour);
                        intent.putExtra("dayOfWeek", dayOfWeek);
                        intent.putExtra("currentEvent",event);
                        startActivityForResult(intent, WeekViewFragment.EDIT_EVENT);
                        break;
                    case 2:
                        prepareDeleteDialog();
                        break;
                    default:
                }
                if(position!=0) {
                    dismiss();
                }
                String selectedItem = HANDLE_CLASS[position];
                Log.i(TAG, selectedItem);
            }
        });
        builder.setView(view);
        return  builder.create();
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
