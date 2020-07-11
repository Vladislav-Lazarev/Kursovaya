package com.hpcc.kursovaya.ui.subjects;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;

public class SubjectsFragment extends Fragment {
    private static final String TAG = SubjectsFragment.class.getSimpleName();

    boolean isCreatedAlready = false;
    private View root;
    private ListView listView;
    private SubjectListAdapter adapter;
    private List<Subject> subjectList;
    private long mLastClickTime = 0;
    private View filterSubjectView;
    private View sortSubjectView;
    private Spinner spinnerSpeciality;
    private Spinner spinnerCourse;
    private Spinner spinnerGroup;
    private AlertDialog sortDialogSubject;
    private TextView listEmpty;
    private View coverView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup  container, Bundle savedInstanceState) {
        LocaleManager.setLocale(getActivity());


        if(!isCreatedAlready) {
            root = inflater.inflate(R.layout.fragment_subjects, container, false);
            listView = root.findViewById(R.id.subjectsLSV);
            listEmpty = root.findViewById(R.id.listEmptyFirstPart);
            coverView = root.findViewById(R.id.emptyList);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(getString(R.string.emprty_list))
                    .append(" ",new ImageSpan(getActivity(),R.mipmap.ic_add_white),0);
            listEmpty.setText(builder);
            Context context = getActivity();

            final Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
            final Toolbar toolbarSearch = ((MainActivity) getActivity()).getToolbarSearch();
            EditText textSearch = toolbarSearch.findViewById(R.id.textView_search);
            textSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    subjectList.clear();
                    adapter.notifyDataSetChanged();
                    if (s.toString().isEmpty()){
                        List<Subject> copyList = DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class, ConstantApplication.NAME));
                        subjectList.addAll(copyList);
                    } else {
                        final RealmResults<Subject> subjectAll = DBManager.readAll(Subject.class, ConstantApplication.NAME);

                        for (Subject subject : subjectAll){
                            if (subject.getName().trim().toLowerCase().contains(s.toString().trim().toLowerCase())){
                                subjectList.add(DBManager.copyObjectFromRealm(subject));
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
                subjectList.clear();
                subjectList.addAll(DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class, ConstantApplication.NAME)));
                adapter.notifyDataSetChanged();
            });
            turnOffSearch.setOnClickListener(v ->{
                toolbar.setVisibility(View.VISIBLE);
                toolbarSearch.setVisibility(View.GONE);
                subjectList.clear();
                subjectList.addAll(DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class, ConstantApplication.NAME)));
                adapter.notifyDataSetChanged();
                hideKeyboardFrom(getActivity(),root);
            });
            FloatingActionButton button = root.findViewById(R.id.fab);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                        return;
                    }
                    if(DBManager.readAll(Speciality.class).size()!=0) {
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Intent intent = new Intent(getActivity(), AddSubjectActivity.class);
                        startActivityForResult(intent, ConstantApplication.ACTIVITY_ADD);
                    } else {
                        Toast.makeText(context, R.string.toast_fragment_no_specialities,Toast.LENGTH_LONG).show();
                    }
                }
            });

            subjectList = new ArrayList<>();
            List<Subject> copyList = DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class, ConstantApplication.NAME));
            subjectList.addAll(copyList);
            adapter = new SubjectListAdapter(getActivity(), R.layout.list_view_item_subject, subjectList);
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
                    Subject entry = (Subject) parent.getItemAtPosition(position);

                    Log.d("TAG", "entry = " + entry.toString());
                    Intent intent = new Intent(getActivity(), EditSubjectActivity.class);
                    intent.putExtra(String.valueOf(ConstantApplication.ACTIVITY_EDIT), entry);
                    startActivityForResult(intent, ConstantApplication.ACTIVITY_EDIT);
                }
            });
            listView.setItemsCanFocus(false);
        }
        isCreatedAlready=true;
        setActionBarTitle();
        setHasOptionsMenu(true);
        if(!subjectList.isEmpty()){
            coverView.setVisibility(View.GONE);
            listEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            listEmpty.setVisibility(View.VISIBLE);
            coverView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        return root;
    }

    public void setActionBarTitle(){
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.menu_subjects));
        ((MainActivity) getActivity()).showOverflowMenu(false);
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
            Subject subject = data.getParcelableExtra(strParcelableExtra);
            adapter.write(subject);
            adapter.update(ConstantApplication.NAME);
            listEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void prepareDeleteDialog(final ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_delete_subject_title);
        builder.setMessage(R.string.popup_delete_subject_content);
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
                        Log.d(TAG, "entity = " + subjectList.get(key));
                        adapter.delete(subjectList.get(key));
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_subject, menu);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        List<Subject> subjectList = DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class));

        if(subjectList.size() == ConstantApplication.ZERO){
            Toast.makeText(getContext(), R.string.toast_fragment_no_subjects, Toast.LENGTH_LONG).show();
            return true;
        }

        switch (item.getItemId()){
            case R.id.filter_subject:
                onClickPrepareFilterSubject();
                return true;
            case R.id.sort_subject:
                onClickPrepareSortSubject();
                return true;
        }
        return false;
    }

    private void onClickPrepareFilterSubject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_filter_subject);
        builder.setPositiveButton(R.string.popup_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                String specialityName = String.valueOf(spinnerSpeciality.getSelectedItem());
                String course = String.valueOf(spinnerCourse.getSelectedItem());
                List<Subject> subjectList = new ArrayList<>();
                List<Subject> findSubjectList = new ArrayList<>();

                if(course.equals(getString(R.string.spinner_all))){
                    subjectList = DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class));
                }else{
                    subjectList = DBManager.copyObjectFromRealm(
                            DBManager.readAll(Subject.class, ConstantApplication.NUMBER_COURSE, Integer.parseInt(course)));
                }

                if(!specialityName.equals(getString(R.string.spinner_all))){
                    for(Subject subject: subjectList){
                       if (subject.getSpecialityList().contains(new Speciality().setName(specialityName))){
                           findSubjectList.add(subject);
                       }
                    }
                }

                adapter.clear();

                adapter.addAll(subjectList);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        filterSubjectView = getLayoutInflater().inflate(R.layout.dialog_filter_subject, null);
        builder.setView(filterSubjectView);

        List<String> specialityNameList = new ArrayList<>();

        List<Speciality> specialityList = DBManager.copyObjectFromRealm(DBManager.readAll(Speciality.class));

        List<Subject> subjectList = DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class));

        for (Subject subject: subjectList){
            for(Speciality speciality: specialityList){
                if(subject.getSpecialityList().contains(speciality) &&
                        !specialityNameList.contains(speciality.getName())){
                    specialityNameList.add(speciality.getName());
                }
            }
        }

        if(specialityNameList.size() > 1){
            specialityNameList.add(getString(R.string.spinner_all));
        }

        spinnerSpeciality =
                ConstantApplication.fillingSpinner(getContext(), filterSubjectView.findViewById(R.id.spinnerSpeciality),
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

    private void onClickPrepareSortSubject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_sort_subject);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        sortSubjectView = getLayoutInflater().inflate(R.layout.dialog_sort_subject, null);
        builder.setView(sortSubjectView);

        List<RadioButton> sortBtnList = new ArrayList<>();

        sortBtnList.add(sortSubjectView.findViewById(R.id.ascName));
        sortBtnList.add(sortSubjectView.findViewById(R.id.descName));
        sortBtnList.add(sortSubjectView.findViewById(R.id.ascCourse));
        sortBtnList.add(sortSubjectView.findViewById(R.id.descCourse));
        sortBtnList.add(sortSubjectView.findViewById(R.id.ascSpeciality));
        sortBtnList.add(sortSubjectView.findViewById(R.id.descSpeciality));

        for(RadioButton sortBtn: sortBtnList){
            sortBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                        return;
                    }

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

                    List<Subject> sortSubjectList = new ArrayList<>();

                    if(fieldName.equals(ConstantApplication.ID_SPECIALITY)){
                        fieldName = ConstantApplication.NAME;
                        List<Speciality> specialityList = DBManager.copyObjectFromRealm(
                                DBManager.readAll(Speciality.class, fieldName, sort));
                        List<Subject> subjectList = DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class));

                        for(Speciality speciality: specialityList){
                            for(Subject subject: subjectList){
                                if(subject.getSpecialityList().contains(speciality)){
                                    if(sortSubjectList.contains(subject)){
                                        int index = sortSubjectList.indexOf(subject);
                                        sortSubjectList.remove(subject);
                                        sortSubjectList.add(index, subject);
                                    }
                                    else{
                                        sortSubjectList.add(subject);
                                    }
                                }
                            }
                        }
                    }else{
                        sortSubjectList = DBManager.copyObjectFromRealm(
                                DBManager.readAll(Subject.class, fieldName, sort));
                    }

                    adapter.clear();

                    adapter.addAll(sortSubjectList);

                    sortDialogSubject.cancel();
                }
            });
        }

        sortDialogSubject = builder.create();
        sortDialogSubject.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                sortDialogSubject.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        sortDialogSubject.show();
        Button positiveButton = sortDialogSubject.getButton(AlertDialog.BUTTON_POSITIVE);
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
                List<String>  courseList = new ArrayList<>();
                List<Subject> subjectList = DBManager.copyObjectFromRealm(
                        DBManager.readAll(Subject.class, ConstantApplication.NUMBER_COURSE, Sort.ASCENDING));
                List<Subject> findSubjectList = new ArrayList<>();

                if(!item.equals(getString(R.string.spinner_all))){
                    Speciality speciality = DBManager.copyObjectFromRealm(DBManager.read(Speciality.class, ConstantApplication.NAME, item));

                    for(Subject subject: subjectList){
                        if(subject.getSpecialityList().contains(speciality)){
                            findSubjectList.add(subject);
                        }
                    }
                }else{
                    findSubjectList.addAll(subjectList);
                }

                for (Subject subject: findSubjectList){
                    if(!courseList.contains(Integer.toString(subject.getNumberCourse()))){
                        courseList.add(Integer.toString(subject.getNumberCourse()));
                    }
                }

                spinnerCourse =
                        ConstantApplication.fillingSpinner(getContext(), filterSubjectView.findViewById(R.id.spinnerCourse),
                                courseList);
                ConstantApplication.setSpinnerText(spinnerCourse, String.valueOf(spinnerCourse.getSelectedItem()));
                listenerSpinnerCourse(spinnerCourse);

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void listenerSpinnerCourse(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                String itemSpeciality = String.valueOf(spinnerSpeciality.getSelectedItem());
                List<Group> groupList = new ArrayList<>();
                List<String> groupNameList = new ArrayList<>();
                List<String> fieldName = new ArrayList<>();
                List<Object> value = new ArrayList<>();


                if(!itemSpeciality.equals(getString(R.string.spinner_all))){
                     Speciality speciality = DBManager.copyObjectFromRealm(DBManager.read(Speciality.class, ConstantApplication.NAME, itemSpeciality));

                     fieldName.add(ConstantApplication.ID_SPECIALITY);
                     value.add(speciality.getId());
                }

                if(!item.equals(getString(R.string.spinner_all))){
                    int course = Integer.parseInt(item);

                    fieldName.add(ConstantApplication.NUMBER_COURSE);
                    value.add(course);
                }

                groupList = DBManager.copyObjectFromRealm(DBManager.readAll(Group.class, fieldName, value));

                for(Group group: groupList){
                    groupNameList.add(group.getName());
                }


                spinnerGroup = ConstantApplication.fillingSpinner(getContext(), filterSubjectView.findViewById(R.id.spinnerGroup),
                                groupNameList);

                ConstantApplication.setSpinnerText(spinnerGroup, String.valueOf(spinnerGroup.getSelectedItem()));


            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}