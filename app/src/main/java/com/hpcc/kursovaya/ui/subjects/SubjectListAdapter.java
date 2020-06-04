package com.hpcc.kursovaya.ui.subjects;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.query.DBManager;

import java.util.List;
import java.util.Map;

import io.realm.Sort;

// Проверить
public class SubjectListAdapter extends ArrayAdapter<Subject> {
    private static final String TAG = SubjectListAdapter.class.getSimpleName();

    private int mResource;
    private Context mContext;
    private int lastPosition =-1;
    private List<Subject> subjectList;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Subject subject = new Subject(
                getItem(position).getName(),
                getItem(position).initMap().getSpecialityCountHourMap(),
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

            holder.subjectGroupsButton = convertView.findViewById(R.id.subjectGroups);
            holder.subjectGroupsButton.setOnClickListener(v -> {
                Intent intent = new Intent(mContext,SubjectGroupsListActivity.class);
                intent.putExtra("entry",getItem(position));
                mContext.startActivity(intent);
            });
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
        if (!subject.isEmptySpecialtyCountHour()){
            StringBuilder str = new StringBuilder();
            for (Map.Entry<Speciality, Integer> set : subject.getSpecialityCountHourMap().entrySet()) {
                str.append(set.getKey().getName() + " - " + set.getValue() + " " + mContext.getString(R.string.hour_subject));
            }
            str.deleteCharAt(str.length() - ConstantApplication.ONE);
            holder.speciality.setText(str.toString());
        }
        holder.course.setText(String.valueOf(subject.getNumberCourse()));
        return convertView;
    }
    //

    static class ViewHolder{
        TextView speciality;
        TextView course;
        TextView name;
        ImageButton subjectGroupsButton;
    }


    public SubjectListAdapter(@NonNull Context context, int resource, @NonNull List<Subject> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
        subjectList = objects;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void write(Subject object) {
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
        subjectList.clear();
        subjectList.addAll(DBManager.copyObjectFromRealm(
                DBManager.readAll(Subject.class, nameSort, sort)));
        notifyDataSetChanged();
    }

    public void delete(Subject object) {
        object.deleteAllLinks();
        //DBManager.delete(Subject.class, ConstantApplication.ID, object.getId());
        notifyDataSetChanged();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}