package com.hpcc.kursovaya.ui.templates;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateScheduleWeek;
import com.hpcc.kursovaya.dao.query.DBManager;

public class AddTemplateActivity extends TemplateActivity {
    private final String TAG = AddTemplateActivity.class.getSimpleName();

    public AddTemplateActivity(){
        super();
        scheduleWeek = new TemplateScheduleWeek();
    }

    @Override
    protected void setHeader(int popup_super_template) {
        super.setHeader(R.string.popup_add_template);
    }

    @Override
    protected AlertDialog.Builder prepareCloseAlertDialog() {
        AlertDialog.Builder builder = super.prepareCloseAlertDialog();
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (TemplateAcademicHour templateAcademicHour : convert2DimensionalTo1Dimensional(classes)){
                    DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID, templateAcademicHour.getId());
                }
                finish();
            }
        });
        buttonPainting(builder);
        return builder;
    }

    @Override
    protected AlertDialog.Builder getConfirmDialogBuilder(int popup_super_template){
        AlertDialog.Builder builder = super.getConfirmDialogBuilder(R.string.popup_add_template);
        scheduleWeek.setTemplateAcademicHourList(convert2DimensionalTo1Dimensional(classes));
        return builder;
    }
    @Override
    protected void onClickAcceptTemplate(DialogInterface dialog, int which) {
        super.onClickAcceptTemplate(dialog, which);

        intent = getIntent();
        intent.putExtra(String.valueOf(ConstantApplication.ACTIVITY_ADD), scheduleWeek);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }
}
