package com.hpcc.kursovaya.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.ui.settings.alarms.AlarmsActivity;
import com.hpcc.kursovaya.ui.settings.backup.BackupActivity;
import com.hpcc.kursovaya.ui.settings.language.LanguageActivity;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;
import com.hpcc.kursovaya.ui.settings.specialities.SpecialitiesActivity;

public class SettingsFragment extends Fragment{
    boolean isCreatedAlready = false;
    private View root;
    private final String TAG = SettingsFragment.class.getSimpleName();
    private long mLastClickTime = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocaleManager.setLocale(getActivity());
        if(!isCreatedAlready) {
            root = inflater.inflate(R.layout.fragment_settings, container, false);
            final String[] SETTINGS = { getResources().getString(R.string.schedule_alarm_title),
                    getResources().getString(R.string.backup_title),
                    getResources().getString(R.string.language_title)};
            ListView lsv = root.findViewById(R.id.settingsLSV);
            ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), R.layout.listview_item_settings, SETTINGS);


            lsv.setAdapter(adapter);
            lsv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    switch (position){
                        case 0:
                            Intent alarmInt = new Intent(getActivity(), AlarmsActivity.class);
                            startActivity(alarmInt);
                            break;
                        case 1:
                            Intent backInt = new Intent(getActivity(), BackupActivity.class);
                            startActivity(backInt);
                            break;
                        case 2:
                            Intent langInt = new Intent(getActivity(), LanguageActivity.class);
                            startActivity(langInt);
                            break;

                        default:
                    }
                    String selectedItem = SETTINGS[position];
                    Log.d(TAG, selectedItem);
                }
            });
            isCreatedAlready=true;
        }
        setActionBarTitle();
        return root;
    }

    public void setActionBarTitle() {
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.menu_settings));
        ((MainActivity) getActivity()).showOverflowMenu(false);
    }
}