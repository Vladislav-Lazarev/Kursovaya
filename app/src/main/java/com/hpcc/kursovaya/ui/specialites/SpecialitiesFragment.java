package com.hpcc.kursovaya.ui.specialites;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import java.util.List;

public class SpecialitiesFragment extends Fragment {
    private static final String TAG = SpecialitiesFragment.class.getSimpleName();
    private Context currentContext = null;

    FloatingActionButton addSpeciality;
    ListView specialityLSV;
    private View addSpecialityView;
    private View editSpecialityView;
    private SpecialityListAdapter adapter;
    private long mLastClickTime = 0;

    private EditText specText;
    private EditText code;
    List<Speciality> specialityList;


    private View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocaleManager.setLocale(getActivity());
        currentContext = getActivity();
        root = inflater.inflate(R.layout.fragment_specialities, container, false);
        specialityLSV = root.findViewById(R.id.specialitiesLSV);
        addSpeciality = root.findViewById(R.id.fab);
        final Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
        addSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onClickPrepareAddSpeciality();
            }
        });
        specialityList = DBManager.copyObjectFromRealm(
                DBManager.readAll(Speciality.class, ConstantApplication.NAME));
        Log.d(TAG, "DBManager.copyObjectFromRealm = " + specialityList.toString());

        adapter = new SpecialityListAdapter(currentContext,R.layout.listview_item_specialties, specialityList);
        specialityLSV.setAdapter(adapter);
        specialityLSV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        specialityLSV.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = specialityLSV.getCheckedItemCount();
                mode.setTitle(checkedCount +" "+getResources().getString(R.string.cab_select_text));
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
                mLastClickTime = SystemClock.elapsedRealtime();
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
            }
        });
        specialityLSV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Speciality entity = (Speciality) parent.getItemAtPosition(position);
                onClickPrepareEditSpeciality(entity);
            }
        });
        setActionBarTitle();
        return root;
    }

    public void setActionBarTitle(){
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.specialty_list_label));
        ((MainActivity) getActivity()).showOverflowMenu(false);
    }

    private void onClickPrepareAddSpeciality() {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
        builder.setTitle(R.string.dialog_add_speciality);
        builder.setPositiveButton(R.string.popup_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onClickAcceptAddSpeciality(dialog, which);
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

    private void onClickAcceptAddSpeciality(DialogInterface dialog, int which) {
       specText = addSpecialityView.findViewById(R.id.speciality_name_text);
       code = addSpecialityView.findViewById(R.id.code_text);

       String strSpeciality = specText.getText().toString().trim();
       String strCode = code.getText().toString().trim();

       if (strSpeciality.isEmpty() || strCode.isEmpty() ||
               !ConstantApplication.checkUI(ConstantApplication.PATTERN_SPECIALITY, strSpeciality)){
            Toast.makeText(currentContext, R.string.toast_check, Toast.LENGTH_LONG).show();
            onClickPrepareAddSpeciality();
            return;
        }

        int codeSpeciality = Integer.parseInt(code.getText().toString());
        Speciality speciality = new Speciality(strSpeciality, codeSpeciality);

        adapter.write(speciality);
        adapter.update(ConstantApplication.NAME);
    }

    private void prepareDeleteDialog(final ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
        builder.setTitle(R.string.popup_delete_speciality);
        builder.setMessage(R.string.popup_delete_speciality_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                SparseBooleanArray positionDel = specialityLSV.getCheckedItemPositions();
                for (int i = 0; i < positionDel.size(); i++) {
                    int key = positionDel.keyAt(i);
                    if (positionDel.get(key)){
                        Log.d(TAG, "entity = " + specialityList.get(key));
                        adapter.delete(specialityList.get(key));
                    }
                }
                adapter.update(ConstantApplication.NAME);

                if (positionDel.size() == ConstantApplication.ONE){
                    Toast.makeText(currentContext, R.string.toast_del_entity, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(currentContext, R.string.toast_del_many_entity, Toast.LENGTH_SHORT).show();
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

    private void onClickPrepareEditSpeciality(Speciality entity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
        builder.setTitle(R.string.dialog_edit_speciality);
        builder.setPositiveButton(R.string.popup_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < ConstantApplication.CLICK_TIME){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onClickAcceptEditSpeciality(dialog, which, entity);
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

        specText = editSpecialityView.findViewById(R.id.speciality_name_text);
        code = editSpecialityView.findViewById(R.id.code_text);

        specText.setText(entity.getName());
        code.setText(Integer.toString(entity.getCode()));

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

    private void onClickAcceptEditSpeciality(DialogInterface dialog, int which, Speciality entity) {
        String strSpeciality = specText.getText().toString().trim();
        String strCode = code.getText().toString().trim();

        if (strSpeciality.isEmpty() ||
                strCode.isEmpty() ||
                !ConstantApplication.checkUI(ConstantApplication.PATTERN_SPECIALITY, strSpeciality)){
            Toast.makeText(currentContext, R.string.toast_check, Toast.LENGTH_LONG).show();
            onClickPrepareEditSpeciality(entity);
            return;
        }

        int codeSpeciality = Integer.parseInt(code.getText().toString());
        entity.setName(strSpeciality).setCode(codeSpeciality);

        adapter.write(entity);
        adapter.update(ConstantApplication.NAME);
    }
}
