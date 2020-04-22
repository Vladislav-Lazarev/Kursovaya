package com.hpcc.kursovaya.ui.settings.backup;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.setting.BackupDB;
import com.hpcc.kursovaya.dao.entity.setting.FileManager;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BackupListAdapter extends ArrayAdapter<BackupDB> {
    private Context mContext;
    private int mResource;
    private List<BackupDB> backupDBList;
    private List<ViewHolder> viewHolderList;
    private SparseBooleanArray mSelectedItemsIds;

    private class ViewHolder{
        private TextView fileName;
        private TextView dateCreate;
        private ImageButton importButton;

        public ViewHolder(View view){
            this.fileName = view.findViewById(R.id.backup_text);
            this.dateCreate = view.findViewById(R.id.date_creation_text);
            this.importButton = view.findViewById(R.id.import_backup);
            this.importButton.setOnClickListener(v -> {
                showImportBackupDialog(v);
            });
        }
    }

    public BackupListAdapter(@NonNull Context context, int resource, @NonNull List<BackupDB> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        backupDBList = objects;
        viewHolderList = new ArrayList<>();
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public void removeSelection() {
        mSelectedItemsIds.clear();
        notifyDataSetChanged();
    }

    public List<BackupDB> getSelectBackupDBList(){
        List<BackupDB> selectBackupDBList = new ArrayList<>();

        for (int i = 0; i < mSelectedItemsIds.size(); i++) {
            int key = mSelectedItemsIds.keyAt(i);
            if (mSelectedItemsIds.get(key)) {
                BackupDB selectBackupDB = backupDBList.get(key);
                selectBackupDBList.add(selectBackupDB);
            }
        }

        return selectBackupDBList;
    }

    public List<BackupDB> getBackupDBList(){
        return backupDBList;
    }

    public SparseBooleanArray getSelectedIds() {
        SparseBooleanArray selectedItemsIds = mSelectedItemsIds;
        removeSelection();
        return selectedItemsIds;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final String formatDate = "dd-MM-yyyy";
        BackupDB backupDB = getItem(position);
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }  else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.fileName.setText(backupDB.getFileName());
        holder.dateCreate.setText(backupDB.getDateCreateToString(formatDate));

        if(!viewHolderList.contains(holder)){
            viewHolderList.add(holder);
        }

        return convertView;
    }

    private void showImportBackupDialog(View selectView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.import_dialog_title);
        builder.setMessage(R.string.import_dialog_message);
        builder.setPositiveButton(R.string.delete_positive, (dialog, which) -> {
            onClickAcceptImport(selectView);
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

    public void add(Uri selectUriBackup, BackupDB backupDB){
        if(selectUriBackup != null){
            boolean isAddBackup = false;
            backupDB.createFile();

            if(!backupDBList.contains(backupDB)){
                isAddBackup = backupDBList.add(backupDB);
            }

            isAddBackup = backupDB.setContent(selectUriBackup, getContext().getContentResolver());

            if(isAddBackup){
                Toast.makeText(getContext(), R.string.toast_add_edit_entity, Toast.LENGTH_SHORT).show();
            }
        }

        notifyDataSetChanged();
    }

    public void rename(@NotNull String fileName, BackupDB editBackupDB){
        if(fileName.isEmpty()){
            Toast.makeText(getContext(), R.string.toast_empty_backup_name, Toast.LENGTH_SHORT).show();
        }

        if(FileManager.isValidFileName(fileName, FileManager.getInvalidCharsFileName())) {
            BackupDB backupDB = new BackupDB(editBackupDB.getLocation(), fileName);
            if(backupDBList.contains(backupDB)){
                Toast.makeText(getContext(), R.string.toast_exists_entity, Toast.LENGTH_SHORT).show();
            }
            else {
                editBackupDB.setFileName(fileName);
                Toast.makeText(getContext(), R.string.toast_edit_entity, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getContext(), getContext().getText(R.string.toast_invalid_backup_name) + " : " + FileManager.getInvalidCharsFileName(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickAcceptImport(View selectView) {
        String fileNameBackupDB = "";

        for(ViewHolder viewHolder : viewHolderList){
            if(viewHolder.importButton == selectView){
                fileNameBackupDB = viewHolder.fileName.getText().toString();
            }
        }

        for(BackupDB backupDB : backupDBList){
            if(backupDB.getFileName().equals(fileNameBackupDB)){
                String locationDirDB = backupDB.getLocation().substring(
                        0,
                        backupDB.getLocation().lastIndexOf(ConstantApplication.DIR_DELIMITER + ConstantApplication.DIR_BACKUP));
                String pathDirDB = locationDirDB + ConstantApplication.DIR_DELIMITER + ConstantApplication.DIR_DB;

                FileManager.remove(pathDirDB + ConstantApplication.DIR_DELIMITER + ConstantApplication.DB_NAME);

                FileManager.create(pathDirDB + ConstantApplication.DIR_DELIMITER + ConstantApplication.DB_NAME, FileManager.TypeFile.FILE);

                File fileBackupDB = new File(backupDB.getLocation(), backupDB.getFileName());
                File fileDB = new File(pathDirDB, ConstantApplication.DB_NAME);
                FileManager.copy(fileBackupDB, fileDB);

                Toast.makeText(getContext(), R.string.toast_restart, Toast.LENGTH_SHORT).show();
                break;
            }
        }
        ProcessPhoenix.triggerRebirth(mContext);
    }
}
