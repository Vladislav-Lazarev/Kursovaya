package com.hpcc.kursovaya.ui.settings.alarms;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;

public class AlarmListAdapter extends ArrayAdapter<int[]> {
    private int[][] timeAlarms = null;
    private Context mContext;
    private int mResource;

    public AlarmListAdapter(@NonNull Context context, int resource, @NonNull int[][] objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        timeAlarms = objects;
    }


    class ViewHolder{
        TextView hourLabel;
        TextView hourTimeLabel;
        TextView minuteTimeLabel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        ViewHolder holder;
        Log.d("AlarmListAdapter","call GetView "+timeAlarms[1][0]+" "+timeAlarms[1][1]);
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);

            holder = new ViewHolder();

            holder.hourLabel = convertView.findViewById(R.id.hourLabel);
            holder.hourTimeLabel = convertView.findViewById(R.id.hourTimeLabel);
            holder.minuteTimeLabel = convertView.findViewById(R.id.minuteTimeLabel);



            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String hourTimeLabel = new String();
        String minuteTimeLabel = new String();
        if(timeAlarms[position][0] < ConstantApplication.MAX_COUNT_HALF_PAIR){
               hourTimeLabel = "0";
           }
        hourTimeLabel +=String.valueOf(timeAlarms[position][0]);
        if(timeAlarms[position][1] < ConstantApplication.MAX_COUNT_HALF_PAIR){
            minuteTimeLabel = "0";
        }
        minuteTimeLabel += String.valueOf(timeAlarms[position][1]);
        holder.hourLabel.setText(String.valueOf(++position)+" "+mContext.getResources().getString(R.string.hourLabel));
        holder.hourTimeLabel.setText(hourTimeLabel);
        holder.minuteTimeLabel.setText(minuteTimeLabel);
        return convertView;
    }
}
