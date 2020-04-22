package com.hpcc.kursovaya.ui.settings.backup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.setting.BackupDB;
import com.hpcc.kursovaya.dao.entity.setting.FileManager;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BackupActivity  extends AppCompatActivity {
    private static String TAG = BackupActivity.class.getSimpleName();
    private static final int PICK_FILE_REQUEST_CODE = 1;

    private FloatingActionButton addBackup;
    private ImageButton importExternalBackupDB;
    private ListView backupLSV;
    private BackupListAdapter adapter;
    private String LOCATION_BACKUP_DB;
    private String LOCATION_DB;
    private Uri selectUriBackup;
    private View addBackupView;
    private View editBackupView;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.activity_list_backups);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        addBackup = findViewById(R.id.fab);
        addBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                File fileDB = new File(LOCATION_DB + ConstantApplication.DIR_DELIMITER + ConstantApplication.DB_NAME);
                selectUriBackup = Uri.fromFile(fileDB);
                onClickPrepareAddBackup();
            }
        });

        importExternalBackupDB = findViewById(R.id.externalBackup);
        importExternalBackupDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showFileManager();
            }
        });

        backupLSV = findViewById(R.id.backupLSV);

        //Init backupsList files from DIR_BACKUP
        LOCATION_BACKUP_DB = getExternalFilesDir(null).getPath() + ConstantApplication.DIR_DELIMITER + ConstantApplication.DIR_BACKUP;
        LOCATION_DB = getExternalFilesDir(null).getPath() + ConstantApplication.DIR_DELIMITER + ConstantApplication.DIR_DB;
        List<BackupDB> backupsList = new ArrayList<>();
        List<File> backupFiles = FileManager.getFiles(LOCATION_BACKUP_DB);
        for (File backupFile : backupFiles) {
                backupsList.add(new BackupDB(backupFile.getPath()));
        }

        adapter = new BackupListAdapter(this,R.layout.listview_item_backup,backupsList);
        backupLSV.setAdapter(adapter);
        backupLSV.setOnItemClickListener((parent,view,position,id)->{
            if(SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            BackupDB entry = (BackupDB) parent.getItemAtPosition(position);
            onClickPrepareEditBackup(entry,position);
        });
        backupLSV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        backupLSV.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = backupLSV.getCheckedItemCount();
                mode.setTitle(checkedCount +" "+getResources().getString(R.string.cab_select_text));
                adapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                toolbar.setVisibility(View.GONE);
                mode.getMenuInflater().inflate(R.menu.activity_backups, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                mLastClickTime = SystemClock.elapsedRealtime();
                switch (item.getItemId()) {
                    case R.id.share:
                        invokeShareIntent();
                        return true;
                    case R.id.delete:
                        prepareDeleteDialog(mode);
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                toolbar.setVisibility(View.VISIBLE);
                adapter.removeSelection();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }


    private void invokeShareIntent() {
        List<BackupDB> sendBackupDBList = adapter.getSelectBackupDBList();
        StringBuilder message = new StringBuilder(getString(R.string.backups_theme_email) + ": ");
        String formatDateCreate = "dd-MM-yyyy";
        for (BackupDB sendBackupDB : sendBackupDBList){
            message.append(sendBackupDB.getFileName() +" - " + sendBackupDB.getDateCreateToString(formatDateCreate));
            if(sendBackupDBList.indexOf(sendBackupDB) != sendBackupDBList.size()-1){
                message.append(", ");
            }
        }

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, message.toString());
        intent.setType("*/*");

        ArrayList<Uri> sendBackupFiles = new ArrayList<Uri>();
        for(BackupDB sendBackupDB : sendBackupDBList) {
            File sendBackupDBFile = new File(sendBackupDB.getLocation(), sendBackupDB.getFileName());
            if(sendBackupDBFile.exists()) {
                Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", sendBackupDBFile);
                sendBackupFiles.add(uri);
            } else {
                Toast.makeText(this,getString(R.string.file_error_toast_part1)+sendBackupDB.getFileName()+" "+getString(R.string.file_error_toast_part2),Toast.LENGTH_LONG).show();
            }
        }

        if(sendBackupFiles.size() != 0) {
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, sendBackupFiles);
            startActivity(intent);
        } else {
          AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.backup_sending_title);
            builder.setMessage(R.string.backups_sending_error_message);
            builder.setPositiveButton(R.string.ok_button,((dialog, which) -> {
                dialog.cancel();
            }));
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
                }
            });
            dialog.show();
        }
    }

    private void prepareDeleteDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_backups_label);
        builder.setMessage(R.string.delete_backups_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                List<BackupDB> backupDBList = adapter.getSelectBackupDBList();
                adapter.removeSelection();

                adapter.getBackupDBList().removeAll(backupDBList);
                for(BackupDB delBackupDB : backupDBList){
                    FileManager.remove(delBackupDB.getFileName(), delBackupDB.getLocation());
                }

                if(backupDBList.size() < 2){
                    Toast.makeText(getApplicationContext(), R.string.toast_del_entity, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.toast_del_many_entity, Toast.LENGTH_SHORT).show();
                }

                mode.finish();

            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mode.finish();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void onClickPrepareEditBackup(final BackupDB editBackupDB, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_name_of_backup);
        builder.setPositiveButton(R.string.popup_edit,(dialog, which) ->{
            if(SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            onClickAcceptEditBackup(editBackupDB);
        } );
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel,((dialog, which) -> {
            dialog.cancel();
        }));
        editBackupView = getLayoutInflater().inflate(R.layout.dialog_backup,null);
        builder.setView(editBackupView);

        EditText backText = editBackupView.findViewById(R.id.backup_name_text);
        String fileNameBackup = editBackupDB.getFileName();
        backText.setText(fileNameBackup.substring(0, fileNameBackup.lastIndexOf(".")));

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener((arg0)->{
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void onClickAcceptEditBackup(BackupDB editBackupDB) {
        EditText backupText = editBackupView.findViewById(R.id.backup_name_text);
        String fileNameBackupDB = backupText.getText().toString() + ConstantApplication.DB_EXTENSION;
        adapter.rename(fileNameBackupDB, editBackupDB);
    }

    private void onClickPrepareAddBackup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_add_backup);
        builder.setPositiveButton(R.string.dialog_button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                EditText backupText = addBackupView.findViewById(R.id.backup_name_text);
                String fileNameBackupDB = backupText.getText().toString() + ConstantApplication.DB_EXTENSION;
                BackupDB backupDB = new BackupDB(LOCATION_BACKUP_DB, fileNameBackupDB);

                if(backupText.getText().toString().isEmpty()) {
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), R.string.toast_empty_backup_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!FileManager.isValidFileName(backupText.getText().toString(), BackupDB.getInvalidCharsBackupDB())){
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_invalid_backup_name) + " : " + BackupDB.getInvalidCharsBackupDB(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (FileManager.exists(LOCATION_BACKUP_DB + ConstantApplication.DIR_DELIMITER + fileNameBackupDB)) {
                    dialog.cancel();
                    onClickPrepareExistBackupDB(backupDB);
                } else {
                    adapter.add(selectUriBackup, backupDB);
                    selectUriBackup = null;
                }
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectUriBackup = null;
                dialog.cancel();
            }
        });
        addBackupView = getLayoutInflater().inflate(R.layout.dialog_backup, null);
        builder.setView(addBackupView);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    private void showFileManager(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(getExternalFilesDir(null).getPath());
        intent.setDataAndType(uri, "*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    private void onClickPrepareExistBackupDB(BackupDB backupDB){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.toast_exist_backup);
        builder.setMessage(R.string.toast_exists_entity);
        builder.setPositiveButton(R.string.popup_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                adapter.add(selectUriBackup, backupDB);
                selectUriBackup = null;
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectUriBackup = null;
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK){
            selectUriBackup = data.getData();
            String fileNameBackupDB = FileManager.getFileName(selectUriBackup);

            if(fileNameBackupDB.contains(ConstantApplication.DB_EXTENSION)) {
                onClickPrepareAddBackup();
                EditText backupText = addBackupView.findViewById(R.id.backup_name_text);

                if(fileNameBackupDB.contains(".")) {
                    backupText.setText(fileNameBackupDB.substring(0, fileNameBackupDB.indexOf(".")));
                }else{
                    backupText.setText(fileNameBackupDB);
                }
            }else{
                Toast.makeText(this,
                        getString(R.string.toast_invalid_backup_file) + ConstantApplication.DB_EXTENSION,
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
}
