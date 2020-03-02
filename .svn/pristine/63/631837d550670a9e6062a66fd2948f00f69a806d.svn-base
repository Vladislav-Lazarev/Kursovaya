package com.hpcc.kursovaya.ui.templates;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.ClassesButton.ClassesButtonWrapper;
import com.hpcc.kursovaya.R;

import java.util.ArrayList;

public class TemplateActivity extends AppCompatActivity {
    protected ClassesButtonWrapper[][] classes = new ClassesButtonWrapper[7][10];
    private final String TAG = "TemplateActivity";
    protected ArrayList<ClassesButtonWrapper> selectedButtons = new ArrayList<>();
    private boolean isSelectMode = false;
    private Toolbar toolbar;
    private Toolbar toolbar1;
    protected View classView;



    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template);
        toolbar = findViewById(R.id.toolbar);
        toolbar1 = findViewById(R.id.toolbarEdit);
        final Button cancelSelect = toolbar1.findViewById(R.id.turnOff_editing);
        cancelSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectMode = false;
                toolbar1.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                if(selectedButtons.size()!=0) {
                    for (ClassesButtonWrapper b: selectedButtons) {
                        b.getBtn().setBackgroundTintList(getResources().getColorStateList(android.R.color.transparent));
                    }
                    selectedButtons.clear();
                }
            }
        });
        Button deleteClasses = toolbar1.findViewById(R.id.delete_classes);
        deleteClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareDeleteDialog();
            }
        });
        toolbar1.setVisibility(View.GONE);
        Thread t = new Thread(){
            public void run(){
                for(int i = 0; i<7; i++){
                    for(int j=0; j<10; j++){
                        StringBuilder className = new StringBuilder("class");
                        className.append(j).append(i);
                        final int classDay = i;
                        final int classHour = j;
                        int classRes = getResources().getIdentifier(className.toString(),"id",getPackageName());
                        classes[i][j] = new ClassesButtonWrapper((Button)findViewById(classRes),getApplicationContext());
                        classes[i][j].getBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isSelectMode) {
                                    addClass(classDay, classHour);
                                } else if(!classes[classDay][classHour].getBtn().getText().equals("") && !classes[classDay][classHour].isSelected()){
                                    classes[classDay][classHour].getBtn().setBackgroundTintList(getResources().getColorStateList(R.color.sideBarTransp));
                                    classes[classDay][classHour].setSelected(true);
                                    selectedButtons.add(classes[classDay][classHour]);
                                } else{
                                    classes[classDay][classHour].getBtn().setBackgroundTintList(getResources().getColorStateList(R.color.menuTextColor));
                                    selectedButtons.remove(classes[classDay][classHour]);
                                    if(selectedButtons.size()==0){
                                        cancelSelect.performClick();
                                    }
                                }
                            }
                        });
                        classes[i][j].getBtn().setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if(!isSelectMode && !classes[classDay][classHour].getBtn().getText().equals("")) {
                                    classes[classDay][classHour].getBtn().setBackgroundTintList(getResources().getColorStateList(R.color.sideBarTransp));
                                    selectedButtons.add(classes[classDay][classHour]);
                                    classes[classDay][classHour].setSelected(true);
                                    isSelectMode = true;
                                    toolbar.setVisibility(View.GONE);
                                    toolbar1.setVisibility(View.VISIBLE);
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                }
            }

        };
        t.start();
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_path_150));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        setHeader();
        Button addButton = (Button) findViewById(R.id.create_template);
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
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDeleteClassesAcceptClick();
            }
        });
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
        for (ClassesButtonWrapper b : selectedButtons) {
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
