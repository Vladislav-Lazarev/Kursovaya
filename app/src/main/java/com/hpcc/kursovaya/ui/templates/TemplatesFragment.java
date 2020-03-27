package com.hpcc.kursovaya.ui.templates;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;

import java.util.ArrayList;

public class TemplatesFragment extends Fragment {
    boolean isCreatedAlready = false;
    private View root;
    private long mLastClickTime = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(!isCreatedAlready) {
            setHasOptionsMenu(false);
            root = inflater.inflate(R.layout.fragment_templates, container, false);
            ListView listView = root.findViewById(R.id.templatesLSV);
            getActivity().setTitle("");
            FloatingActionButton button = root.findViewById(R.id.fab);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();                    Intent intent = new Intent(getActivity(), AddTemplateActivity.class);
                    startActivity(intent);
                }
            });

            TemplateEntity sample = new TemplateEntity("SampleName");
            ArrayList<TemplateEntity> listTemplates = new ArrayList<>();
            listTemplates.add(sample);
            TemplateListAdapter templateListAdapter = new TemplateListAdapter(getActivity(), R.layout.list_view_item_template, listTemplates);
            listView.setAdapter(templateListAdapter);
            isCreatedAlready =true;
        }
        setActionBarTitle();
        return root;
    }
    public void setActionBarTitle(){
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.menu_templates));
        ((MainActivity) getActivity()).showOverflowMenu(false);
    }
}
