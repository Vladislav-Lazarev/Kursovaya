package com.hpcc.kursovaya.ui.settings.specialities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;
import com.hpcc.kursovaya.dao.entity.query.DBManager;

import java.util.List;

import io.realm.RealmList;


public class SpecialitiesActivity extends AppCompatActivity {
    private static final String TAG = SpecialitiesActivity.class.getSimpleName();

    static void setSpinnerString(Spinner spin, String str) {
        for(int i= 0; i < spin.getAdapter().getCount(); i++) {
            if(spin.getAdapter().getItem(i).toString().contains(str)) {
                spin.setSelection(i);
            }
        }
    }

    FloatingActionButton addSpeciality;
    ListView specialityLSV;
    SpecialityListAdapter adapter;
    private View addSpecialityView;
    private View editSpecialityView;
    List<Speciality> specialitiesList = new RealmList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_specialties);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        addSpeciality = findViewById(R.id.fab);
        addSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPrepareAddSpeciality();
            }
        });
        specialityLSV = findViewById(R.id.specialitiesLSV);

        specialitiesList.addAll(DBManager.readAll(Speciality.class, ConstantEntity.ID));
        adapter = new SpecialityListAdapter(this,R.layout.listview_item_specialties, specialitiesList);
        specialityLSV.setAdapter(adapter);
        specialityLSV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        specialityLSV.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = specialityLSV.getCheckedItemCount();
                mode.setTitle(checkedCount +" "+getResources().getString(R.string.cab_select_text));
                adapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                toolbar.setVisibility(View.GONE);
                mode.getMenuInflater().inflate(R.menu.activity_listview, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
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
        specialityLSV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Speciality entry = (Speciality) parent.getItemAtPosition(position);
                onClickPrepareEditSpeciality(entry, position);
            }
        });

    }

    private void onClickPrepareAddSpeciality() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_add_speciality);
        builder.setPositiveButton(R.string.dialog_button_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickAcceptAddGroup(dialog, which);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        addSpecialityView = getLayoutInflater().inflate(R.layout.dialog_speciality, null);
        builder.setView(addSpecialityView);
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
    private void onClickAcceptAddGroup(DialogInterface dialog, int which) {
        EditText specText = addSpecialityView.findViewById(R.id.speciality_name_text);
        Spinner courseSpinner = addSpecialityView.findViewById(R.id.courseSpinner);
        int countCourse = Integer.parseInt(courseSpinner.getSelectedItem().toString());

        Speciality newSpeciality = new Speciality(specText.getText().toString(), countCourse);
        DBManager.write(newSpeciality);

        specialitiesList.add(newSpeciality);
        adapter.notifyDataSetChanged();
    }

    private void onClickPrepareEditSpeciality(final Speciality entry, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_edit_speciality);
        builder.setPositiveButton(R.string.popup_edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickAcceptEditGroup(dialog, which, entry, position);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        editSpecialityView = getLayoutInflater().inflate(R.layout.dialog_speciality, null);
        builder.setView(editSpecialityView);

        EditText specText = editSpecialityView.findViewById(R.id.speciality_name_text);
        specText.setText(entry.getName());

        Spinner courseSpinner = editSpecialityView.findViewById(R.id.courseSpinner);
        setSpinnerString(courseSpinner, String.valueOf(entry.getCountCourse()));

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
    private void onClickAcceptEditGroup(DialogInterface dialog, int which, Speciality speciality, int position) {
        EditText specText = editSpecialityView.findViewById(R.id.speciality_name_text);
        String strSpeciality = specText.getText().toString();
        Spinner courseSpinner = editSpecialityView.findViewById(R.id.courseSpinner);
        int countCourse = Integer.parseInt(courseSpinner.getSelectedItem().toString());

        speciality.setName(strSpeciality)
                .setCountCourse(countCourse);
        DBManager.write(speciality);

        specialitiesList.set(position, speciality);
        adapter.notifyDataSetChanged();
    }

    private void prepareDeleteDialog(final ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.popup_delete_speciality);
        builder.setMessage(R.string.popup_delete_speciality_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SparseBooleanArray selected = adapter
                        .getSelectedIds();
                for (int i = (selected.size() - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        Speciality selecteditem = adapter
                                .getItem(selected.keyAt(i));
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
}