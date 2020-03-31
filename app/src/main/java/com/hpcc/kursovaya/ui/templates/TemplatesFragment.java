package com.hpcc.kursovaya.ui.templates;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateScheduleWeek;

import java.util.ArrayList;

public class TemplatesFragment extends Fragment {
    boolean isCreatedAlready = false;
    ListView listView = null;
    private View root;
    TemplateListAdapter adapter = null;
    private long mLastClickTime = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
        if(!isCreatedAlready) {
            setHasOptionsMenu(false);
            root = inflater.inflate(R.layout.fragment_templates, container, false);
            listView = root.findViewById(R.id.templatesLSV);
            getActivity().setTitle("");
            FloatingActionButton button = root.findViewById(R.id.fab);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent intent = new Intent(getActivity(), AddTemplateActivity.class);
                    startActivity(intent);
                }
            });

            ArrayList<TemplateScheduleWeek> listTemplates = new ArrayList<>();
            adapter = new TemplateListAdapter(getActivity(), R.layout.list_view_item_template, listTemplates);
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    final int checkedCount = listView.getCheckedItemCount();
                    mode.setTitle(checkedCount +" "+getResources().getString(R.string.cab_select_text));
                    adapter.toggleSelection(position);
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    toolbar.setVisibility(View.GONE);
                    mode.getMenuInflater().inflate(R.menu.activity_listview, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete:
                            prepareDeleteDialog(mode);
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    toolbar.setVisibility(View.VISIBLE);
                    adapter.removeSelection();
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    TemplateScheduleWeek entry = (TemplateScheduleWeek) parent.getItemAtPosition(position);

                    Log.d("TAG", "entry = " + entry.toString());
                    Intent intent = new Intent(getActivity(), EditTemplateActivity.class);
                    intent.putExtra("posOfTemplateInList", position);
                    intent.putExtra("template", entry);

                    startActivityForResult(intent, ConstantEntity.ACTIVITY_EDIT);
                }
            });
            isCreatedAlready =true;
        }
        setActionBarTitle();
        return root;
    }

    private void prepareDeleteDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.delete_alert_header))
                .setMessage(R.string.delete_alert_msg)
                .setPositiveButton(R.string.delete_positive,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                SparseBooleanArray selected = adapter.getSelectedIds();
                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                    if (selected.valueAt(i)) {
                                        TemplateScheduleWeek selecteditem = adapter
                                                .getItem(selected.keyAt(i));
                                        // Remove selected items following the ids
                                        adapter.remove(selecteditem);
                                    }
                                }
                                mode.finish();
                            }
                        })
                .setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        alert.show();
    }

    public void setActionBarTitle(){
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.menu_templates));
        ((MainActivity) getActivity()).showOverflowMenu(false);
    }
}
