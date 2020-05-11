package com.hpcc.kursovaya.ui.specialites;

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
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.query.DBManager;

import java.util.List;

import io.realm.Sort;

class SpecialityListAdapter extends ArrayAdapter<Speciality> {
    private static final String TAG = SpecialityListAdapter.class.getSimpleName();

    private Context mContext;
    private int mResource;
    private List<Speciality> specialityList;

    static class ViewHolder {
        TextView name;
        TextView courseQuantity;
        TextView code;
    }

    public SpecialityListAdapter(@NonNull Context context, int resource, @NonNull List<Speciality> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
        specialityList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String templateName = getItem(position).getName();
        String code = Integer.toString(getItem(position).getCode());

        final View result;
        ViewHolder holder;

        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.specialities_text);
            holder.code = convertView.findViewById(R.id.speciality_code_text);
            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
       /* Animation animation = AnimationUtils.loadAnimation(mContext,(position>lastPosition) ? R.anim.load_down_anim:R.anim.load_up_anim);
        result.startAnimation(animation);*/

        holder.name.setText(templateName);
        holder.code.setText(code);
        return convertView;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void write(Speciality object) {
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
        specialityList.clear();
        specialityList.addAll(DBManager.copyObjectFromRealm(
                DBManager.readAll(Speciality.class, nameSort, sort)));
        notifyDataSetChanged();
    }

    public void delete(Speciality object) {
        object.deleteAllLinks();
        notifyDataSetChanged();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}