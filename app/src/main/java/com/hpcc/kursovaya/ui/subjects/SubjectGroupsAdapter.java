package com.hpcc.kursovaya.ui.subjects;

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
import com.hpcc.kursovaya.dao.NewEntity;

import java.util.List;

import io.realm.Sort;

public class SubjectGroupsAdapter extends ArrayAdapter<NewEntity> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private List<NewEntity> groupList;
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

    public SubjectGroupsAdapter(@NonNull Context context, int resource, @NonNull List<NewEntity> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        groupList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getGroup().getName();
        String speciality = getItem(position).getGroup().getSpecialty().getName();
        String totalHours = Integer.toString(getItem(position).getHoursPlan());
        String readHours = Integer.toString(getItem(position).getHoursDeducted());
        String canceledHours = Integer.toString(getItem(position).getHoursCanceled());

        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent,false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.groupNameText);
            holder.speciality = convertView.findViewById(R.id.specialityText);
            holder.totalHours = convertView.findViewById(R.id.totalHoursText);
            holder.readHours = convertView.findViewById(R.id.readHoursText);
            holder.canceledHours = convertView.findViewById(R.id.canceledHoursText);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(name);
        holder.speciality.setText(speciality);
        holder.totalHours.setText(totalHours);
        holder.readHours.setText(readHours);
        holder.canceledHours.setText(canceledHours);
        return convertView;
    }

    public void write(NewEntity object) {
        /*
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
        notifyDataSetChanged();*/
    }

    public void update(String nameSort) {
        update(nameSort, Sort.ASCENDING);
    }
    public void update(String nameSort, Sort sort) {
        //исправь под себя - это копирка из GroupListAdapter
        // Могу сортировтаь по Названию и по кол-во Курсо(не в приоритете)
        groupList.clear();
        /*groupList.addAll(DBManager.copyObjectFromRealm(
                DBManager.readAll(NewEntity.class, nameSort, sort)));*/
        notifyDataSetChanged();
    }

    public void delete(NewEntity object) {
      //  DBManager.delete(Group.class, ConstantApplication.ID, object.getId());
        notifyDataSetChanged();
    }

}
