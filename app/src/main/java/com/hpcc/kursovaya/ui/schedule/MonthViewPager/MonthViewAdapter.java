package com.hpcc.kursovaya.ui.schedule.MonthViewPager;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
        if(dateAtPos.getMonthOfYear()==currentMonth){
            holder.myTextView.setTextColor(context.getResources().getColor(R.color.calendarBlack));
        }
        if(dateAtPos.equals(now)){
            holder.myTextView.setTextColor(Color.WHITE);
            holder.myTextView.setBackground(context.getResources().getDrawable(R.drawable.current_day_circle));
        }
        holder.myTextView.setText(Integer.toString(startDate.plusDays(mData[position]).getDayOfMonth()));
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return 42;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.dayText);
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
