package com.hpcc.kursovaya.ui.subjects;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.util.List;

import io.realm.Case;
import io.realm.Sort;

public class SubjectsFragment extends Fragment {
    private static final String TAG = SubjectsFragment.class.getSimpleName();

    boolean isCreatedAlready = false;
    private View root;
    private ListView listView;
    private SubjectListAdapter adapter;
    private List<Subject> subjectList;
    private long mLastClickTime = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup  container, Bundle savedInstanceState) {
        LocaleManager.setLocale(getActivity());


        if(!isCreatedAlready) {
            root = inflater.inflate(R.layout.fragment_subjects, container, false);
            listView = root.findViewById(R.id.subjectsLSV);

            final Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
            Context context = getActivity();
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
                        subjectList = DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class, ConstantApplication.NAME));
                    } else {
                        subjectList = DBManager.copyObjectFromRealm(DBManager
                                .search(Subject.class, ConstantApplication.NAME, s.toString(),
                                        Case.INSENSITIVE, ConstantApplication.NAME, Sort.ASCENDING));
                    }
                    adapter.addAll(subjectList);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
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

            subjectList = DBManager.copyObjectFromRealm(DBManager.readAll(Subject.class, ConstantApplication.NAME));
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

}