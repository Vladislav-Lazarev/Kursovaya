package com.hpcc.kursovaya.ui.schedule.MonthViewPager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DayHeaderAdapter extends RecyclerView.Adapter<DayHeaderAdapter.ViewHolder> {

    private final Context context;
    private final LayoutInflater mInflater;

    public DayHeaderAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_day_headers,parent,false);
        return new DayHeaderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateTime firstDayOfWeek = DateTime.parse("01/01/1990 00:00:00", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")).plusDays(position);
        String lang = LocaleManager.getLocale(context.getResources()).getLanguage().split("-")[0];
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE", new Locale(lang));
        holder.dayHeader.setText(dayOfWeekFormat.format(firstDayOfWeek.toDate()));
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayHeader;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayHeader = itemView.findViewById(R.id.dayHeader);
        }
    }
}
