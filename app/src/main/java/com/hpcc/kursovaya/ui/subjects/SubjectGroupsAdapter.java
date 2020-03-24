package com.hpcc.kursovaya.ui.subjects;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hpcc.kursovaya.dao.entity.Group;

import java.util.List;

public class SubjectGroupsAdapter extends ArrayAdapter<Group> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private List<Group> groupList;
    private int totalHours = 0;
    private int readHours = 0;
    private int canceledHours = 0;
    private SparseBooleanArray mSelectedItemsIds;


    static class ViewHolder {
        TextView name;
        TextView speciality;
        TextView totalHours;
        TextView readHours;
        TextView canceledHours;
    }

    public SubjectGroupsAdapter(@NonNull Context context, int resource, @NonNull List<Group> objects) {
        super(context, resource, objects);
    }
}
