package com.hpcc.kursovaya.ui.templates;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupAutoCompleteAdapter extends ArrayAdapter<Group> implements Filterable {
    private final String TAG = "GroupACAdapter";
    private final Context context;
    private List<Group> items;
    private List<Group> itemsAll = new ArrayList<>();
    private List<Group> suggestions;
    private int viewResourceId;


    public GroupAutoCompleteAdapter(Context context, int viewResourceId, List<Group> items){
        super(context, viewResourceId, items);
        this.items = items;
        for(Group group : items) {
            itemsAll.add(group);
        }
        Log.d(TAG, Integer.toString(itemsAll.size()));
        /*this.itemsAll.clear();
        this.itemsAll.addAll(items);*/
        this.context = context;
        this.suggestions = new ArrayList<Group>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
       Group group = items.get(position);
        if (group != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.groupNameLabel);
            if (customerNameLabel != null) {
                Log.i(TAG, "getView Customer Name:"+group.getName());
                customerNameLabel.setText(group.getName());
            }
        }
        return v;
    }


    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Group)(resultValue)).getName();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (Group group : itemsAll) {
                    if(group.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(group);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Group> filteredList = (ArrayList<Group>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (Group c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

    @Nullable
    @Override
    public Group getItem(int position) {
        return items.get(position);
    }
    @Override
    public int getCount() {
        return items.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }
}
