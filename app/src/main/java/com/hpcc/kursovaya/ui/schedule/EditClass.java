package com.hpcc.kursovaya.ui.schedule;

import android.os.Bundle;
import android.util.Log;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;

import org.joda.time.DateTime;

public class EditClass extends Class {
    private int savedHourSize = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        dayOfWeek = (DateTime)intent.getSerializableExtra("dayOfWeek");
        numberOfLesson = intent.getIntExtra("classHour",0);
        classHour = intent.getIntExtra("classHour",0);
        classDay = intent.getIntExtra("classDay",0);
        super.onCreate(savedInstanceState);
        academicHourList.clear();
        templateAcademicHourList.clear();
        currentAcademicHour = intent.getParcelableExtra("currentCell");
        currentTemplateAcademicHour = currentAcademicHour.getTemplateAcademicHour();
        academicHourList.add(currentAcademicHour);
        templateAcademicHourList.add(currentTemplateAcademicHour);
        AcademicHour secondCell = intent.getParcelableExtra("secondCell");
        if(secondCell!=null) {
            TemplateAcademicHour secondCellTemplate = secondCell.getTemplateAcademicHour();
            if(secondCellTemplate!=null &&
                    currentTemplateAcademicHour.getGroup().equals(secondCellTemplate.getGroup()) &&
                    currentTemplateAcademicHour.getSubject().equals(secondCellTemplate.getSubject()) &&
                    currentAcademicHour.getRepeatForNextWeek() == secondCell.getRepeatForNextWeek() && secondCell.getNote().equals(currentAcademicHour.getNote())){
                templateAcademicHourList.add(secondCellTemplate);
                academicHourList.add(secondCell);
            }
        }

        final int maxCountButton = templateAcademicHourList.size();

        groupNameSuggest.setText(currentTemplateAcademicHour.getGroup().getName());
        fillSpinnerByGroup(this, subjectSpinner, currentTemplateAcademicHour.getGroup());
        ConstantApplication.setSpinnerText(subjectSpinner, currentTemplateAcademicHour.getSubject().getName());
        switch (maxCountButton){
            case ConstantApplication.ONE:
                radioGroup.check(R.id.duration_rgroup_short);
                break;
            case ConstantApplication.TWO:
                radioGroup.check(R.id.duration_rgroup_full);
                break;
        }
        classSummary.setText(currentAcademicHour.getNote());
        repeatForNextWeekContent.setSelection(currentAcademicHour.getRepeatForNextWeek());
        notificationBeforeContent.setSelection(currentAcademicHour.getNotificationBefore());
        notificationBefore = false;
        numberOfHalf = ((numberOfLesson+1) % 2 == 0)? 1 : 0;
        numberOfLesson = numberOfLesson/2;
        savedHourSize = academicHourList.size();
        Log.d("sdf","sfdsf");
    }


    protected void actionBar(ActionBarI actionBarI){
        super.actionBar(this::addClass);
    }

    @Override
    protected void addClass(){
        if(savedHourSize!=academicHourList.size()
        && savedHourSize!=ConstantApplication.ONE){
            intent.putExtra("clearSecondCell",true);
        }
        super.addClass();
    }
    @Override
    protected void setHeader(int popup_super){
        super.setHeader(R.string.edit_class);
    }

}