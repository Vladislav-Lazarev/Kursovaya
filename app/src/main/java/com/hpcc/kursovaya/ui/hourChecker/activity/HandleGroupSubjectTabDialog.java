package com.hpcc.kursovaya.ui.hourChecker.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.DayClassAdapter;
import com.hpcc.kursovaya.ui.schedule.HandleClassDialog;

import org.joda.time.DateTime;

public class HandleGroupSubjectTabDialog extends HandleClassDialog {
    public static final String TAG = HandleGroupSubjectTabDialog.class.getSimpleName();

    public static HandleGroupSubjectTabDialog newInstance(Context context, int classDay, int classHour, DateTime dayOfWeek,
                                                          AcademicHour currentCell, int secondClassHour, AcademicHour secondCell, DayClassAdapter adapter) {
        Bundle args = new Bundle();
        HandleGroupSubjectTabDialog fragment = new HandleGroupSubjectTabDialog();
        fragment.context = context;
        fragment.classHour = classHour;
        fragment.classDay = classDay;
        fragment.dayOfWeek = dayOfWeek;
        fragment.currentCell = currentCell;
        fragment.secondClassHour = secondClassHour;
        fragment.secondCell = secondCell;
        fragment.dayClassAdapter = adapter;
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
        final String[] HANDLE_CLASS = {
                getResources().getString(R.string.check_uncheck_as_read),
                getResources().getString(R.string.check_uncheck_as_canceled),
                getResources().getString(R.string.delete_class)
                /*getResources().getString(R.string.substitution)*/};
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, HANDLE_CLASS);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                switch (position){
                    case 0:
                        if(currentCell.hasCompleted()){
                            prepareCancelCompletedClassesDialog();
                        } else if(!currentCell.hasCanceled()) {
                            prepareCompletedClassesDialog();
                        } else {
                            Toast.makeText(getActivity(), R.string.class_cant_be_read ,Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 1:
                        if(currentCell.hasCanceled()){
                            prepareUncancelledClasses();
                        } else if(!currentCell.hasCompleted()){
                            prepareCancelledClassesDialog();
                        } else {
                            Toast.makeText(getActivity(), R.string.class_cant_be_eaded ,Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 2:
                        prepareDeleteDialog();
                        break;

                    default:
                }
                dismiss();
                String selectedItem = HANDLE_CLASS[position];
                Log.i(TAG, selectedItem);
            }
        });
        builder.setView(view);
        return  builder.create();
    }
}
