package com.hpcc.kursovaya.ui.schedule;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;

import org.joda.time.DateTime;

public class EditClass extends Class {
    private int savedHourSize = 0;

    private Subject currentSubject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        dayOfWeek = (DateTime)intent.getSerializableExtra("dayOfWeek");
        numberOfLesson = intent.getIntExtra("classHour",0);
        classDay = intent.getIntExtra("classDay",0);
        classHour = intent.getIntExtra("classHour",0);

        super.onCreate(savedInstanceState);

        templateAcademicHourList.clear();
        academicHourList = intent.getParcelableArrayListExtra("academicHourList");
        savedHourSize = academicHourList.size();
        currentAcademicHour = academicHourList.get(ConstantApplication.ZERO);
        currentTemplateAcademicHour = currentAcademicHour.getTemplateAcademicHour();

        switch (academicHourList.size()){
            case ConstantApplication.ONE:
                templateAcademicHourList.add(currentTemplateAcademicHour);
                ((RadioButton)findViewById(R.id.duration_rgroup_short)).setChecked(true);
                break;
            case ConstantApplication.TWO:
                AcademicHour secondCell = academicHourList.get(ConstantApplication.ONE);
                TemplateAcademicHour secondCellTemplate = secondCell.getTemplateAcademicHour();

                if(currentTemplateAcademicHour.getGroup().equals(secondCellTemplate.getGroup()) &&
                        currentTemplateAcademicHour.getSubject().equals(secondCellTemplate.getSubject()) &&
                        currentAcademicHour.getRepeatForNextWeek() == secondCell.getRepeatForNextWeek() &&
                        secondCell.getNote().equals(currentAcademicHour.getNote())){
                    templateAcademicHourList.add(secondCellTemplate);
                }

                ((RadioButton)findViewById(R.id.duration_rgroup_full)).setChecked(true);
                break;
            default:
                throw new RuntimeException("EditClass - onCreate - academicHourList.size() = " + academicHourList.size());
        }

        groupNameSuggest.setText(currentTemplateAcademicHour.getGroup().getName());
        ConstantApplication.setSpinnerText(subjectSpinner, currentTemplateAcademicHour.getSubject().getName());

        classSummary.setText(currentAcademicHour.getNote());
        repeatForNextWeekContent.setSelection(currentAcademicHour.getRepeatForNextWeek());
        notificationBeforeContent.setSelection(currentAcademicHour.getNotificationBefore());
        notificationBefore = false;
        numberOfHalf = ((numberOfLesson+1) % 2 == 0)? 1 : 0;
        numberOfLesson = numberOfLesson/2;
        Log.d("sdf","sfdsf");
    }


    protected void actionBar(ActionBarI actionBarI){
        super.actionBar(this::saveClass);
    }

    @Override
    protected void saveClass(){
        if(savedHourSize!=academicHourList.size() && savedHourSize!=ConstantApplication.ONE){
            intent.putExtra("clearSecondCell",true);
        }
        super.saveClass();
    }
    @Override
    protected void setHeader(int popup_super){
        super.setHeader(R.string.edit_class);
    }
}