package com.hpcc.kursovaya.ui.templates;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.hpcc.kursovaya.R;

public class AddTemplateActivity extends TemplateActivity {
    private final String TAG = AddTemplateActivity.class.getSimpleName();

    public AddTemplateActivity(){
        super();
    }

    @Override
    protected void setHeader(int popup_super_template) {
        super.setHeader(R.string.popup_add_template);
    }

    @Override
    protected AlertDialog.Builder getConfirmDialogBuilder(int popup_super_template){
        return super.getConfirmDialogBuilder(R.string.popup_add_template);
    }
    @Override
    protected void onClickAcceptTemplate(DialogInterface dialog, int which) {
        // Intent Add

        dialog.cancel();
    }
}
