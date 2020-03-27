package com.hpcc.kursovaya.ClassesButton;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.AcademicHour;

public class ClassesButtonWrapper {
    private Button btn;
    private Context context;
    private Drawable drawableDef;


    private AcademicHour academicHour;

    private int selectColorList = R.color.sideBarTransp;
    private boolean isSelected = false;

    public ClassesButtonWrapper(Button btn, Context context){
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
        btn.setBackground(drawableDef);
        isSelected = false;
    }

    public void clearButtonContent(){
        drawableDef = context.getResources().getDrawable(R.drawable.hover_add);
        btn.setBackground(drawableDef);
        //clear entity
        btn.setText("");
        isSelected=false;
    }
    public AcademicHour getAcademicHour() {
        return academicHour;
    }

    public void setAcademicHour(AcademicHour academicHour) {
        this.academicHour = academicHour;
        GradientDrawable background = (GradientDrawable) btn.getBackground();
        background.setColor(academicHour.getTemplateAcademicHour().getSubject().getColor());
        btn.setText(academicHour.getTemplateAcademicHour().getSubject().getName());
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
