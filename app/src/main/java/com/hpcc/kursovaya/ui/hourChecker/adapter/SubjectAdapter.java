package com.hpcc.kursovaya.ui.hourChecker.adapter;


import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.ui.hourChecker.model.SubjectModel;

import java.util.List;



public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>{
    private Context context;
    private LayoutInflater mInflater;
    private List<SubjectModel> items;
    private ItemClickListener mClickListener;
    private SparseBooleanArray selectedItems;

    public SubjectAdapter(Context context, List<SubjectModel> items){
        this.context = context;
        this.items = items;
        mInflater = LayoutInflater.from(context);
        selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_subject_item,parent,false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        SubjectModel model = items.get(position);
        if(model!=null){
            holder.subjectName.setText(model.getSubject().getName());
            holder.subjectPlan.setText(Integer.toString(model.getPlanHours()));
            holder.subjectRead.setText(Integer.toString(model.getReadList().size()));
            holder.subjectCanceled.setText(Integer.toString(model.getCanceledHours().size()));
            holder.subjectUnread.setText(Integer.toString(model.getUnreadHours().size()));
            holder.subjectTotalInSchedule.setText(Integer.toString(model.getTotalInSchedule()));
            holder.restHours.setText(Integer.toString(model.getRestHours()));
            if(!model.isFull()){
                holder.checkResult.setText(R.string.subject_check_problems_conformity);
                holder.checkResult.setTextColor(Color.RED);
            } else{
                holder.checkResult.setText(R.string.group_check_no_problems_conformity);
                holder.checkResult.setTextColor(Color.GREEN);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onLongItemClick(View view, int position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView subjectName;
        TextView subjectPlan;
        TextView subjectRead;
        TextView subjectCanceled;
        TextView subjectUnread;
        TextView subjectTotalInSchedule;
        TextView restHours;
        TextView checkResult;

        public SubjectViewHolder(View itemView){
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectName);
            subjectPlan = itemView.findViewById(R.id.subjectPlan);
            subjectRead = itemView.findViewById(R.id.subjectRead);
            subjectCanceled = itemView.findViewById(R.id.subjectCancelled);
            subjectUnread = itemView.findViewById(R.id.subjectUnread);
            subjectTotalInSchedule = itemView.findViewById(R.id.subjectTotalInSchedule);
            restHours = itemView.findViewById(R.id.restHours);
            checkResult = itemView.findViewById(R.id.checkResult);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
