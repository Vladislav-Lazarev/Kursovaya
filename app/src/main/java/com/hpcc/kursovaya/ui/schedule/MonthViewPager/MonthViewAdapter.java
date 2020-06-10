package com.hpcc.kursovaya.ui.schedule.MonthViewPager;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.AnotherEvent;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAnotherEvent;
import com.hpcc.kursovaya.dao.query.DBManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class MonthViewAdapter extends RecyclerView.Adapter<MonthViewAdapter.ViewHolder>{
    private int[] mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
    private DateTime startDate = formatter.parseDateTime("01/01/1990 00:00:00");
    private int currentMonth;
    private Context context;

    public MonthViewAdapter(Context context, int[] data){
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        currentMonth = startDate.plusDays(data[0]).plusWeeks(1).getMonthOfYear();
        this.context = context;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.recyclerview_day_of_month,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateTime dateAtPos = startDate.plusDays(mData[position]);
        DateTime now = DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        List<AcademicHour> academicHours = DBManager.copyObjectFromRealm(AcademicHour.academicHourListFromPeriod(dateAtPos.toDate(),dateAtPos.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999).toDate()));
        List<AnotherEvent> anotherEvents = DBManager.copyObjectFromRealm(AnotherEvent.anotherEventListFromPeriod(dateAtPos.toDate(),dateAtPos.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999).toDate()));
        if(dateAtPos.getMonthOfYear()==currentMonth){
            holder.dayText.setTextColor(context.getResources().getColor(R.color.calendarBlack));
        }
        if(dateAtPos.equals(now)){
            holder.dayText.setTextColor(Color.WHITE);
            holder.dayText.setBackground(context.getResources().getDrawable(R.drawable.current_day_circle));
        }
        holder.dayText.setText(Integer.toString(startDate.plusDays(mData[position]).getDayOfMonth()));
        for(AcademicHour academicHour : academicHours){
            TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
            if(templateAcademicHour!=null) {
                holder.cards.get(templateAcademicHour.getNumberHalfPairButton()).setVisibility(View.VISIBLE);
                holder.classes.get(templateAcademicHour.getNumberHalfPairButton()).setText(templateAcademicHour.getGroup().getName());
                holder.classes.get(templateAcademicHour.getNumberHalfPairButton()).setBackgroundColor(templateAcademicHour.getSubject().getColor());
                if(academicHour.hasCanceled()){
                    holder.classes.get(templateAcademicHour.getNumberHalfPairButton()).setTextColor(Color.GRAY);
                    holder.classes.get(templateAcademicHour.getNumberHalfPairButton()).setPaintFlags(holder.classes.get(templateAcademicHour.getNumberHalfPairButton()).getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG );
                }
                if(academicHour.hasCompleted()){
                    holder.classes.get(templateAcademicHour.getNumberHalfPairButton()).setTextColor(Color.GREEN);
                }
            }

        }
        for (AnotherEvent anotherEvent: anotherEvents){
            TemplateAnotherEvent templateAnotherEvent = anotherEvent.getTemplateAnotherEvent();
            if(templateAnotherEvent!=null){
                holder.cards.get(templateAnotherEvent.getNumberHalfPairButton()).setVisibility(View.VISIBLE);
                holder.classes.get(templateAnotherEvent.getNumberHalfPairButton()).setText(templateAnotherEvent.getTitle());
                holder.classes.get(templateAnotherEvent.getNumberHalfPairButton()).setBackgroundColor(templateAnotherEvent.getColor());
            }
        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return 42;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dayText;
        List<TextView> classes;
        List<CardView> cards;

        ViewHolder(View itemView) {
            super(itemView);
            classes = new ArrayList<>();
            cards = new ArrayList<>();
            for(int i = 0; i<10;i++){
                StringBuilder cardName = new StringBuilder("card");
                StringBuilder className = new StringBuilder("class");
                className.append(i);
                cardName.append(i);
                int classRes = context.getResources().getIdentifier(className.toString(),"id",context.getPackageName());
                int cardRes = context.getResources().getIdentifier(cardName.toString(),"id",context.getPackageName());
                TextView clazz = itemView.findViewById(classRes);
                clazz.setTextColor(Color.WHITE);
                clazz.setShadowLayer(5,4,4,Color.BLACK);
                CardView card = itemView.findViewById(cardRes);
                card.setVisibility(View.INVISIBLE);
                cards.add(card);
                classes.add(clazz);
            }
            dayText = itemView.findViewById(R.id.dayText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    int getItem(int id) {
        return mData[id];
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
