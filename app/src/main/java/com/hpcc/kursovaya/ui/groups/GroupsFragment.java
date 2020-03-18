package com.hpcc.kursovaya.ui.groups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import io.realm.RealmList;

public class GroupsFragment extends Fragment {
    private final static int ACTIVITY_ADD = 1;
    private final static int ACTIVITY_EDIT = 2;

    private boolean isCreatedAlready = false;
    private View root;

    private GroupListAdapter adapter;
    private RealmList<Group> groupList;

    {
        adapter = null;
        groupList = new RealmList<>();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if(!isCreatedAlready) {
            root = inflater.inflate(R.layout.fragment_groups, container, false);
            ListView listView = root.findViewById(R.id.groupLSV);
            // getActivity().setTitle("");

            FloatingActionButton button = root.findViewById(R.id.fab);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddGroupActivity.class);
                    startActivityForResult(intent, ACTIVITY_ADD);
                }
            });

            //creating elements for listview
            groupList = DBManager.readAll(Group.class);
            adapter = new GroupListAdapter(getActivity(), R.layout.list_view_item_group, groupList);
            listView.setAdapter(adapter);

            isCreatedAlready=true;
        }
        setActionBarTitle();

        return root;
    }

    public void setActionBarTitle(){
        ((MainActivity)getActivity()).setActionBarTitle(getContext().getString(R.string.menu_groups));
        ((MainActivity) getActivity()).showOverflowMenu(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case ACTIVITY_ADD:
                    Group group = data.getParcelableExtra("newGroup");
                    groupList.add(group);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    return;
            }
        }
    }
}