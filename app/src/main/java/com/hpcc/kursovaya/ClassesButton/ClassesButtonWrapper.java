package com.hpcc.kursovaya.ClassesButton;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.query.DBManager;

public class ClassesButtonWrapper {
    private Button btn;
    private Context context;
    private Drawable drawableDef;


    private AcademicHour academicHour;

    private int selectColorList = R.color.sideBarTransp;
    private boolean isSelected = false;
    private boolean isBackgroundChanged = false;

    public boolean isGroupNameShown() {
        return isGroupNameShown;
    }

    public void setGroupNameShown(boolean groupNameShown) {
        isGroupNameShown = groupNameShown;
    }

    private boolean isGroupNameShown = true;

    public ClassesButtonWrapper(Button btn, Context context){
        this.btn = btn;
        this.context= context;
        drawableDef = btn.getBackground();
    }

    public Button getBtn() {
        return btn;
    }

    public void setBtn(Button btn)
    {
        this.btn = btn;
    }

    public void setSelectBackground(){
        isBackgroundChanged = true;
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
            btn.setBackgroundColor(academicHour.getTemplateAcademicHour().getSubject().getColor());
        } else {
            btn.setBackground(drawableDef);
        }
        isSelected = false;
    }

    public void clearButtonContent(){
        drawableDef = context.getResources().getDrawable(R.drawable.hover_add);
        if(academicHour!=null && academicHour.getTemplateAcademicHour()!=null) {
            DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID, academicHour.getTemplateAcademicHour().getId());
            DBManager.delete(AcademicHour.class, ConstantApplication.ID, academicHour.getId());
        }
        btn.setBackground(drawableDef);
        academicHour = null;
        isBackgroundChanged = false;
        btn.setText("");
        isSelected=false;
    }
    public void clearButtonContentWithoutDeleting(){
        btn.setBackground(drawableDef);
        academicHour = null;
        isBackgroundChanged = false;
        btn.setText("");
        isSelected=false;
    }
    public AcademicHour getAcademicHour() {
        return academicHour;
    }

    public void setAcademicHour(AcademicHour academicHour) {
        this.academicHour = academicHour;
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[] { 0, 0, 0, 0, 0, 0, 0, 0 });
        shape.setColor(academicHour.getTemplateAcademicHour().getSubject().getColor());
        shape.setStroke(1, Color.BLACK);
        btn.setBackground(shape);
       // btn.setBackgroundColor(academicHour.getTemplateAcademicHour().getSubject().getColor());
        btn.setText(academicHour.getTemplateAcademicHour().getGroup().getName());
        btn.setTextColor(Color.WHITE);
        btn.setShadowLayer(5,4,4,Color.BLACK);
        setCompleted(academicHour.hasCompleted());
        if(!academicHour.hasCompleted()) {
            setCanceled(academicHour.hasCanceled());
        }
        isBackgroundChanged = true;
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setCompleted(boolean isCompleted) {
        if(academicHour != null){
            if(isCompleted){
                btn.setTextColor(Color.GREEN);
            } else {
                btn.setTextColor(Color.WHITE);
            }
            academicHour.setCompleted(isCompleted);
            DBManager.write(academicHour);
        }
    }
    public void setCanceled(boolean isCanceled){
        if(academicHour!=null){
            if(isCanceled){
                btn.setTextColor(Color.GRAY);
                btn.setPaintFlags(btn.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG );
            } else {
                btn.setPaintFlags(0);
                btn.setTextColor(Color.WHITE);
            }
            academicHour.setCanceled(isCanceled);
            DBManager.write(academicHour);
        }
    }
}
