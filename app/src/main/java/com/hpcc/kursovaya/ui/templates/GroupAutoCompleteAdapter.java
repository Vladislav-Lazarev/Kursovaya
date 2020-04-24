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
    private final static String TAG = GroupAutoCompleteAdapter.class.getSimpleName();

    private final Context context;
    private List<Group> items;
    private List<Group> itemsAll;
    private int viewResourceId;

    public GroupAutoCompleteAdapter(Context context, int viewResourceId, List<Group> items){
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<Group>) ((ArrayList<Group>) items).clone();
        Log.d(TAG,"items size"+items.size());
        this.context = context;
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
            TextView groupNameLabel = (TextView) v.findViewById(R.id.groupNameLabel);
            if (groupNameLabel != null) {
                Log.d(TAG,"performFilteringAtTheStart"+group.getName());
                groupNameLabel.setText(group.getName());
            }
        }
        return v;
    }


    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((Group)(resultValue)).getName();
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint!=null) {
                ArrayList<Group> suggestions = new ArrayList<>();
                for (Group group : itemsAll) {
                    if (group.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(group);
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                Log.d(TAG,"publishResults ok"+results.count);
                addAll((ArrayList<Group>) results.values);
            } else {
                Log.d(TAG,"publishResults not ok");
                addAll(items);
            }
            notifyDataSetChanged();
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
