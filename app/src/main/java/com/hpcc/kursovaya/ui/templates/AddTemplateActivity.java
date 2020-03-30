package com.hpcc.kursovaya.ui.templates;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.util.Pair;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateAcademicHour;

import java.util.ArrayList;
import java.util.Collections;


public class AddTemplateActivity extends TemplateActivity {
    private final String TAG = AddTemplateActivity.class.getSimpleName();

    public AddTemplateActivity(){
        super();
    }

    @Override
    protected AlertDialog.Builder getClassDialogBuilder(final int classDay,final int classHour){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        classView = getLayoutInflater().inflate(R.layout.dialog_add_new_class_template,null);
        final Context context = this;

        //create a list for writing a dialog box
        templateAcademicHourList = new ArrayList<>(Collections.singletonList(new TemplateAcademicHour()));
        currentTemplate = templateAcademicHourList.get(ConstantEntity.ZERO);
        final int pos = secondCellShift(classHour);

        Spinner subjectSpinner = classView.findViewById(R.id.spinnerSubject);
        listenerSpinnerSubject(subjectSpinner);

        AutoCompleteTextView groupNameSuggest = classView.findViewById(R.id.groupNameSuggestET);
        groupList = DBManager.copyObjectFromRealm(DBManager.readAll(Group.class));
        GroupAutoCompleteAdapter adapter = new GroupAutoCompleteAdapter(this,R.layout.group_auto, groupList);
        groupNameSuggest.setAdapter(adapter);

        // Нажатие на выпадающий список групп и заполнение spinner subject
        groupNameSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Group pressedGroup = (Group) adapterView.getItemAtPosition(i);
                currentTemplate.setGroup(pressedGroup);
                fillSpinnerByGroup(context, subjectSpinner, pressedGroup);

                if (templateAcademicHourList.size() == ConstantEntity.TWO){
                    templateAcademicHourList.set(ConstantEntity.ONE,
                            templateAcademicHourList.get(ConstantEntity.ONE).setGroup(pressedGroup));
                }
            }
        });

        // Выбор, определения сколько часов она будет ввести
        RadioGroup radioGroup = classView.findViewById(R.id.popup_duration_rgroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                currentTemplate.setDayAndPair(Pair.create(classDay, classHour));
                switch (checkedId){
                    case R.id.popup_duration_rgroup_short:
                        if (templateAcademicHourList.size() == ConstantEntity.TWO) {
                            templateAcademicHourList.remove(ConstantEntity.ONE);
                        }
                        break;
                    case R.id.popup_duration_rgroup_full:
                        if (templateAcademicHourList.size() == ConstantEntity.ONE){
                            TemplateAcademicHour following = new TemplateAcademicHour();
                            if (currentTemplate.getGroup() != null){
                                following.setGroup(currentTemplate.getGroup())
                                        .setSubject(currentTemplate.getSubject())
                                        .setDayAndPair(Pair.create(classDay, classHour + pos));
                            }
                            templateAcademicHourList.add(following);
                        } else if (templateAcademicHourList.size() == ConstantEntity.TWO) {
                            templateAcademicHourList.set(ConstantEntity.ONE,
                                    templateAcademicHourList.get(ConstantEntity.ONE)
                                            .setDayAndPair(Pair.create(classDay, classHour + pos)));
                        } else {
                            throw new RuntimeException("popup_duration_rgroup_full = Шо за дичь");
                        }
                        break;
                }
            }
        });

        if ("".equals(super.classes.get(classDay).get(classHour).getBtn().getText().toString())) {
            builder.setTitle(R.string.popup_add_class);

            builder.setPositiveButton(R.string.popup_accept,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickAcceptClass(dialog,which,classDay,classHour);
                }
            });
        } else {
            builder.setTitle(R.string.popup_edit_class);
            templateAcademicHourList.clear();

            // Fill UI
            currentTemplate = super.classes.get(classDay).get(classHour).getTemplateAcademicHour();
            templateAcademicHourList.add(currentTemplate);
            TemplateAcademicHour following = super.classes.get(classDay).get(classHour + pos).getTemplateAcademicHour();

            if (following != null &&
                    currentTemplate.getGroup().equals(following.getGroup()) &&
                    currentTemplate.getSubject().equals(following.getSubject())) {
                templateAcademicHourList.add(following);
            }

            final int maxCountButton = templateAcademicHourList.size();

            groupNameSuggest.setText(currentTemplate.getGroup().getName());
            fillSpinnerByGroup(context, subjectSpinner, currentTemplate.getGroup());
            ConstantEntity.setSpinnerText(subjectSpinner, currentTemplate.getSubject().getName());

            switch (maxCountButton){
                case ConstantEntity.ONE:
                    radioGroup.check(R.id.popup_duration_rgroup_short);
                    break;
                case ConstantEntity.TWO:
                    radioGroup.check(R.id.popup_duration_rgroup_full);
                    break;
            }

            builder.setPositiveButton(R.string.popup_accept,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickAcceptEditClass(dialog,which,classDay,classHour, maxCountButton);
                }
            });
        }

        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener(){
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
        for (TemplateAcademicHour templateAcademicHour : templateAcademicHourList){
            templateAcademicHour.createEntity();
            DBManager.write(templateAcademicHour);
            Log.d(TAG, "templateAcademicHour = " + templateAcademicHour.toString());
        }

        int pos = 0;
        for (TemplateAcademicHour templateAcademicHour : templateAcademicHourList){
            // Assigning data to a cell
            super.classes.get(classDay).get(classHour + pos).setTemplateAcademicHour(templateAcademicHour);

            if (templateAcademicHourList.size() == ConstantEntity.TWO){
                pos = secondCellShift(classHour);
            }
        }
        templateAcademicHourList.clear();
    }
    private void onClickAcceptEditClass(DialogInterface dialog, int which, int classDay, int classHour, final int maxCountButton) {
        /**
         *  maxCountButton - количество заполненных ячеек 1 или 2
         *  (maxCountButton == templateAcademicHourList.size()) - проверка если исодное кол-во ячеек равное текущему,
            то есть если пользователь в прошом ввел 1 ячеку(1 час), то проверяю какого текущее состояние 1 или 2 ячейки(1 или 2 час)
         *  switch - если кол-во ячеек разнятся, было 1 стало 2 или на оборот:
         *  Если было 2 ячеки и стало 1 -> 2 удаляю , 1 презаписываю
         *  Если было 1 ячейка и стало 2 -> перезаписываю 1 и записываю 2
        */

        // Когда текущее количество часов совпадае с предыдущим
        if (templateAcademicHourList.size() == maxCountButton){
            onClickAcceptClass(dialog, which, classDay, classHour);
        } else {
            int pos = secondCellShift(classHour);
            switch (templateAcademicHourList.size()) {
                // Было 2 часа стал 1
                case ConstantEntity.ONE:
                    // Del data and button
                    super.classes.get(classDay).get(classHour + pos).clearButtonContent();
                    break;
                // Было 1 час стало 2
                case ConstantEntity.TWO:
                    // Insert data and button
                    onClickAcceptClass(dialog, which, classDay, classHour);
                    break;
                default:
                    Log.d(TAG, "onClickAcceptEditClass = " + templateAcademicHourList.toString());
                    throw new RuntimeException("Хрен знает в чем ошибка)" + "onClickAcceptEditClass = " + templateAcademicHourList.toString());
            }
        }
        templateAcademicHourList.clear();
    }
}
