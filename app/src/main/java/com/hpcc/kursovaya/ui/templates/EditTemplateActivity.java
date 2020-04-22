package com.hpcc.kursovaya.ui.templates;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.query.DBManager;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateAcademicHour;

import java.util.List;

public class EditTemplateActivity extends TemplateActivity {
    private final String TAG = EditTemplateActivity.class.getSimpleName();


    List<TemplateAcademicHour> originalList;

    public EditTemplateActivity(){
        super();
    }
    private void fillCellsEssentially(List<TemplateAcademicHour> _1DList){
        for (int i = 0, i1DList = 0; i < ConstantApplication.MAX_COUNT_WEEK; i++){
            for (int j = 0; j < ConstantApplication.MAX_COUNT_HALF_PAIR; j++){
                TemplateAcademicHour templateAcademicHour = _1DList.get(i1DList);
                if (i == templateAcademicHour.getNumberDayOfWeek() &&
                        j == templateAcademicHour.getNumberHalfPairButton()){
                    classes.get(i).get(j).setTemplateAcademicHour(templateAcademicHour);
                    if (++i1DList == _1DList.size()){
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        scheduleWeek = intent.getParcelableExtra(String.valueOf(ConstantApplication.ACTIVITY_EDIT));
        originalList = scheduleWeek.getTemplateAcademicHourList();
        fillCellsEssentially(scheduleWeek.getTemplateAcademicHourList());
    }

    @Override
    protected void setHeader(int popup_super_template){
        super.setHeader(R.string.popup_edit_template);
    }

    @Override
    protected AlertDialog.Builder prepareCloseAlertDialog() {
        AlertDialog.Builder builder = super.prepareCloseAlertDialog();
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<TemplateAcademicHour> templateAcademicHours = originalList;
                for (TemplateAcademicHour templateAcademicHour : convert2DimensionalTo1Dimensional(classes)){
                        DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID, templateAcademicHour.getId());
                }

                for(TemplateAcademicHour templateAcademicHour : templateAcademicHours){
                    DBManager.write(templateAcademicHour);
                }
                finish();
            }
        });
        buttonPainting(builder);
        return builder;
    }

    @Override
    protected AlertDialog.Builder getConfirmDialogBuilder(int popup_super_template){
        AlertDialog.Builder builder = super.getConfirmDialogBuilder(R.string.popup_edit_template);
        scheduleWeek.setTemplateAcademicHourList(convert2DimensionalTo1Dimensional(classes));
        templateNameEditText.setText(scheduleWeek.getName());
        return builder;
    }
    @Override
    protected void onClickAcceptTemplate(DialogInterface dialog, int which) {
        // Intent Edit
        scheduleWeek.setName(templateNameEditText.getText().toString());

        intent = getIntent();
        intent.putExtra(String.valueOf(ConstantApplication.ACTIVITY_EDIT), scheduleWeek);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }
}
