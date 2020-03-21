package com.hpcc.kursovaya.ui.subjects;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import io.realm.RealmList;

public class SubjectsFragment extends Fragment {

    boolean isCreatedAlready = false;
    private View root;
    private ListView listView;
    private SubjectListAdapter adapter;
    private RealmList<Subject> subjectList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(!isCreatedAlready) {
            root = inflater.inflate(R.layout.fragment_subjects, container, false);
            listView = root.findViewById(R.id.subjectsLSV);
            FloatingActionButton button = root.findViewById(R.id.fab);
            final Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddSubjectActivity.class);
                    startActivityForResult(intent, ConstantEntity.ACTIVITY_ADD);
                }
            });

            subjectList = DBManager.readAll(Subject.class, ConstantEntity.ID);
            adapter = new SubjectListAdapter(getActivity(), R.layout.list_view_item_subject, subjectList);
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
                    Subject entry = (Subject) parent.getItemAtPosition(position);

                    Log.d("TAG", "entry = " + entry.toString());
                    Intent intent = new Intent(getActivity(), EditSubjectActivity.class);
                    intent.putExtra("posOldSubject", position);
                    intent.putExtra("editSubject", entry);

                    startActivityForResult(intent, ConstantEntity.ACTIVITY_EDIT);
                }
            });
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
            Subject subject;
            switch (requestCode){
                case ConstantEntity.ACTIVITY_ADD:
                    subject = data.getParcelableExtra("addSubject");

                    subjectList.add(subject);
                    adapter.notifyDataSetChanged();
                    return;
                case ConstantEntity.ACTIVITY_EDIT:
                    int posOldSubject = data.getIntExtra("posOldSubject",0);
                    subject = data.getParcelableExtra("editSubject");

                    subjectList.set(posOldSubject, subject);
                    adapter.notifyDataSetChanged();
                    return;
                default:

                    return;
            }
        }
    }

    private void prepareDeleteDialog(final ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.popup_delete_subject_title);
        builder.setMessage(R.string.popup_delete_subject_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SparseBooleanArray selected = adapter
                        .getSelectedIds();
                for (int i = (selected.size() - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        Subject selecteditem = adapter
                                .getItem(selected.keyAt(i));
                        // Remove selected items following the ids
                        adapter.remove(selecteditem);
                    }
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