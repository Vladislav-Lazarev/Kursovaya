package com.hpcc.kursovaya.ui.groups;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import java.util.List;

public class GroupListAdapter extends ArrayAdapter<Group> {
    private static final String TAG = "GroupListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private List<Group> groupList;
    private SparseBooleanArray mSelectedItemsIds;


    static class ViewHolder {
        TextView speciality;
        TextView course;
        TextView name;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        Group group = new Group(
                getItem(position).getName(),
                getItem(position).getSpecialty(),
                getItem(position).getNumberCourse());

        final View result;
        ViewHolder holder;

        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);

            holder = new ViewHolder();

            holder.speciality = convertView.findViewById(R.id.speciality_label);
            holder.course = convertView.findViewById(R.id.course_label);
            holder.name = convertView.findViewById(R.id.groupName_label);


            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

       /* Animation animation = AnimationUtils.loadAnimation(mContext,(position>lastPosition) ? R.anim.load_down_anim: R.anim.load_up_anim);
        result.startAnimation(animation);*/

        lastPosition = position;

        holder.name.setText(group.getName());
        holder.speciality.setText(group.getSpecialty().getName());
        holder.course.setText(String.valueOf(group.getNumberCourse()));

        return convertView;
    }


    public GroupListAdapter(@NonNull Context context, int resource, @NonNull List<Group> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
        groupList = objects;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public void remove(Group object) {
        groupList.remove(object);
        DBManager.delete(Group.class, ConstantEntity.ID, object.getId());
        notifyDataSetChanged();
    }
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
