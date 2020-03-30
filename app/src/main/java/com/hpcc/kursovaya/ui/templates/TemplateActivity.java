package com.hpcc.kursovaya.ui.templates;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.ClassesButton.TemplateClassesButtonWrapper;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateAcademicHour;

import java.util.ArrayList;
import java.util.List;

public class TemplateActivity extends AppCompatActivity {
    private final static String TAG = TemplateActivity.class.getSimpleName();

    private long mLastClickTime = 0;
    protected List<List<TemplateClassesButtonWrapper>> classes = new ArrayList<>();// new ClassesButtonWrapper[ConstantEntity.MAX_COUNT_WEEK][ConstantEntity.MAX_COUNT_ACADEMIC_HOUR * ConstantEntity.MAX_COUNT_LESSON];
    protected List<TemplateClassesButtonWrapper> selectedButtons = new ArrayList<>();
    private boolean isSelectMode = false;
    private Toolbar toolbar;
    private Toolbar toolbar1;
    protected View classView;
    protected List<Group> groupList;

    protected TemplateAcademicHour currentTemplate;
    protected List<TemplateAcademicHour> templateAcademicHourList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template);
        toolbar = findViewById(R.id.toolbar);
        toolbar1 = findViewById(R.id.toolbarEdit);

        groupList = DBManager.copyObjectFromRealm(DBManager.readAll(Group.class));
        final ImageButton cancelSelect = toolbar1.findViewById(R.id.turnOff_editing);
        cancelSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                toolbar1.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                isSelectMode = false;
                if(selectedButtons.size()!=0) {
                    for (TemplateClassesButtonWrapper b: selectedButtons) {
                        b.setUnselectBackground();
                    }
                    selectedButtons.clear();
                }
            }
        });

        ImageButton deleteClasses = toolbar1.findViewById(R.id.delete_classes);
        deleteClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                if(!isSelectMode) {
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
        toolbar.setNavigationOnClickListener(v -> prepareCloseAlertDialog());

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        setHeader();
        ImageButton addButton = findViewById(R.id.create_template);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmButton();
            }
        });
        try {
            t.join();
        }
        catch (InterruptedException ex){
            Log.e(TAG,ex.toString());
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Vlad Code
    protected void listenerSpinnerSubject(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                Subject subject = DBManager.read(Subject.class, ConstantEntity.NAME, item);
                currentTemplate.setSubject(subject);

                if (templateAcademicHourList.size() == ConstantEntity.TWO){
                    templateAcademicHourList.set(ConstantEntity.ONE,
                            templateAcademicHourList.get(ConstantEntity.ONE).setSubject(subject));
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    protected void fillSpinnerByGroup(Context context, Spinner spinner, Group group){
        List<Subject> subjectList = group.toSubjectList(group.getNumberCourse(), group.getSpecialty());
        List<String> stringList = new Subject().entityToNameList(subjectList);
        ConstantEntity.fillingSpinner(context, spinner, stringList);
    }
    protected int secondCellShift(int currentCellPosition){
        return (currentCellPosition % ConstantEntity.TWO == ConstantEntity.ZERO) ? 1 : -1;
    }
    static public List<TemplateAcademicHour> convert2DimensionalTo1Dimensional(List<List<TemplateClassesButtonWrapper>> _2DList){
        List<TemplateAcademicHour> result = new ArrayList<>();

        for (List<TemplateClassesButtonWrapper> list : _2DList){
            for (TemplateClassesButtonWrapper buttonWrapper : list){
                if (buttonWrapper.getTemplateAcademicHour() != null &&
                        buttonWrapper.getTemplateAcademicHour().isEntity()){
                    result.add(buttonWrapper.getTemplateAcademicHour());
                }
            }
        }

        return result;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void prepareCloseAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.template_activity_close_alert_title);
        builder.setMessage(R.string.template_activity_close_alert_content);

        DialogInterface.OnClickListener clickListener = this::onCloseActivityAcceptClick;
        builder.setPositiveButton(R.string.delete_positive, clickListener);
        /*builder.setPositiveButton(R.string.delete_positive, (dialog, which) -> {
            onCloseActivityAcceptClick();

            *//*Runnable runnable = this::onCloseActivityAcceptClick;
            Thread thread = new Thread(runnable, "delete_positive");
            thread.start();*//*
        });*/

        builder.setNegativeButton(R.string.delete_negative, (dialog, which) -> dialog.cancel());
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

    protected void onCloseActivityAcceptClick(DialogInterface dialogInterface, int i) {
        List<TemplateAcademicHour> templateAcademicHourList = convert2DimensionalTo1Dimensional(classes);
        for (TemplateAcademicHour templateAcademicHour : templateAcademicHourList){
            DBManager.delete(TemplateAcademicHour.class, ConstantEntity.ID, templateAcademicHour.getId());
        }

        finish();
    }

    /*protected void onCloseActivityAcceptClick() {
        List<TemplateAcademicHour> templateAcademicHourList = convert2DimensionalTo1Dimensional(classes);
        for (TemplateAcademicHour templateAcademicHour : templateAcademicHourList){
            DBManager.delete(TemplateAcademicHour.class, ConstantEntity.ID, templateAcademicHour.getId());
        }

        finish();
    }*/

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
        builder.setPositiveButton(R.string.delete_positive, (dialog, which) -> onDeleteClassesAcceptClick());
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
        Log.d(TAG, "onDeleteClassesAcceptClick"+selectedButtons.size());
        for (TemplateClassesButtonWrapper b : selectedButtons) {
            b.clearButtonContent();
        }
        selectedButtons.clear();
        toolbar1.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        isSelectMode = false;
    }

    protected void setHeader(){

    }

    protected AlertDialog.Builder getClassDialogBuilder(final int classDay,final int classHour){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.popup_add_class);
        builder.setPositiveButton(R.string.popup_accept,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickAcceptClass(dialog,which,classDay,classHour);
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
