package com.hpcc.kursovaya.ui.templates;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import com.hpcc.kursovaya.ClassesButton.TemplateClassesButtonWrapper;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateScheduleWeek;
import com.hpcc.kursovaya.dao.query.DBManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TemplateActivity extends AppCompatActivity {
    private final static String TAG = TemplateActivity.class.getSimpleName();
    protected final Context currentContext = this;

    protected List<List<TemplateClassesButtonWrapper>> classes = new ArrayList<>();
    protected List<TemplateClassesButtonWrapper> selectedButtons = new ArrayList<>();
    private boolean isSelectMode = false;
    private Toolbar toolbar;
    private Toolbar toolbar1;
    protected View classView;
    private long mLastClickTime = 0;

    // UI - cell
    protected List<Group> groupList;
    protected AutoCompleteTextView groupNameSuggest;
    protected Spinner subjectSpinner;
    protected RadioGroup radioGroup;
    // UI - name template
    protected EditText templateNameEditText;

    protected TemplateAcademicHour currentTemplate;
    protected List<TemplateAcademicHour> templateAcademicHourList;

    protected TemplateScheduleWeek scheduleWeek;
    protected Intent intent;

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
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
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
                for (int i = 0; i < ConstantApplication.MAX_COUNT_WEEK; i++){
                    final List<TemplateClassesButtonWrapper> buttonWrapperList = new ArrayList<>();
                    for (int j = 0; j < ConstantApplication.MAX_COUNT_HALF_PAIR; j++){
                        StringBuilder className = new StringBuilder("class");
                        className.append(j).append(i);
                        final int classDay = i;
                        final int classHour = j;
                        int classRes = getResources().getIdentifier(className.toString(), ConstantApplication.ID, getPackageName());
                        buttonWrapperList.add(new TemplateClassesButtonWrapper(findViewById(classRes), getApplicationContext()));

                        buttonWrapperList.get(j).getBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isSelectMode) {
                                    if (buttonWrapperList.get(classDay).getBtn().getText()!=""){
                                        editClass(classDay,classHour, "");
                                    } else {
                                        addClass(classDay, classHour, "");
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
            if (convert2DimensionalTo1Dimensional(classes).isEmpty()){
                finish();
            } else {
                prepareCloseAlertDialog();
            }
            });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        setHeader(R.string.popup_super_template);
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
    private void listenerSpinnerSubject(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                Subject subject = DBManager.read(Subject.class, ConstantApplication.NAME, item);
                currentTemplate.setSubject(subject);

                if (templateAcademicHourList.size() == ConstantApplication.TWO){
                    templateAcademicHourList.set(ConstantApplication.ONE,
                            templateAcademicHourList.get(ConstantApplication.ONE).setSubject(subject));
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void fillSpinnerByGroup(Context context, Spinner spinner, Group group){
        List<Subject> subjectList = group.toSubjectList(group.getNumberCourse(), group.getSpecialty());
        List<String> stringList = new Subject().entityToNameList(subjectList);
        ConstantApplication.fillingSpinner(context, spinner, stringList);
    }
    protected List<TemplateAcademicHour> convert2DimensionalTo1Dimensional(List<List<TemplateClassesButtonWrapper>> _2DList){
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

    // Диалоговое окно для возращения к Списку Шаблонов
    protected AlertDialog.Builder prepareCloseAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
        builder.setTitle(R.string.template_activity_close_alert_title);
        builder.setMessage(R.string.template_activity_close_alert_content);

        builder.setNegativeButton(R.string.delete_negative, (dialog, which) -> dialog.cancel());
        return builder;
    }
    protected void buttonPainting(AlertDialog.Builder builder){
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener((arg0)-> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));});
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if(!isSelectMode){
            prepareCloseAlertDialog();
        } else {
            isSelectMode = false;
            toolbar1.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        }
    }
    private void prepareDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
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

    //~~~~~~ Работа с ячейками
    // Название Активити
    protected void setHeader(int popup_super_template){
        // Name Head
        TextView textCont = findViewById(R.id.toolbar_title);
        textCont.setText(getResources().getString(popup_super_template));
    }
    // Для конструирования getClassDialogBuilder
    protected void addClass(final int classDay,final int classHour, String strGroup){
        final AlertDialog dialog = getClassDialogBuilder(classDay,classHour, strGroup).create();
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
    protected void editClass(final int classDay,final int classHour, String strGroup){
        final AlertDialog dialog = getClassDialogBuilder(classDay,classHour, strGroup).create();
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
    // Работа я ячейками
    protected AlertDialog.Builder getClassDialogBuilder(final int classDay,final int classHour, String strGroup){
        AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
        builder.setTitle(R.string.popup_add_class);
        builder.setCancelable(false);
        classView = getLayoutInflater().inflate(R.layout.dialog_add_new_class_template,null);
        builder.setView(classView);

        //create a list for writing a dialog box
        templateAcademicHourList = new ArrayList<>(Collections
                .singletonList(new TemplateAcademicHour().setDayAndPair(Pair.create(classDay, classHour))));
        currentTemplate = templateAcademicHourList.get(ConstantApplication.ZERO);

        int posSecondCell = ConstantApplication.secondCellShift(classHour);

        subjectSpinner = classView.findViewById(R.id.spinnerSubject);
        listenerSpinnerSubject(subjectSpinner);

        groupNameSuggest = classView.findViewById(R.id.groupNameSuggestET);
        groupNameSuggest.setText(strGroup);
        groupList = DBManager.copyObjectFromRealm(DBManager.readAll(Group.class, ConstantApplication.NAME));
        GroupAutoCompleteAdapter adapter = new GroupAutoCompleteAdapter(currentContext,R.layout.group_auto, groupList);
        groupNameSuggest.setAdapter(adapter);
        groupNameSuggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strGroup = s.toString();
                Group group = DBManager.read(Group.class, ConstantApplication.NAME, strGroup);

                if (!ConstantApplication.checkUI(ConstantApplication.PATTERN_GROUP, strGroup) || group == null){
                    ConstantApplication.fillingSpinner(currentContext, subjectSpinner, new ArrayList<>());
                    return;
                }

                currentTemplate.setGroup(DBManager.copyObjectFromRealm(group));
                fillSpinnerByGroup(currentContext, subjectSpinner, group);

                if (templateAcademicHourList.size() == ConstantApplication.TWO){
                    templateAcademicHourList.set(ConstantApplication.ONE,
                            templateAcademicHourList.get(ConstantApplication.ONE).setGroup(group));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // Нажатие на выпадающий список групп и заполнение spinner subject
        groupNameSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                subjectSpinner = classView.findViewById(R.id.spinnerSubject);
                listenerSpinnerSubject(subjectSpinner);

                Group pressedGroup = (Group) adapterView.getItemAtPosition(i);
                currentTemplate.setGroup(pressedGroup);
                fillSpinnerByGroup(currentContext, subjectSpinner, pressedGroup);

                if (templateAcademicHourList.size() == ConstantApplication.TWO){
                    templateAcademicHourList.set(ConstantApplication.ONE,
                            templateAcademicHourList.get(ConstantApplication.ONE).setGroup(pressedGroup));
                }
            }
        });

        // Выбор, определения сколько часов она будет ввести
        ((RadioButton) classView.findViewById(R.id.popup_duration_rgroup_short)).setChecked(true);
        radioGroup = classView.findViewById(R.id.popup_duration_rgroup);
        radioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.popup_duration_rgroup_short:
                    if (templateAcademicHourList.size() == ConstantApplication.TWO) {
                        if (templateAcademicHourList.get(ConstantApplication.ONE).getId() != ConstantApplication.ZERO) {
                            DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID,
                                    templateAcademicHourList.get(ConstantApplication.ONE).getId());
                        }
                        templateAcademicHourList.remove(ConstantApplication.ONE);
                    }
                    break;
                case R.id.popup_duration_rgroup_full:
                    if (templateAcademicHourList.size() == ConstantApplication.TWO &&
                            templateAcademicHourList.get(ConstantApplication.ONE).getId() != ConstantApplication.ZERO) {
                        break;
                    }

                    TemplateAcademicHour following = new TemplateAcademicHour();
                    if (currentTemplate.getGroup() != null) {
                        following.setGroup(currentTemplate.getGroup())
                                .setSubject(currentTemplate.getSubject())
                                .setDayAndPair(Pair.create(classDay, classHour + posSecondCell));
                    }
                    templateAcademicHourList.add(following);
                    break;
            }
        });

        if (classes.get(classDay).get(classHour).getBtn().getText().toString().isEmpty()) {
            builder.setTitle(R.string.popup_add_class);
            builder.setPositiveButton(R.string.popup_accept,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickAcceptClass(dialog,which,classDay,classHour);
                }
            });
        } else {
            builder.setTitle(R.string.popup_edit_class);
            // Fill UI
            templateAcademicHourList.clear();

            currentTemplate = classes.get(classDay).get(classHour).getTemplateAcademicHour();
            templateAcademicHourList.add(currentTemplate);

            TemplateAcademicHour following = classes.get(classDay).get(classHour + posSecondCell).getTemplateAcademicHour();
            if (following != null &&
                    currentTemplate.getGroup().equals(following.getGroup()) &&
                    currentTemplate.getSubject().equals(following.getSubject())) {
                templateAcademicHourList.add(following);
            }

            groupNameSuggest.setText(currentTemplate.getGroup().getName());
            final int maxCountButton = templateAcademicHourList.size();
            switch (maxCountButton){
                case ConstantApplication.ONE:
                    ((RadioButton) classView.findViewById(R.id.popup_duration_rgroup_short)).setChecked(true);
                    break;
                case ConstantApplication.TWO:
                    ((RadioButton) classView.findViewById(R.id.popup_duration_rgroup_full)).setChecked(true);
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
        return builder;
    }

    // Нажатия\Действия кнопок getClassDialogBuilder
    private void onClickCancelClass(DialogInterface dialog, int which,int classDay,int classHour) {
        dialog.cancel();
    }
    protected void onClickAcceptClass(DialogInterface dialog, int which,int classDay, int classHour) {

        int pos = ConstantApplication.secondCellShift(classHour);
        TemplateAcademicHour adjacentCellToDelete = classes.get(classDay).get(classHour + pos).getTemplateAcademicHour();
        if (templateAcademicHourList.size() == ConstantApplication.TWO && adjacentCellToDelete != null){
            classes.get(classDay).get(classHour + pos).clearButtonContent();
        }
        
        for (TemplateAcademicHour templateAcademicHour : templateAcademicHourList){
            try {
                DBManager.write(templateAcademicHour.createEntity());
                Log.d(TAG, "templateAcademicHour = " + templateAcademicHour.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pos = 0;
        for (TemplateAcademicHour templateAcademicHour : templateAcademicHourList){
            // Assigning data to a cell
            classes.get(classDay).get(classHour + pos).setTemplateAcademicHour(templateAcademicHour);

            if (templateAcademicHourList.size() == ConstantApplication.TWO){
                pos = ConstantApplication.secondCellShift(classHour);
            }
        }
    }
    private void onClickAcceptEditClass(DialogInterface dialog, int which, int classDay, int classHour, final int maxCountButton) {
        /**
         *  maxCountButton - количество заполненных ячеек 1 или 2
         *  (maxCountButton == templateAcademicHourList.size()) - проверка если исодное кол-во ячеек равное текущему,
         то есть если пользователь в прошом ввел 1 ячеку(1 час), то проверяю какого текущее состояние 1 или 2 ячейки(1 или 2 час)
         * (templateAcademicHourList.size() == ConstantEntity.ONE) - проверка если было 2 часа, но сейчас 1, то убить соседнюю
         */

        if (templateAcademicHourList.size() != maxCountButton &&
                templateAcademicHourList.size() == ConstantApplication.ONE){
            int pos = ConstantApplication.secondCellShift(classHour);
            classes.get(classDay).get(classHour + pos).clearButtonContent();
        }

        onClickAcceptClass(dialog, which, classDay, classHour);
    }

    //~~~~~~ Формирование имени Шаблона
    // Для конструирования getConfirmDialogBuilder
    private void confirmButton() {
        List<TemplateAcademicHour> entityTemplateAcademicHourList = convert2DimensionalTo1Dimensional(classes);
        if (entityTemplateAcademicHourList.isEmpty()){
            Toast.makeText(currentContext, R.string.toast_empty_template, Toast.LENGTH_SHORT).show();
            return;
        }

        final AlertDialog dialog = getConfirmDialogBuilder(R.string.popup_super_template).create();
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
    // Диалоговое окно для создания шаблона
    protected AlertDialog.Builder getConfirmDialogBuilder(int popup_super_template){
        AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
        builder.setTitle(popup_super_template);
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
        templateNameEditText = view.findViewById(R.id.template_name_text);
        templateNameEditText.setText(scheduleWeek.getName());
        builder.setView(view);

        return builder;
    }
    // Нажатия\Действия кнопок getConfirmDialogBuilder
    protected void onClickCancelTemplate(DialogInterface dialog, int which) {
        dialog.cancel();
    }
    protected void onClickAcceptTemplate(DialogInterface dialog, int which){
        String strTemplate = templateNameEditText.getText().toString();
        if (!ConstantApplication.checkUI(ConstantApplication.PATTERN_TEMPLATE, strTemplate)){
            Toast.makeText(this, R.string.toast_check, Toast.LENGTH_LONG);
            return;
        }
        scheduleWeek.setName(strTemplate)
                .setTemplateAcademicHourList(convert2DimensionalTo1Dimensional(classes));
    }
}