package com.hpcc.kursovaya.ui.groups;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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

import java.util.List;

public class GroupsFragment extends Fragment {
    private static final String TAG = GroupsFragment.class.getSimpleName();

    private boolean isCreatedAlready = false;
    private View root;
    private ListView listView;
    private long mLastClickTime = 0;
    private GroupListAdapter adapter;
    private List<Group> groupList;

    public void setActionBarTitle(){
        ((MainActivity)getActivity()).setActionBarTitle(getContext().getString(R.string.menu_groups));
        ((MainActivity) getActivity()).showOverflowMenu(false);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LocaleManager.setLocale(getActivity());
        if(!isCreatedAlready) {
            root = inflater.inflate(R.layout.fragment_groups, container, false);
            listView = root.findViewById(R.id.groupLSV);

            // getActivity().setTitle("");
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

                    if(DBManager.readAll(Speciality.class).size()!=0) {
                        Intent intent = new Intent(getActivity(), AddGroupActivity.class);
                        startActivityForResult(intent, ConstantApplication.ACTIVITY_ADD);
                    } else {
                        Toast.makeText(context, R.string.toast_fragment_no_specialities,Toast.LENGTH_LONG).show();
                    }
                }
            });

            //creating elements for listview
            groupList = DBManager.copyObjectFromRealm(
                    DBManager.readAll(Group.class));
            adapter = new GroupListAdapter(getActivity(), R.layout.list_view_item_group, groupList);
            listView.setAdapter(adapter);

            isCreatedAlready=true;
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

        return root;
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

}