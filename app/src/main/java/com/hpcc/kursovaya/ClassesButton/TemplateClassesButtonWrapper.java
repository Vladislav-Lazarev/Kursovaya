package com.hpcc.kursovaya.ClassesButton;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateAcademicHour;

public class TemplateClassesButtonWrapper {
    private Button btn;
    private Context context;
    private Drawable drawableDef;


    private TemplateAcademicHour templateAcademicHour;

    private int selectColorList = R.color.sideBarTransp;
    private boolean isSelected = false;
    private boolean isBackgroundChanged = false;


    public boolean isBackgroundChanged() {
        return isBackgroundChanged;
    }

    public void setBackgroundChanged(boolean backgroundChanged) {
        isBackgroundChanged = backgroundChanged;
    }

    public TemplateClassesButtonWrapper(Button btn, Context context){
        this.btn = btn;
        this.context= context;
        drawableDef = btn.getBackground();
    }

    public Button getBtn() {
        return btn;
    }

    public void setBtn(Button btn) {
        this.btn = btn;
    }

    public void setSelectBackground(){
        btn.setBackgroundColor(context.getResources().getColor(selectColorList));
        isSelected = true;
    }

    public void setSelectBackground(Drawable draw){
        drawableDef = draw;
        btn.setBackground(draw);
        isSelected = false;
    }

    public void setUnselectBackground(){
        if(isBackgroundChanged) {
            btn.setBackgroundColor(templateAcademicHour.getSubject().getColor());
        } else {
            btn.setBackground(drawableDef);
        }
        isSelected = false;
    }

    public void clearButtonContent(){
        drawableDef = context.getResources().getDrawable(R.drawable.hover_add);
        btn.setBackground(drawableDef);
        isBackgroundChanged = false;
        //clear entity
        btn.setText("");
        isSelected=false;
    }


    public TemplateAcademicHour getTemplateAcademicHour() {
        return templateAcademicHour;
    }

    public void setTemplateAcademicHour(TemplateAcademicHour templateAcademicHour) {
        this.templateAcademicHour = templateAcademicHour;
        btn.setBackgroundColor(templateAcademicHour.getSubject().getColor());
        btn.setText(templateAcademicHour.getGroup().getName());
        isBackgroundChanged = true;
    }
    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
