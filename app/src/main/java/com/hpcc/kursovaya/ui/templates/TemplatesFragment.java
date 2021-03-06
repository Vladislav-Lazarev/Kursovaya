package com.hpcc.kursovaya.ui.templates;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateScheduleWeek;
import com.hpcc.kursovaya.dao.query.DBManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class TemplatesFragment extends Fragment {
    private static final String TAG = TemplatesFragment.class.getSimpleName();

    boolean isCreatedAlready = false;
    ListView listView = null;
    private View root;
    private long mLastClickTime = 0;
    private Toolbar toolbar;
    private TextView listEmpty;
    private View coverView;
    TemplateListAdapter adapter;
    private List<TemplateScheduleWeek> scheduleWeekList;

    public void setActionBarTitle(){
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.menu_templates));
        ((MainActivity) getActivity()).showOverflowMenu(false);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        toolbar = ((MainActivity)getActivity()).getToolbar();
        setHasOptionsMenu(false);
        root = inflater.inflate(R.layout.fragment_templates, container, false);
        listView = root.findViewById(R.id.templatesLSV);
        listEmpty = root.findViewById(R.id.listEmptyFirstPart);
        coverView = root.findViewById(R.id.emptyList);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(getString(R.string.emprty_list))
                .append(" ",new ImageSpan(getActivity(),R.mipmap.ic_add_white),0);
        listEmpty.setText(builder);

        Context context = getActivity();
        getActivity().setTitle("");
        FloatingActionButton button = root.findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(DBManager.readAll(Speciality.class).size()!=0) {
                    if(DBManager.readAll(Group.class).size()!=0) {
                        if(DBManager.readAll(Subject.class).size()!=0) {
                            Intent intent = new Intent(getActivity(), AddTemplateActivity.class);
                            startActivityForResult(intent, ConstantApplication.ACTIVITY_ADD);
                        } else {
                            Toast.makeText(context, R.string.toast_fragment_no_subjects,Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context, R.string.toast_fragment_no_groups,Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, R.string.toast_fragment_no_specialities,Toast.LENGTH_LONG).show();
                }
            }
        });



        final Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
        final Toolbar toolbarSearch = ((MainActivity) getActivity()).getToolbarSearch();
        EditText textSearch = toolbarSearch.findViewById(R.id.textView_search);
        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scheduleWeekList.clear();
                adapter.notifyDataSetChanged();
                if (s.toString().isEmpty()){
                    List<TemplateScheduleWeek> copyList = DBManager.copyObjectFromRealm(DBManager.readAll(TemplateScheduleWeek.class, ConstantApplication.NAME));
                    scheduleWeekList.addAll(copyList);
                } else {
                    final RealmResults<TemplateScheduleWeek> subjectAll = DBManager.readAll(TemplateScheduleWeek.class, ConstantApplication.NAME);

                    for (TemplateScheduleWeek tmpl : subjectAll){
                        if (tmpl.getName().trim().toLowerCase().contains(s.toString().trim().toLowerCase())){
                            scheduleWeekList.add(DBManager.copyObjectFromRealm(tmpl));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ImageButton turnOffSearch = toolbarSearch.findViewById(R.id.turnOff_search);
        ImageButton clearButton = toolbarSearch.findViewById(R.id.clear_search);
        clearButton.setOnClickListener(v -> {
            textSearch.setText("");
            scheduleWeekList.clear();
            scheduleWeekList.addAll(DBManager.copyObjectFromRealm(DBManager.readAll(TemplateScheduleWeek.class, ConstantApplication.NAME)));
            adapter.notifyDataSetChanged();
        });
        turnOffSearch.setOnClickListener(v ->{
            toolbar.setVisibility(View.VISIBLE);
            toolbarSearch.setVisibility(View.GONE);
            scheduleWeekList.clear();
            scheduleWeekList.addAll(DBManager.copyObjectFromRealm(DBManager.readAll(TemplateScheduleWeek.class, ConstantApplication.NAME)));
            adapter.notifyDataSetChanged();
            hideKeyboardFrom(getActivity(),root);
        });

        scheduleWeekList = new ArrayList<>();
        scheduleWeekList.addAll(DBManager.copyObjectFromRealm(
                DBManager.readAll(TemplateScheduleWeek.class)));
        adapter = new TemplateListAdapter(getActivity(), R.layout.list_view_item_template, scheduleWeekList);
        listView.setAdapter(adapter);

        Thread fillListView = new Thread(this::listViewFiller);
        fillListView.run();
        try {
            fillListView.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!scheduleWeekList.isEmpty()){
            coverView.setVisibility(View.GONE);
            listEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            listEmpty.setVisibility(View.VISIBLE);
            coverView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        setActionBarTitle();
        return root;
    }

    public void listViewFiller(){
        adapter = new TemplateListAdapter(getActivity(), R.layout.list_view_item_template, scheduleWeekList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = listView.getCheckedItemCount();
                mode.setTitle(checkedCount +" "+getResources().getString(R.string.cab_select_text));
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
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                TemplateScheduleWeek entry = (TemplateScheduleWeek) parent.getItemAtPosition(position);

                Log.d(TAG, "entry = " + entry.toString());
                Intent intent = new Intent(getActivity(), EditTemplateActivity.class);
                intent.putExtra(String.valueOf(ConstantApplication.ACTIVITY_EDIT), entry);
                startActivityForResult(intent, ConstantApplication.ACTIVITY_EDIT);
            }
        });
        listView.setItemsCanFocus(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            String strParcelableExtra = "";
            switch (requestCode){
                case ConstantApplication.ACTIVITY_ADD:
                    strParcelableExtra = String.valueOf(ConstantApplication.ACTIVITY_ADD);
                    break;
                case ConstantApplication.ACTIVITY_EDIT:
                    strParcelableExtra = String.valueOf(ConstantApplication.ACTIVITY_EDIT);
                    break;

            }
            TemplateScheduleWeek scheduleWeek = data.getParcelableExtra(strParcelableExtra);
            adapter.write(scheduleWeek);
            adapter.update(ConstantApplication.NAME);
            listEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void prepareDeleteDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.delete_alert_header))
                .setMessage(R.string.delete_alert_msg)
                .setPositiveButton(R.string.delete_positive,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();

                                SparseBooleanArray positionDel = listView.getCheckedItemPositions();
                                for (int i = 0; i < positionDel.size(); i++) {
                                    int key = positionDel.keyAt(i);
                                    if (positionDel.get(key)){
                                        Log.d(TAG, "entity = " + scheduleWeekList.get(key));
                                        adapter.delete(scheduleWeekList.get(key));
                                    }
                                }
                                adapter.update(ConstantApplication.NAME);
                                if (adapter.getCount()==0){
                                    listEmpty.setVisibility(View.VISIBLE);
                                    coverView.setVisibility(View.VISIBLE);
                                    listView.setVisibility(View.GONE);
                                }
                                if (positionDel.size() == ConstantApplication.ONE){
                                    Toast.makeText(getContext(), R.string.toast_del_entity, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), R.string.toast_del_many_entity, Toast.LENGTH_SHORT).show();
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


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}