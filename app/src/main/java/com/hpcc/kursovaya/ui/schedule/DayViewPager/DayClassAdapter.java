package com.hpcc.kursovaya.ui.schedule.DayViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import androidx.appcompat.view.ActionMode;

import android.graphics.Paint;
import android.os.SystemClock;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.ClassesButton.ClassesButtonWrapper;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.AnotherEvent;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAnotherEvent;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.schedule.MonthViewPager.MonthViewAdapter;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DayClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected Context context;
    protected LayoutInflater mInflater;
    protected List<DayViewFragment.EventAgregator> academicHourList;
    protected ItemClickListener mClickListener;
    protected SparseBooleanArray selectedItems;


    public void delete(Integer position) {
       DayViewFragment.EventAgregator eventAgregator = academicHourList.get(position);
        if(eventAgregator!=null){
            if(eventAgregator.academicHour!=null){
                AcademicHour academicHour = eventAgregator.academicHour;
                int templateId =  academicHour.getTemplateAcademicHour().getId();
                int academicId = academicHour.getId();
                DBManager.delete(AcademicHour.class, ConstantApplication.ID,academicId);
                academicHour.refreshAllNumberPair(null);
                DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID,templateId);
            } else if(eventAgregator.anotherEvent!=null){
                DBManager.delete(TemplateAnotherEvent.class,ConstantApplication.ID,eventAgregator.anotherEvent.getTemplateAnotherEvent().getId());
                DBManager.delete(AnotherEvent.class,ConstantApplication.ID,eventAgregator.anotherEvent.getId());
            }
            academicHourList.set(position,null);
            notifyDataSetChanged();
        }

    }


    public void updateList(Date from, Date to){
        List<DayViewFragment.EventAgregator> academicHourList =Arrays.asList(new DayViewFragment.EventAgregator[10]);
        List<AcademicHour> actualAcademicHours = DBManager.copyObjectFromRealm(AcademicHour.academicHourListFromPeriod(from,to));
        List<AnotherEvent> actualAnotherEvent = DBManager.copyObjectFromRealm(AnotherEvent.anotherEventListFromPeriod(from,to));
        for(AcademicHour academicHour:actualAcademicHours){
            TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
            DayViewFragment.EventAgregator event = new DayViewFragment.EventAgregator();
            event.academicHour = academicHour;
            academicHourList.set(templateAcademicHour.getNumberHalfPairButton(),event);
        }
        for(AnotherEvent anotherEvent:actualAnotherEvent){
            TemplateAnotherEvent templateAnotherEvent = anotherEvent.getTemplateAnotherEvent();
            DayViewFragment.EventAgregator event = new DayViewFragment.EventAgregator();
            event.anotherEvent = anotherEvent;
            academicHourList.set(templateAnotherEvent.getNumberHalfPairButton(),event);
        }
        this.academicHourList = academicHourList;
        notifyDataSetChanged();
    }


    public DayClassAdapter(Context context, List<DayViewFragment.EventAgregator> actualAcademicHours){
        this.context = context;
        mInflater = LayoutInflater.from(context);
        academicHourList = actualAcademicHours;
        selectedItems = new SparseBooleanArray();
        /*for(AcademicHour academicHour:actualAcademicHours){
            TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
            academicHourList.set(templateAcademicHour.getNumberHalfPairButton(),academicHour);
        }*/
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_day,parent,false);
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder baseHolder, int position) {
        AcademicHour academicHour = null;
        AnotherEvent anotherEvent = null;
        ViewHolder holder = (ViewHolder) baseHolder;
        if(academicHourList.get(position)!=null){
            academicHour = academicHourList.get(position).academicHour;
            anotherEvent = academicHourList.get(position).anotherEvent;
        }
        int couple = position / 2;
        int half = ((position + 1) % 2 == 0) ? 1 : 0;
        if(academicHour!=null && academicHour.getTemplateAcademicHour()!=null){
            int numberHalfPair = academicHour.getTemplateAcademicHour().getNumberHalfPairButton();
            couple = numberHalfPair/2;
            half= ((numberHalfPair+1)%2 ==0) ? 1: 0;
        }
        String timeHeader = getCoupleHeader(couple, half);
        holder.timeHeader.setText(timeHeader);
        holder.numberPair.setVisibility(View.GONE);
        holder.groupName.setText(context.getResources().getString(R.string.day_fragment_no_group));
        holder.subjectName.setText(context.getResources().getString(R.string.day_fragment_class_free));
        holder.description.setText(context.getResources().getString(R.string.day_fragment_class_no_description));
        holder.groupName.setTextColor(Color.WHITE);
        holder.groupName.setPaintFlags(0);
        holder.description.setTextColor(Color.WHITE);
        holder.description.setPaintFlags(0);
        holder.subjectName.setTextColor(Color.WHITE);
        holder.subjectName.setPaintFlags(0);
        holder.rl.setBackgroundColor(context.getResources().getColor(R.color.colorStatusBar));
        if(academicHour!=null && academicHour.getTemplateAcademicHour()!=null){
            Group group = academicHour.getTemplateAcademicHour().getGroup();
            Subject subject = academicHour.getTemplateAcademicHour().getSubject();
            String description = academicHour.getNote();
            if(!description.equals("")){
                holder.description.setText(description);
            }
            holder.numberPair.setText(academicHour.getNumberPair()+"");
            holder.numberPair.setVisibility(View.VISIBLE);
            holder.subjectName.setText(subject.getName());
            if(academicHour.hasCompleted()){
                holder.subjectName.setTextColor(Color.GREEN);
                holder.groupName.setTextColor(Color.GREEN);
                holder.description.setTextColor(Color.GREEN);
            }
            if(academicHour.hasCanceled()){
               holder.subjectName.setTextColor(Color.GRAY);
               holder.subjectName.setPaintFlags(holder.subjectName.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG );

                holder.groupName.setTextColor(Color.GRAY);
                holder.groupName.setPaintFlags(holder.groupName.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG );

                holder.description.setTextColor(Color.GRAY);
                holder.description.setPaintFlags(holder.description.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG );
            }
            holder.groupName.setText(group.getName());
            if(!selectedItems.get(position,false)) {
                holder.rl.setBackgroundColor(subject.getColor());
            } else {
                holder.rl.setBackgroundColor(context.getResources().getColor(R.color.sideBarTransp));
            }
        }
        if(anotherEvent!=null){
            TemplateAnotherEvent templateAnotherEvent = anotherEvent.getTemplateAnotherEvent();

            String description = anotherEvent.getNote();
            if(!description.equals("")){
                holder.description.setText(description);
            }
            //holder.subjectName.setText(subject.getName());
            holder.subjectName.setVisibility(View.GONE);
            holder.groupName.setText(templateAnotherEvent.getTitle());
            if(!selectedItems.get(position,false)) {
                holder.rl.setBackgroundColor(templateAnotherEvent.getColor());
            } else {
                holder.rl.setBackgroundColor(context.getResources().getColor(R.color.sideBarTransp));
            }
        }

    }

    private String getCoupleHeader(int couple,int half){
        StringBuilder hourTimeLabel = new StringBuilder();
        StringBuilder minuteTimeLabel = new StringBuilder();
        if(ConstantApplication.timeArray[couple][half][0]<10){
            hourTimeLabel.append("0");
        }
        hourTimeLabel.append(ConstantApplication.timeArray[couple][half][0]);
        if(ConstantApplication.timeArray[couple][half][1]<10){
            minuteTimeLabel.append("0");
        }
        minuteTimeLabel.append(ConstantApplication.timeArray[couple][half][1]);
        StringBuilder result = new StringBuilder(hourTimeLabel);
        result.append(":").append(minuteTimeLabel);
        return result.toString();
    }


    @Override
    public int getItemCount() {
        return academicHourList.size();
    }

    public void toggleSelection(int position, RecyclerView.ViewHolder holder) {
        if(selectedItems.get(position,false)){
            selectedItems.delete(position);
            //((ViewHolder)holder).rl.setBackgroundColor(context.getResources().getColor(R.color.colorStatusBar));
        } else {
            selectedItems.put(position,true);
            //((ViewHolder)holder).rl.setBackgroundColor(context.getResources().getColor(R.color.sideBarTransp));
        }
        notifyItemChanged(position);
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List getSelectedItems() {
        List items =
                new ArrayList(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void setCompleted(int position,boolean b) {
        AcademicHour selected = academicHourList.get(position).academicHour;
        if(b && selected!=null){
            if(selected.hasCanceled()){
                Toast.makeText(context, R.string.class_cant_be_unread_day ,Toast.LENGTH_LONG).show();
            } else {
                academicHourList.get(position).academicHour.setCompleted(b);
            }
        } else if(selected!=null){
            if(selected.hasCompleted()){
                academicHourList.get(position).academicHour.setCompleted(b);
            } else {
                Toast.makeText(context, R.string.class_cant_be_unread_cancelled_day ,Toast.LENGTH_LONG).show();
            }
        }
        DBManager.write(selected);
        selected.refreshAllNumberPair(null);

    }

    public void setCanceled(int position,boolean b) {
        AcademicHour selected = academicHourList.get(position).academicHour;
        if(b && selected!=null){
            if(selected.hasCompleted()){
                Toast.makeText(context, R.string.readed_class_cant_be_cancelled_day ,Toast.LENGTH_LONG).show();
            } else {
                selected.setCanceled(b);
            }
        } else if(selected!=null) {
            if(selected.hasCanceled()){
                selected.setCanceled(b);
            } else {
                Toast.makeText(context, R.string.class_cant_be_cancelled_day ,Toast.LENGTH_LONG).show();
            }
        }
        DBManager.write(selected);
        selected.refreshAllNumberPair(null);


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected RelativeLayout rl;

        protected TextView timeHeader;

        protected TextView numberPair;
        protected TextView groupName;
        protected TextView subjectName;
        protected TextView description;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rl = itemView.findViewById(R.id.button);
            rl.setBackgroundColor(context.getResources().getColor(R.color.colorStatusBar));
            numberPair = itemView.findViewById(R.id.numberPair);
            numberPair.setTextColor(Color.WHITE);
            numberPair.setShadowLayer(5,4,4,Color.BLACK);
            numberPair.setVisibility(View.GONE);
            groupName = itemView.findViewById(R.id.groupName);
            groupName.setTextColor(Color.WHITE);
            groupName.setShadowLayer(5,4,4,Color.BLACK);
            subjectName = itemView.findViewById(R.id.subjectName);
            subjectName.setTextColor(Color.WHITE);
            subjectName.setShadowLayer(5,4,4,Color.BLACK);
            description = itemView.findViewById(R.id.description);
            description.setTextColor(Color.WHITE);
            description.setShadowLayer(5,4,4,Color.BLACK);

            timeHeader = itemView.findViewById(R.id.timeHeader);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if(mClickListener!=null){
                mClickListener.onLongItemClick(v, getAdapterPosition());
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
            return true;
        }
    }

    public DayViewFragment.EventAgregator getItem(int id) {
        return academicHourList.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onLongItemClick(View view, int position);
    }
}
