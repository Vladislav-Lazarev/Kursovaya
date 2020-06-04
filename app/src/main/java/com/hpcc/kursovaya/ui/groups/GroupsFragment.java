package com.hpcc.kursovaya.ui.groups;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Sort;

public class GroupsFragment extends Fragment {
    private static final String TAG = GroupsFragment.class.getSimpleName();

    private boolean isCreatedAlready = false;
    private View root;
    private ListView listView;
    private long mLastClickTime = 0;
    private GroupListAdapter adapter;
    private List<Group> groupList;
    private View filterGroupView;
    private View sortGroupView;
    private Spinner spinnerSpeciality;
    private Spinner spinnerCourse;
    private AlertDialog sortGroupDialog;

    public void setActionBarTitle(){
        ((MainActivity)getActivity()).setActionBarTitle(getContext().getString(R.string.menu_groups));
        ((MainActivity) getActivity()).showOverflowMenu(false);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LocaleManager.setLocale(getActivity());
        if(!isCreatedAlready) {

            //creating elements for listview
            root = inflater.inflate(R.layout.fragment_groups, container, false);
            listView = root.findViewById(R.id.groupLSV);
            final Toolbar toolbarSearch = ((MainActivity) getActivity()).getToolbarSearch();
            EditText textSearch = toolbarSearch.findViewById(R.id.textView_search);
            textSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();

                    if (s.toString().isEmpty()){
                        groupList = DBManager.copyObjectFromRealm(DBManager.readAll(Group.class, ConstantApplication.NAME));
                    } else {
                        groupList = DBManager.copyObjectFromRealm(DBManager
                                .search(Group.class, ConstantApplication.NAME, s.toString(),
                                        Case.INSENSITIVE, ConstantApplication.NAME, Sort.ASCENDING));
                    }
                    adapter.addAll(groupList);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            groupList = DBManager.copyObjectFromRealm(
                    DBManager.readAll(Group.class));
            adapter = new GroupListAdapter(getActivity(), R.layout.list_view_item_group, groupList);
            listView.setAdapter(adapter);

            final Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
            FloatingActionButton button = root.findViewById(R.id.fab);
            Context context = getActivity();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    if(DBManager.readAll(Speciality.class).size() != ConstantApplication.ZERO) {
                        Intent intent = new Intent(getActivity(), AddGroupActivity.class);
                        startActivityForResult(intent, ConstantApplication.ACTIVITY_ADD);
                    } else {
                        Toast.makeText(context, R.string.toast_fragment_no_specialities,Toast.LENGTH_LONG).show();
                    }
                }
            });

            isCreatedAlready = true;
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

                    Group entry = (Group) parent.getItemAtPosition(position);

                    Intent intent = new Intent(getActivity(), EditGroupActivity.class);
                    intent.putExtra(String.valueOf(ConstantApplication.ACTIVITY_EDIT), entry);
                    startActivityForResult(intent, ConstantApplication.ACTIVITY_EDIT);
                }
            });
        }
        isCreatedAlready=true;
        setActionBarTitle();
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_group, menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        List<Group> groupList = DBManager.copyObjectFromRealm(
                DBManager.readAll(Group.class));

        if(groupList.size() == ConstantApplication.ZERO){
            Toast.makeText(getContext(), R.string.toast_fragment_no_groups, Toast.LENGTH_LONG).show();
            return false;
        }

        switch (item.getItemId()){
            case R.id.filter_group:
                onClickPrepareFilterGroup();
                break;
            case R.id.sort_group:
                onClickPrepareSortGroup();
                break;
        }
        return false;
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
            Group group = data.getParcelableExtra(strParcelableExtra);
            adapter.write(group);
            adapter.update(ConstantApplication.NAME);
        }
    }

    private void onClickPrepareFilterGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_filter_group);
        builder.setPositiveButton(R.string.popup_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                String specialityName = String.valueOf(spinnerSpeciality.getSelectedItem());
                String course = String.valueOf(spinnerCourse.getSelectedItem());
                List<String> fieldName = new ArrayList<>();
                List<Object> value = new ArrayList<>();

                if(!specialityName.equals(getString(R.string.spinner_all))){
                    Speciality speciality = DBManager.copyObjectFromRealm(
                            DBManager.read(Speciality.class, ConstantApplication.NAME, specialityName));
                    fieldName.add(ConstantApplication.ID_SPECIALITY);
                    value.add(speciality.getId());
                }

                if(!course.equals(getString(R.string.spinner_all))){
                    fieldName.add(ConstantApplication.NUMBER_COURSE);
                    value.add(Integer.parseInt(course));
                }


                List<Group> groupList = new ArrayList<>();
                groupList.addAll(DBManager.copyObjectFromRealm(
                        DBManager.readAll(Group.class, fieldName, value)));


                adapter.clear();

                adapter.addAll(groupList);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        filterGroupView = getLayoutInflater().inflate(R.layout.dialog_filter_group, null);
        builder.setView(filterGroupView);

        List<String> specialityNameList = new ArrayList<>();

        List<Speciality> specialityList = DBManager.copyObjectFromRealm(
                DBManager.readAll(Speciality.class));

        List<Group> groupList = DBManager.copyObjectFromRealm(
                DBManager.readAll(Group.class));

        for (Group group: groupList){
            for (Speciality speciality: specialityList){
                if(group.getSpecialty().equals(speciality) &&
                        !specialityNameList.contains(speciality.getName())){
                    specialityNameList.add(speciality.getName());
                }
            }
        }


        if(specialityNameList.size() > 1){
            specialityNameList.add(getString(R.string.spinner_all));
        }

        spinnerSpeciality =
                ConstantApplication.fillingSpinner(getContext(), filterGroupView.findViewById(R.id.spinnerSpeciality),
                        specialityNameList);
        listenerSpinnerSpeciality(spinnerSpeciality);

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
    private void onClickPrepareSortGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_sort_group);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        sortGroupView = getLayoutInflater().inflate(R.layout.dialog_sort_group, null);
        builder.setView(sortGroupView);

        List<RadioButton> sortBtnList = new ArrayList<>();

        sortBtnList.add(sortGroupView.findViewById(R.id.ascName));
        sortBtnList.add(sortGroupView.findViewById(R.id.descName));
        sortBtnList.add(sortGroupView.findViewById(R.id.ascCourse));
        sortBtnList.add(sortGroupView.findViewById(R.id.descCourse));
        sortBtnList.add(sortGroupView.findViewById(R.id.ascSpeciality));
        sortBtnList.add(sortGroupView.findViewById(R.id.descSpeciality));

        for(RadioButton sortBtn: sortBtnList){
            sortBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    String fieldName = "";
                    Sort sort = Sort.ASCENDING;

                    switch (v.getId()){
                        case R.id.ascName:
                            fieldName = ConstantApplication.NAME;
                            sort = Sort.ASCENDING;
                            break;
                        case R.id.descName:
                            fieldName = ConstantApplication.NAME;
                            sort = Sort.DESCENDING;
                            break;
                        case R.id.ascCourse:
                            fieldName = ConstantApplication.NUMBER_COURSE;
                            sort = Sort.ASCENDING;
                            break;
                        case R.id.descCourse:
                            fieldName = ConstantApplication.NUMBER_COURSE;
                            sort = Sort.DESCENDING;
                            break;
                        case R.id.ascSpeciality:
                            fieldName = ConstantApplication.ID_SPECIALITY;
                            sort = Sort.ASCENDING;
                            break;
                        case R.id.descSpeciality:
                            fieldName = ConstantApplication.ID_SPECIALITY;
                            sort = Sort.DESCENDING;
                            break;
                    }

                    List<Group> groupList = new ArrayList<>();

                    if(fieldName.equals(ConstantApplication.ID_SPECIALITY)){
                        fieldName = ConstantApplication.NAME;
                        List<Speciality> specialityList = DBManager.copyObjectFromRealm(
                                DBManager.readAll(Speciality.class, fieldName, sort));

                        for(Speciality speciality: specialityList){
                            groupList.addAll(DBManager.copyObjectFromRealm(
                                    DBManager.readAll(Group.class, ConstantApplication.ID_SPECIALITY, speciality.getId())));
                        }
                    }else{
                        groupList = DBManager.copyObjectFromRealm(
                                DBManager.readAll(Group.class, fieldName, sort));
                    }

                    adapter.clear();

                    adapter.addAll(groupList);

                    sortGroupDialog.cancel();
                }
            });
        }


        sortGroupDialog = builder.create();
        sortGroupDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                sortGroupDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        sortGroupDialog.show();
        Button positiveButton = sortGroupDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void prepareDeleteDialog(final ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_delete_group_title);
        builder.setMessage(R.string.popup_delete_group_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                SparseBooleanArray positionDel = listView.getCheckedItemPositions();
                for (int i = 0; i < positionDel.size(); i++) {
                    int key = positionDel.keyAt(i);
                    if (positionDel.get(key)){
                        Log.d(TAG, "entity = " + groupList.get(key));
                        adapter.delete(groupList.get(key));
                    }
                }
                adapter.update(ConstantApplication.NAME);

                if (positionDel.size() == ConstantApplication.ONE){
                    Toast.makeText(getContext(), R.string.toast_del_entity, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.toast_del_many_entity, Toast.LENGTH_SHORT).show();
                }

                mode.finish();
            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mode.finish();
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

    private void listenerSpinnerSpeciality(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                List<String> courseList = new ArrayList<>();
                List<Group> groupList = new ArrayList<>();

                if(item.equals(getString(R.string.spinner_all))){
                    groupList = DBManager.copyObjectFromRealm(
                            DBManager.readAll(Group.class, ConstantApplication.NUMBER_COURSE, Sort.ASCENDING));
                }else{
                    Speciality speciality = DBManager.read(Speciality.class, ConstantApplication.NAME, item);
                    groupList = DBManager.copyObjectFromRealm(
                            DBManager.readAll(Group.class, ConstantApplication.ID_SPECIALITY, speciality.getId(), ConstantApplication.NUMBER_COURSE, Sort.ASCENDING));
                }

                if(groupList != null){
                    for(Group group: groupList){
                        if(!courseList.contains(Integer.toString(group.getNumberCourse()))){
                            courseList.add(Integer.toString(group.getNumberCourse()));
                        }
                    }
                }

                if(courseList.size() > 1){
                    courseList.add(getString(R.string.spinner_all));
                }

                spinnerCourse =
                        ConstantApplication.fillingSpinner(getContext(), filterGroupView.findViewById(R.id.spinnerCourse),
                                courseList);
                ConstantApplication.setSpinnerText(spinnerCourse, String.valueOf(spinnerCourse.getSelectedItem()));

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}