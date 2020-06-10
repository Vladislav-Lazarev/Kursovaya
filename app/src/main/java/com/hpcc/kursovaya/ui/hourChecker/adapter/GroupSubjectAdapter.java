package com.hpcc.kursovaya.ui.hourChecker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.DayClassAdapter;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.DayViewFragment;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.text.SimpleDateFormat;
import java.util.List;

public class GroupSubjectAdapter extends DayClassAdapter {
    public static final String TAG =GroupSubjectAdapter.class.getSimpleName();


    public GroupSubjectAdapter(Context context, List<DayViewFragment.EventAgregator> actualAcademicHours) {
        super(context, actualAcademicHours);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_hour_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void delete(Integer position) {
        super.delete(position);
    }

    @Override
    public void setCompleted(int position, boolean b) {
        super.setCompleted(position, b);
    }

    @Override
    public void setCanceled(int position, boolean b) {
        super.setCanceled(position, b);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder baseHolder, int position) {
        super.onBindViewHolder(baseHolder, position);
        ViewHolder holder = (ViewHolder) baseHolder;
        AcademicHour academicHour = null;
        if(academicHourList.get(position)!=null){
            academicHour = academicHourList.get(position).academicHour;
        }
        if(academicHour!=null){
            TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", LocaleManager.getLocale(context.getResources()));
            holder.date.setText(sdf.format(academicHour.getDate()));
            holder.pairNumber.setText(Integer.toString(academicHour.getNumberPair()));
            Subject subject = templateAcademicHour.getSubject();
            subject = subject.initMap();
            Speciality speciality = templateAcademicHour.getGroup().getSpecialty();
            if(subject!=null && speciality!=null){
                int planHours = subject.getSpecialityCountHour(speciality);
                int actualHours = academicHour.getNumberPair()*2;
                if(actualHours>planHours){
                    holder.warning.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    public class ViewHolder extends DayClassAdapter.ViewHolder{

        protected TextView pairNumber;
        protected TextView warning;
        protected TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            date.setTextColor(Color.WHITE);
            date.setShadowLayer(5,4,4,Color.BLACK);
            warning = itemView.findViewById(R.id.warning);
            warning.setShadowLayer(5,4,4,Color.BLACK);
            pairNumber = itemView.findViewById(R.id.numberPair);
            numberPair.setTextColor(Color.WHITE);
            numberPair.setShadowLayer(5,4,4,Color.BLACK);
        }
    }
}
