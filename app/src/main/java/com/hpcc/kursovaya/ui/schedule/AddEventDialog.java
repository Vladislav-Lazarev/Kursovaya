package com.hpcc.kursovaya.ui.schedule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.ui.schedule.WeekViewPager.WeekViewFragment;

import org.joda.time.DateTime;

import static com.hpcc.kursovaya.ui.schedule.WeekViewPager.WeekViewFragment.ADD_CLASS;

public class AddEventDialog extends DialogFragment {
    private static String TAG= AddEventDialog.class.getSimpleName();

    private Context context;
    private int classDay;
    private int classHour;
    private DateTime dayOfWeek;
    private long mLastClickTime = 0;


    public static AddEventDialog newInstance(Context context, int classDay, int classHour, DateTime dayOfWeek) {
        Bundle args = new Bundle();
        AddEventDialog fragment = new AddEventDialog();
        fragment.context = context;
        fragment.classHour = classHour;
        fragment.classDay = classDay;
        fragment.dayOfWeek = dayOfWeek;
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.add);
        View view = View.inflate(context,R.layout.add_new_event_dialog,null);
        ListView listView = view.findViewById(R.id.lsv);
        final String[] ADD_EVENT = { getResources().getString(R.string.add_class),
                getResources().getString(R.string.add_event)};
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, ADD_EVENT);
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
                        intent = new Intent(context, AddClass.class);
                        intent.putExtra("classDay", classDay);
                        intent.putExtra("classHour", classHour);
                        intent.putExtra("dayOfWeek", dayOfWeek);
                        startActivityForResult(intent, ADD_CLASS);
                        break;
                    case 1:
                        intent = new Intent(context, AddEvent.class);
                        intent.putExtra("classDay", classDay);
                        intent.putExtra("classHour", classHour);
                        intent.putExtra("dayOfWeek", dayOfWeek);
                        startActivityForResult(intent, WeekViewFragment.ADD_EVENT);
                        break;
                    default:
                }
                String selectedItem = ADD_EVENT[position];
                Log.i(TAG, selectedItem);
            }
        });
        builder.setView(view);
        return  builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        dismiss();
        if(getTargetFragment()!=null){
            getTargetFragment().onActivityResult(requestCode,resultCode,data);}
    }
}
