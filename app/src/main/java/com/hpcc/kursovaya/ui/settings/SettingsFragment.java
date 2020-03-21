package com.hpcc.kursovaya.ui.settings;

import android.content.Intent;
import android.os.Bundle;
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
import com.hpcc.kursovaya.ui.settings.specialities.SpecialitiesActivity;

public class SettingsFragment extends Fragment{
    boolean isCreatedAlready = false;
    private View root;
    private final String TAG = "SettingsFragment";
    private final String[] SETTINGS = { "Розклад дзвінків","Спеціальності", "Мова", "Про програму"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if(!isCreatedAlready) {
            root = inflater.inflate(R.layout.fragment_settings, container, false);
            ListView lsv = root.findViewById(R.id.settingsLSV);
            ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), R.layout.listview_item_settings, SETTINGS);


            lsv.setAdapter(adapter);
            lsv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        default:
                            Intent specInt = new Intent(getActivity(), SpecialitiesActivity.class);
                            startActivity(specInt);
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