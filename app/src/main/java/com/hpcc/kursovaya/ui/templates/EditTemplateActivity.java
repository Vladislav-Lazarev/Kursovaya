package com.hpcc.kursovaya.ui.templates;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.core.util.Pair;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateScheduleWeek;

import java.util.List;

public class EditTemplateActivity extends TemplateActivity {
    private final String TAG = EditTemplateActivity.class.getSimpleName();

    private TemplateScheduleWeek templateScheduleWeek;

    private void fillCellsEssentially(List<TemplateAcademicHour> _1DList){
        for (int i = 0, i1DList = 0; i < ConstantApplication.MAX_COUNT_WEEK; i++){
            for (int j = 0; j < ConstantApplication.MAX_COUNT_LESSON * ConstantApplication.TWO; j++){
                Pair<Integer, Integer> pair = _1DList.get(i1DList).getDayAndPair();
                if (i == pair.first && j == pair.second){
                    classes.get(i).get(j).setTemplateAcademicHour(_1DList.get(i1DList++));
                }
            }
        }
    }
    public EditTemplateActivity(){
        super();
        intent = getIntent();
        templateScheduleWeek = intent.getParcelableExtra("editTemplateScheduleWeek");
        fillCellsEssentially(templateScheduleWeek.getTemplateAcademicHourList());
        setResult(Activity.RESULT_OK, intent);
    }

    @Override
    protected void setHeader(int popup_super_template){
        super.setHeader(R.string.popup_edit_template);
    }

    @Override
    protected AlertDialog.Builder getConfirmDialogBuilder(int popup_super_template){
        AlertDialog.Builder builder = super.getConfirmDialogBuilder(R.string.popup_edit_template);
        String stringName = templateScheduleWeek.getName();
        nameTemplate.setText(stringName);
        return builder;
    }
    @Override
    protected void onClickAcceptTemplate(DialogInterface dialog, int which) {
        // Intent Edit


        dialog.cancel();
    }
}
