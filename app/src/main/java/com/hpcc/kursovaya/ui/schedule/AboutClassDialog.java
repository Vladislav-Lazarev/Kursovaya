package com.hpcc.kursovaya.ui.schedule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;
import java.util.List;

public class AboutClassDialog extends DialogFragment {
    private static String TAG = AboutClassDialog.class.getSimpleName();

    private Context context;
    private AcademicHour academicHour;

    public static AboutClassDialog newInstance(Context context, AcademicHour academicHour) {
        Bundle args = new Bundle();
        AboutClassDialog fragment = new AboutClassDialog();
        fragment.context = context;
        fragment.academicHour = academicHour;
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DateTime from = DateTime.now();
        DateTime to = DateTime.now();
        if(from.getMonthOfYear() < DateTimeConstants.JULY){
            from = from.withYear(from.getYear()-1).withMonthOfYear(DateTimeConstants.SEPTEMBER).withDayOfMonth(1);
            to = to.withMonthOfYear(DateTimeConstants.JULY).withDayOfMonth(1);
        } else {
            from = from.withMonthOfYear(DateTimeConstants.SEPTEMBER).withDayOfMonth(1);
            to = to.withYear(to.getYear()+1).withMonthOfYear(DateTimeConstants.JULY).withDayOfMonth(1);
        }

        List<AcademicHour> academicHourList = new ArrayList<>();

        for(AcademicHour academicHour : AcademicHour.academicHourListFromPeriod(from.toDate(),to.plusDays(1).toDate())){
            try{
                TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                if(templateAcademicHour!=null){
                    Group group = templateAcademicHour.getGroup();
                    Subject subject = templateAcademicHour.getSubject();
                    if(group!=null && subject!=null && this.academicHour.getTemplateAcademicHour().getGroup().equals(group) && this.academicHour.getTemplateAcademicHour().getSubject().equals(subject)){
                        academicHourList.add(academicHour);
                    }
                }
            } catch (Exception ex){
                Log.e(TAG, "reportGen:"+ex.toString());
            }

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.about_class_dialog_title);
        View view = View.inflate(context,R.layout.dialog_about_class,null);
        TextView groupName = view.findViewById(R.id.contentGroupName);
        TextView subjectName = view.findViewById(R.id.contentSubject);
        TextView descriptionText = view.findViewById(R.id.contentDescription);
        TextView totalHours = view.findViewById(R.id.contentPlanHours);
        TextView totalParaQuantity = view.findViewById(R.id.contentParaQuantity);
        TextView totalReadHours = view.findViewById(R.id.contentReadHours);
        TextView totalReadParaQuantity = view.findViewById(R.id.contentReadParaQuantity);
        TextView totalLeftHours = view.findViewById(R.id.contentLeftHours);
        TextView totalLeftParaQuantity = view.findViewById(R.id.contentLeftParaQuantity);
        groupName.setText(academicHour.getTemplateAcademicHour().getGroup().getName());
        subjectName.setText(academicHour.getTemplateAcademicHour().getSubject().getName());
        descriptionText.setText(academicHour.getNote());
        Subject subject = academicHour.getTemplateAcademicHour().getSubject();
        Speciality speciality = academicHour.getTemplateAcademicHour().getGroup().getSpecialty();
        int totHours = subject.initMap().getSpecialityCountHour(speciality);
        totalHours.setText(Integer.toString(totHours));
        totalParaQuantity.setText(Integer.toString(totHours/2+1));
        builder.setView(view);
        return builder.create();
    }
}
