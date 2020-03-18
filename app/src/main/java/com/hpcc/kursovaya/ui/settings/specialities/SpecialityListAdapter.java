package com.hpcc.kursovaya.ui.settings.specialities;

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
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import java.util.List;

class SpecialityListAdapter extends ArrayAdapter<Speciality> {
    private static final String TAG = "SpecialityListAdapter";


    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private List<Speciality> specialityList;
    private SparseBooleanArray mSelectedItemsIds;

    static class ViewHolder {
        TextView name;
        TextView courseQuantity;
    }

    public SpecialityListAdapter(@NonNull Context context, int resource, @NonNull List<Speciality> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
        specialityList = objects;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String templateName = getItem(position).getName();
        String courseQuantity = Integer.toString(getItem(position).getCountCourse());

        final View result;
        ViewHolder holder;

        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.specialities_text);
            holder.courseQuantity = convertView.findViewById(R.id.courses_quantity_text);
            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
       /* Animation animation = AnimationUtils.loadAnimation(mContext,(position>lastPosition) ? R.anim.load_down_anim:R.anim.load_up_anim);
        result.startAnimation(animation);*/

        lastPosition = position;

        holder.name.setText(templateName);
        holder.courseQuantity.setText(courseQuantity);
        return convertView;
    }

    @Override
    public void remove(Speciality object) {
        specialityList.remove(object);
        DBManager.delete(Speciality.class, ConstantEntity.ID, object.getId());
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
