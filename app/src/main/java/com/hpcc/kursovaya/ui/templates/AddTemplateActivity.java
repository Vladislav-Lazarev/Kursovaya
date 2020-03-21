package com.hpcc.kursovaya.ui.templates;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.hpcc.kursovaya.R;


public class AddTemplateActivity extends TemplateActivity {
    private final String TAG = "AddTemplateActivity";


    public AddTemplateActivity(){
        super();
    }

    @Override
    protected AlertDialog.Builder getClassDialogBuilder(final int classDay,final int classHour){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        /*        if(выражение на проверку пустоты в клеточке шаблона) {
           builder.setTitle(R.string.popup_edit_class);
        }
        else {

                     builder.setTitle(R.string.popup_add_class);
        }*/
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
      AutoCompleteTextView groupName = classView.findViewById(R.id.groupNameSuggestET);
      String displayedGroupName = groupName.getText().toString();
      Log.d(TAG, classDay + " " + classHour);
      Log.d(TAG, displayedGroupName);
      super.classes[classDay][classHour].getBtn().setText(displayedGroupName);
  }

}
