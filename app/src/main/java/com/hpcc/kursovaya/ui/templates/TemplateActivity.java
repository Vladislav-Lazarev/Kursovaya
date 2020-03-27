package com.hpcc.kursovaya.ui.templates;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.ClassesButton.TemplateClassesButtonWrapper;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import java.util.ArrayList;
import java.util.List;

public class TemplateActivity extends AppCompatActivity {
    private final static String TAG = TemplateActivity.class.getSimpleName();

    protected List<List<TemplateClassesButtonWrapper>> classes = new ArrayList<>();// new ClassesButtonWrapper[ConstantEntity.MAX_COUNT_WEEK][ConstantEntity.MAX_COUNT_ACADEMIC_HOUR * ConstantEntity.MAX_COUNT_LESSON];
    protected List<TemplateClassesButtonWrapper> selectedButtons = new ArrayList<>();
    private boolean isSelectMode = false;
    private Toolbar toolbar;
    private Toolbar toolbar1;
    protected View classView;
    protected List<Group> groupList;
    protected Group selectedGroup;
    private long mLastClickTime = 0;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template);
        toolbar = findViewById(R.id.toolbar);
        toolbar1 = findViewById(R.id.toolbarEdit);
        //creating elements for listview
        groupList = DBManager.copyObjectFromRealm(DBManager.readAll(Group.class, ConstantEntity.NUMBER_COURSE));
        final ImageButton cancelSelect = toolbar1.findViewById(R.id.turnOff_editing);
        cancelSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();                isSelectMode = false;
                toolbar1.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                if(selectedButtons.size()!=0) {
                    for (TemplateClassesButtonWrapper b: selectedButtons) {
                        b.getBtn().setBackgroundTintList(getResources().getColorStateList(android.R.color.transparent));
                    }
                    selectedButtons.clear();
                }
            }
        });
        ImageButton deleteClasses = toolbar1.findViewById(R.id.delete_classes);
        deleteClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                prepareDeleteDialog();
            }
        });
        toolbar1.setVisibility(View.GONE);
        Thread t = new Thread(){
            public void run(){
                for (int i = 0; i < ConstantEntity.MAX_COUNT_WEEK; i++){
                    final List<TemplateClassesButtonWrapper> buttonWrapperList = new ArrayList<>();
                    for (int j = 0; j < ConstantEntity.MAX_COUNT_ACADEMIC_HOUR * ConstantEntity.MAX_COUNT_LESSON; j++){
                        StringBuilder className = new StringBuilder("class");
                        className.append(j).append(i);
                        final int classDay = i;
                        final int classHour = j;
                        int classRes = getResources().getIdentifier(className.toString(), ConstantEntity.ID, getPackageName());
                        buttonWrapperList.add(new TemplateClassesButtonWrapper((Button)findViewById(classRes),getApplicationContext()));

                        buttonWrapperList.get(j).getBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();                                if(!isSelectMode) {
                                    if (buttonWrapperList.get(classDay).getBtn().getText()!=""){
                                        editClass(classDay,classHour);
                                    } else {
                                        addClass(classDay, classHour);
                                    }
                                } else if(!buttonWrapperList.get(classHour).getBtn().getText().equals("") && !buttonWrapperList.get(classHour).isSelected()){
                                    buttonWrapperList.get(classHour).setSelectBackground();
                                    buttonWrapperList.get(classHour).setSelected(true);
                                    selectedButtons.add(buttonWrapperList.get(classHour));
                                } else{
                                    buttonWrapperList.get(classHour).setUnselectBackground();
                                    selectedButtons.remove(buttonWrapperList.get(classHour));
                                    if(selectedButtons.size()==0){
                                        cancelSelect.performClick();
                                    }
                                }
                            }
                        });

                        buttonWrapperList.get(j).getBtn().setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if(!isSelectMode && !buttonWrapperList.get(classHour).getBtn().getText().equals("")) {
                                    buttonWrapperList.get(classHour).setSelectBackground();
                                    selectedButtons.add(buttonWrapperList.get(classHour));
                                    buttonWrapperList.get(classHour).setSelected(true);
                                    isSelectMode = true;
                                    toolbar.setVisibility(View.GONE);
                                    toolbar1.setVisibility(View.VISIBLE);
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                    classes.add(buttonWrapperList);
                }
            }
        };
        t.start();
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_path_150));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            prepareCloseAlertDialog();});

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        setHeader();
        ImageButton addButton = findViewById(R.id.create_template);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();                confirmButton();
            }
        });
        try {
            t.join();
        }
        catch (InterruptedException ex){
            Log.e(TAG,ex.toString());
        }
    }

    private void prepareCloseAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.template_activity_close_alert_title);
        builder.setMessage(R.string.template_activity_close_alert_content);
        builder.setPositiveButton(R.string.delete_positive, (dialog, which) -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();            onCloseActivityAcceptClick(); });
        builder.setNegativeButton(R.string.delete_negative, (dialog, which) -> {
            dialog.cancel();});
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener((arg0)-> {dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));});
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    protected void onCloseActivityAcceptClick() {
        finish();
    }

    @Override
    public void onBackPressed() {
        if(!isSelectMode){
            super.onBackPressed();
        } else {
            isSelectMode = false;
            toolbar1.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        }
    }
    private void prepareDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.popup_delete_classes_template);
        builder.setMessage(R.string.popup_delete_classes_content);
        builder.setPositiveButton(R.string.delete_positive, (dialog, which) ->{
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
        mLastClickTime = SystemClock.elapsedRealtime();
        onDeleteClassesAcceptClick();});
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void onDeleteClassesAcceptClick() {
        for (TemplateClassesButtonWrapper b : selectedButtons) {
            b.getBtn().setBackgroundTintList(getResources().getColorStateList(R.color.menuTextColor));
            b.setSelected(false);
            b.getBtn().setText("");
            b.getBtn().setBackground(getResources().getDrawable(R.drawable.hover_add));
        }
        selectedButtons.clear();
    }

    protected void setHeader(){

    }

    protected AlertDialog.Builder getClassDialogBuilder(final int classDay,final int classHour){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.popup_add_class);
        builder.setPositiveButton(R.string.popup_accept,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();                onClickAcceptClass(dialog,which,classDay,classHour);
            }
        });
        builder.setNegativeButton(R.string.popup_cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickCancelClass(dialog,which,classDay,classHour);
            }
        });
        classView = getLayoutInflater().inflate(R.layout.dialog_add_new_class_template,null);
        builder.setView(classView);
        return builder;
    }



    private void addClass(final int classDay,final int classHour){

        final AlertDialog dialog = getClassDialogBuilder(classDay,classHour).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void editClass(final int classDay,final int classHour){

        final AlertDialog dialog = getClassDialogBuilder(classDay,classHour).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    protected void onClickCancelClass(DialogInterface dialog, int which,int classDay,int classHour) {
       dialog.cancel();
    }

    protected void onClickAcceptClass(DialogInterface dialog, int which,int classDay, int classHour) {
        dialog.dismiss();
    }

    protected AlertDialog.Builder getConfirmDialogBuilder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        return builder;
    }

    private void confirmButton() {
        final AlertDialog dialog = getConfirmDialogBuilder().create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    protected void onClickAcceptTemplate(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    protected void onClickCancelTemplate(DialogInterface dialog, int which) {
        dialog.cancel();
    }


}
