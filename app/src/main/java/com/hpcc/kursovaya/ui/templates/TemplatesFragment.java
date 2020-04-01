package com.hpcc.kursovaya.ui.templates;

import android.app.Activity;
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
import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.query.DBManager;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateScheduleWeek;

import java.util.List;

public class TemplatesFragment extends Fragment {
    boolean isCreatedAlready = false;
    private View root;
    private long mLastClickTime = 0;
    private List<TemplateScheduleWeek> listTemplateWeekList;
    private TemplateListAdapter adapter;

    public void setActionBarTitle(){
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.menu_templates));
        ((MainActivity) getActivity()).showOverflowMenu(false);
    }

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
                    if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent intent = new Intent(getActivity(), AddTemplateActivity.class);
                    startActivityForResult(intent, ConstantApplication.ACTIVITY_ADD);
                }
            });

            listTemplateWeekList = DBManager.copyObjectFromRealm(DBManager.readAll(TemplateScheduleWeek.class));
            adapter = new TemplateListAdapter(getActivity(), R.layout.list_view_item_template, listTemplateWeekList);
            listView.setAdapter(adapter);
            isCreatedAlready = true;
        }
        setActionBarTitle();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode== Activity.RESULT_OK){
            TemplateScheduleWeek templateScheduleWeek;
            switch (requestCode){
                case ConstantApplication.ACTIVITY_ADD:
                    templateScheduleWeek = data.getParcelableExtra("addTemplateScheduleWeek");
                    DBManager.write(templateScheduleWeek);

                    listTemplateWeekList.add(templateScheduleWeek);
                    break;
                case ConstantApplication.ACTIVITY_EDIT:
                    int posOldGroupTemplateScheduleWeek = data.getIntExtra("posOldTemplateScheduleWeek",0);
                    templateScheduleWeek = data.getParcelableExtra("editTemplateScheduleWeek");
                    DBManager.write(templateScheduleWeek);

                    listTemplateWeekList.set(posOldGroupTemplateScheduleWeek, templateScheduleWeek);
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    }

}