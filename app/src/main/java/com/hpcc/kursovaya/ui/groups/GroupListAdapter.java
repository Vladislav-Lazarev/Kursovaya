package com.hpcc.kursovaya.ui.groups;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import java.util.List;

import io.realm.Sort;

public class GroupListAdapter extends ArrayAdapter<Group> {
    private static final String TAG = GroupListAdapter.class.getSimpleName();

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private List<Group> groupList;

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
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Vlad Code
    public void write(Group object) {
        if (object.existsEntity()) {
            Log.d(TAG, "And\\Edit Entity = " + object);
            Toast.makeText(mContext, R.string.toast_exists_entity, Toast.LENGTH_SHORT).show();
        } else {
            try {
                DBManager.write(object.createEntity());
                Toast.makeText(mContext, R.string.toast_add_edit_entity, Toast.LENGTH_SHORT).show();
            } catch (Exception ex){
                Log.e(TAG, ex.getMessage());
            }
        }
        notifyDataSetChanged();
    }

    public void update(String nameSort) {
        update(nameSort, Sort.ASCENDING);
    }
    public void update(String nameSort, Sort sort) {
        // Могу сортировтаь по Названию и по кол-во Курсо(не в приоритете)
        groupList.clear();
        groupList.addAll(DBManager.copyObjectFromRealm(
                DBManager.readAll(Group.class, nameSort, sort)));
        notifyDataSetChanged();
    }

    public void delete(Group object) {
        DBManager.delete(Group.class, ConstantApplication.ID, object.getId());
        notifyDataSetChanged();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
