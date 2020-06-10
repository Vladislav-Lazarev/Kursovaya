package com.hpcc.kursovaya.ui.hourChecker.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.ui.hourChecker.Interface.ILoadMore;
import com.hpcc.kursovaya.ui.hourChecker.model.GroupModel;

import java.util.List;


public class GroupAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_ITEM=0, VIEW_TYPE_LOADING=1;
    private ItemClickListener mClickListener;

    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    List<GroupModel> groups;
    int visibleThreshold = 1;
    int lastVisibleItem, totalItemCount;

    public GroupAdapter(RecyclerView recyclerView, Activity activity, List<GroupModel> groups){
        this.activity = activity;
        this.groups = groups;

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if(!isLoading && totalItemCount<=(lastVisibleItem+visibleThreshold)){
                    if(loadMore!=null){
                        loadMore.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });*/
    }

    @Override
    public int getItemViewType(int position) {
        return groups.get(position)==null ? VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore){
        this.loadMore = loadMore;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.recyclerview_group_item,parent,false);
            return new GroupViewHolder(view);
        } else if(viewType==VIEW_TYPE_LOADING){
            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.recyclerview_load_item,parent,false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof GroupViewHolder){
            GroupModel groupModel = groups.get(position);

            GroupViewHolder viewHolder = (GroupViewHolder) holder;
            viewHolder.groupName.setText(groupModel.getGroup().getName());
            if(!groupModel.isFull()){
                viewHolder.checkResult.setText(R.string.group_check_problems_conformity);
                viewHolder.checkResult.setTextColor(Color.RED);
            }
        } else if(holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded(){
        isLoading = false;
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView){
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView groupName;
        public TextView checkResult;

        public GroupViewHolder(View itemView){
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            checkResult = itemView.findViewById(R.id.checkResult);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onLongItemClick(View view, int position);
    }

    public void loadItems(){
        loadMore.onLoadMore();
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
}
