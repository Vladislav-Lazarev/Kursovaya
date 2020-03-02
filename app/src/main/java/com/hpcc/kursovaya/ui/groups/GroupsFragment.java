package com.hpcc.kursovaya.ui.groups;

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
import com.hpcc.kursovaya.dao.entity.Course;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Specialty;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {
    private boolean isCreatedAlready = false;
    private View root;

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
                    startActivity(intent);
                }
            });

            //creating elements for listview
            Group P61 = new Group("П-61", new Course().setNumber(3), new Specialty("РПЗ", 8));
            Group P62 = new Group("П-611111", new Course().setNumber(5), new Specialty("Йцу", 8));
            Group P63 = new Group("П-612222", new Course().setNumberCourse(1), new Specialty("Пасв", 8));

            List<Group> groupList = new ArrayList<>();
            groupList.add(P61);
            groupList.add(P62);
            groupList.add(P63);

            GroupListAdapter adapter = new GroupListAdapter(getActivity(), R.layout.list_view_item_group, groupList);
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
}