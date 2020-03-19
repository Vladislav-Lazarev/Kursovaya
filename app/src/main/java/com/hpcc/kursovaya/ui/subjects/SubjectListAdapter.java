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
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import java.util.List;

public class SubjectListAdapter extends ArrayAdapter<Subject> {
    private static final String TAG = SubjectListAdapter.class.getSimpleName();

    private int mResource;
    private Context mContext;
    private int lastPosition =-1;
    private List<Subject> subjectList;
    private SparseBooleanArray mSelectedItemsIds;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Subject subject = new Subject(
                getItem(position).getName(),
                getItem(position).getSpecialityCountHourMap(),
                getItem(position).getNumberCourse(),
                getItem(position).getColor());


        final View result;
        ViewHolder holder;

        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);

            holder = new ViewHolder();

            holder.speciality = (TextView) convertView.findViewById(R.id.speciality_label);
            holder.course = (TextView) convertView.findViewById(R.id.course_label);
            holder.name = (TextView) convertView.findViewById(R.id.subjectName_label);


            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        lastPosition = position;

      /* Animation animation = AnimationUtils.loadAnimation(mContext,(position>lastPosition) ? R.anim.load_down_anim:R.anim.load_up_anim);
       result.startAnimation(animation);*/

        holder.name.setText(subject.getName());

        StringBuilder str = new StringBuilder();
        List<Speciality> specList = subject.getSpecialityList();
        List<Integer> hourList = subject.getCountHourList();
        int i = 0;
        final int oneValue = 1;
        while (i < specList.size() - oneValue
                && i < hourList.size() - oneValue) {
            str.append(specList.get(i).getName() + " - " + hourList.get(i) + "\n");
            i++;
        }
        str.append("\t\t\t" + specList.get(i).getName() + " - " + hourList.get(i));
        holder.speciality.setText(str.toString());

        holder.course.setText(String.valueOf(subject.getNumberCourse()));
        return convertView;
    }
    //

    static class ViewHolder{
        TextView speciality;
        TextView course;
        TextView name;
    }


    public SubjectListAdapter(@NonNull Context context, int resource, @NonNull List<Subject> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
        subjectList = objects;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public void remove(Subject object) {
        subjectList.remove(object);
        DBManager.delete(Subject.class, ConstantEntity.ID, object.getId());
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
    //
}