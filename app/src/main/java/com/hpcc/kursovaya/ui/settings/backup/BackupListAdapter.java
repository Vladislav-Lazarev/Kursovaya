package com.hpcc.kursovaya.ui.settings.backup;

import android.app.AlertDialog;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.setting.BackupDB;

import java.util.Calendar;
import java.util.List;

public class BackupListAdapter extends ArrayAdapter<BackupDB> {
    private Context mContext;
    private int mResource;
    private List<BackupDB> backupDBList;
    private SparseBooleanArray mSelectedItemsIds;

    public BackupListAdapter(@NonNull Context context, int resource, @NonNull List<BackupDB> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        backupDBList = objects;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    @Override
    public void remove(BackupDB object) {
        //не забудь удалить из базы или хз где ты его хранить будешь
        backupDBList.remove(object);
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public void removeSelection() {
        mSelectedItemsIds.clear();/* = new SparseBooleanArray();*/
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    class ViewHolder{
        TextView name;
        TextView date;
        ImageButton importButton;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        StringBuilder sbDate = new StringBuilder();
        Calendar date = Calendar.getInstance();
        date.setTime((getItem(position).getDateCreate()));
        sbDate.append(date.get(Calendar.YEAR)).append(".").append(date.get(Calendar.MONTH)).append(".").append(date.get(Calendar.DAY_OF_MONTH));
        String dateStr = sbDate.toString();

        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent,false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.backup_text);
            holder.date = convertView.findViewById(R.id.date_creation_text);
            holder.importButton = convertView.findViewById(R.id.import_backup);
            holder.importButton.setOnClickListener(v -> {
                showImportBackupDialog();
            });
            convertView.setTag(holder);
        }  else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(name);
        holder.date.setText(dateStr);

        return convertView;
    }

    private void showImportBackupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.import_dialog_title);
        builder.setMessage(R.string.import_dialog_message);
        builder.setPositiveButton(R.string.delete_positive,(dialog, which) ->{
            onClickAcceptImport();
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel,((dialog, which) -> {
            dialog.cancel();
        }));
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener((arg0)->{
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mContext.getResources().getColor(R.color.sideBar));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mContext.getResources().getColor(R.color.sideBar));
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void onClickAcceptImport() {
        //обработчик кнопки принять
    }
}
