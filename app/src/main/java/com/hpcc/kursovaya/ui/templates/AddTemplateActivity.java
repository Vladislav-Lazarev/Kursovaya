package com.hpcc.kursovaya.ui.templates;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;


public class AddTemplateActivity extends TemplateActivity {
    private final String TAG = AddTemplateActivity.class.getSimpleName();

    public AddTemplateActivity(){
        super();
    }

    @Override
    protected AlertDialog.Builder getClassDialogBuilder(final int classDay,final int classHour){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        final Context context = this;
        classView = getLayoutInflater().inflate(R.layout.dialog_add_new_class_template,null);
        final Spinner subjectSpinner = classView.findViewById(R.id.spinnerSubject);
        AutoCompleteTextView suggestEditText = classView.findViewById(R.id.groupNameSuggestET);

        suggestEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGroup = (Group) adapterView.getItemAtPosition(i);
                //List<Subject> subjectList = selectedGroup.toSubjectList();
               // List<String> stringList = new Subject().entityToNameList(subjectList);
              //  ConstantEntity.fillingSpinner(context, subjectSpinner, stringList);

                //это обработчик нажатия на выдачу из AutoCompleteTextView
                //здесь ты можешь заполнить спиннер предметов
            }
        });

        GroupAutoCompleteAdapter adapter = new GroupAutoCompleteAdapter(this,R.layout.group_auto, groupList);
        suggestEditText.setAdapter(adapter);
        //заполнять спиннер нужно в зависимости от курса и специальности группы, но по дефолту можно тупо все предметы залить туда

        RadioButton oneHourRB = classView.findViewById(R.id.popup_duration_rgroup_short);
        RadioButton twoHourRB = classView.findViewById(R.id.popup_duration_rgroup_full);
        if(super.classes.get(classDay).get(classHour).getBtn().getText().equals("")) {
            builder.setTitle(R.string.popup_add_class);
            builder.setPositiveButton(R.string.popup_accept,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickAcceptClass(dialog,which,classDay,classHour);
                }
            });
        }
        else {
            builder.setTitle(R.string.popup_edit_class);
            //данные получай с объекта, а не с кнопки - это пример
            suggestEditText.setText(super.classes.get(classDay).get(classHour).getBtn().getText());
            //также заполни здесь спиннер предметов и выдели один из радиобатоннов
            builder.setPositiveButton(R.string.popup_accept,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickAcceptEditClass(dialog,which,classDay,classHour);
                }
            });
        }

        builder.setNegativeButton(R.string.popup_cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickCancelClass(dialog,which,classDay,classHour);
            }
        });
        builder.setView(classView);
        return builder;
    }




    @Override
    protected AlertDialog.Builder getConfirmDialogBuilder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.popup_add_template);
        builder.setPositiveButton(R.string.popup_accept, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickAcceptTemplate(dialog,which);
            }
        });
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickCancelTemplate(dialog,which);
            }
        });
        View view = getLayoutInflater().inflate(R.layout.dialog_add_new_template,null);
        builder.setView(view);
        return builder;
    }

    @Override
    protected void setHeader(){
        TextView textCont = (TextView)findViewById(R.id.toolbar_title);
        textCont.setText(getResources().getString(R.string.popup_add_template));
    }

  @Override
  protected void onClickAcceptTemplate(DialogInterface dialog, int which) {
      dialog.cancel();
  }
  @Override
  protected void onClickCancelTemplate(DialogInterface dialog, int which) {
      dialog.cancel();
  }

  @Override
  protected void onClickAcceptClass(DialogInterface dialog, int which,int classDay,int classHour) {
      //здесь обработчик кнопки принять
      AutoCompleteTextView groupName = classView.findViewById(R.id.groupNameSuggestET);
      String displayedGroupName = groupName.getText().toString();

      Log.d(TAG, classDay + " " + classHour);
      Log.d(TAG, displayedGroupName);
      super.classes.get(classDay).get(classHour).getBtn().setText(displayedGroupName);
  }
  private void onClickAcceptEditClass(DialogInterface dialog, int which, int classDay, int classHour) {
        //тоже самое но для редактирования
        AutoCompleteTextView groupName = classView.findViewById(R.id.groupNameSuggestET);
        String displayedGroupName = groupName.getText().toString();
        Log.d(TAG, classDay + " " + classHour);
        Log.d(TAG, displayedGroupName);
        super.classes.get(classDay).get(classHour).getBtn().setText(displayedGroupName);
    }
}
