package com.hpcc.kursovaya.ui.templates;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateScheduleWeek;
import com.hpcc.kursovaya.dao.query.DBManager;

import java.util.List;

import io.realm.Sort;

public class TemplateListAdapter extends ArrayAdapter<TemplateScheduleWeek> {
    private static final String TAG = TemplateListAdapter.class.getSimpleName();

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private List<TemplateScheduleWeek> scheduleWeekList;

    static class ViewHolder {
        TextView name;
    }

    public TemplateListAdapter (@NonNull Context context, int resource, @NonNull List<TemplateScheduleWeek> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
        scheduleWeekList = objects;
    }

    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        TemplateScheduleWeek scheduleWeek = new TemplateScheduleWeek(
                getItem(position).getName(),
                getItem(position).getTemplateAcademicHourList());

        final View result;
        ViewHolder holder;

        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.template_text);
            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,(position>lastPosition) ? R.anim.load_down_anim:R.anim.load_up_anim);
        result.startAnimation(animation);

        lastPosition = position;

        holder.name.setText(scheduleWeek.getName());

        return convertView;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Vlad Code
    public void write(TemplateScheduleWeek object) {
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
        scheduleWeekList.clear();
        scheduleWeekList.addAll(DBManager.copyObjectFromRealm(
                DBManager.readAll(TemplateScheduleWeek.class, nameSort, sort)));
        notifyDataSetChanged();
    }

    public void delete(TemplateScheduleWeek object) {
        for (TemplateAcademicHour templateAcademicHour : object.getTemplateAcademicHourList()){
            DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID, templateAcademicHour.getId());
        }
        DBManager.delete(TemplateScheduleWeek.class, ConstantApplication.ID, object.getId());
        notifyDataSetChanged();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}