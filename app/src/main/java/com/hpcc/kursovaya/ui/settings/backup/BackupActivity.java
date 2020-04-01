package com.hpcc.kursovaya.ui.settings.backup;

import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.setting.BackupDB;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BackupActivity  extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    private static String TAG = BackupActivity.class.getSimpleName();
    FloatingActionButton addBackup;
    ListView backupLSV;
    BackupListAdapter adapter;
    List<BackupDB> backupsList = new ArrayList<>();
    BackupDB speciality;
    private View addBackupView;
    private View editBackupView;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_backups);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
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
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onClickPrepareAddBackup();
            }
        });
        backupLSV = findViewById(R.id.backupLSV);
        BackupDB db1 = new BackupDB();
        db1.setName("ept");
        db1.setDateCreate(Calendar.getInstance().getTime());
        db1.setFileName("/DB.realm");
        backupsList.add(db1);
        adapter = new BackupListAdapter(this,R.layout.listview_item_backup,backupsList);
        backupLSV.setAdapter(adapter);
        backupLSV.setOnItemClickListener((parent,view,position,id)->{
            if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
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

    private void invokeShareIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        StringBuilder sbDate = new StringBuilder();
        Calendar date = Calendar.getInstance();
        sbDate.append(date.get(Calendar.YEAR)).append(".").append(date.get(Calendar.MONTH)).append(".").append(date.get(Calendar.DAY_OF_MONTH));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.backups_theme_email)+" "+sbDate.toString());
        intent.setType("image/jpeg"); /* This example is sharing jpeg images. */

        ArrayList<Uri> files = new ArrayList<Uri>();

        String folderPath = Environment.getExternalStorageDirectory()+"/TeachersDiaryBackups";
        for(BackupDB entry : backupsList) {
            String filePath = folderPath + entry.getFileName();
            File file = new File(filePath);
            if(file.exists()) {
                Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
                files.add(uri);
            } else {
                Toast.makeText(this,getString(R.string.file_error_toast_part1)+entry.getFileName()+" "+getString(R.string.file_error_toast_part2),Toast.LENGTH_LONG).show();
            }
        }
        Log.d(TAG, files.size()+"");
        if(!(files.size()==0)) {
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
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
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                SparseBooleanArray selected = adapter
                        .getSelectedIds();
                for (int i = (selected.size() - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        BackupDB selecteditem = adapter.getItem(selected.keyAt(i));
                        // Remove selected items following the ids
                        adapter.remove(selecteditem);
                    }
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

    private void onClickPrepareEditBackup(final BackupDB entry,final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_name_of_backup);
        builder.setPositiveButton(R.string.popup_edit,(dialog, which) ->{
            if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            onClickAcceptEditBackup(dialog,which,entry,position);
        } );
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel,((dialog, which) -> {
            dialog.cancel();
        }));
        editBackupView = getLayoutInflater().inflate(R.layout.dialog_backup,null);
        builder.setView(editBackupView);

        EditText backText = editBackupView.findViewById(R.id.backup_name_text);
        backText.setText(entry.getName());

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

    private void onClickAcceptEditBackup(DialogInterface dialog, int which, BackupDB entry, int position) {
        EditText backupText = editBackupView.findViewById(R.id.backup_name_text);
        String strBackup = backupText.getText().toString();
        entry.setName(strBackup);
    }

    private void onClickPrepareAddBackup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_add_backup);
        builder.setPositiveButton(R.string.dialog_button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onClickAcceptAddBackup(dialog, which);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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

    private void onClickAcceptAddBackup(DialogInterface dialog, int which) {
        EditText backupText = addBackupView.findViewById(R.id.backup_name_text);
        String strBackup = backupText.getText().toString();
        BackupDB backupDB = new BackupDB();
        backupDB.setName(strBackup);
        backupDB.setDateCreate(Calendar.getInstance().getTime());
        backupDB.setFileName("/DB (3).realm");
        backupsList.add(backupDB);
        adapter.notifyDataSetChanged();
    }
}
