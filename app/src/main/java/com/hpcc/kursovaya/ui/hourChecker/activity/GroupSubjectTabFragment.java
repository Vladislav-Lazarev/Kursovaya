package com.hpcc.kursovaya.ui.hourChecker.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.ui.hourChecker.adapter.GroupSubjectAdapter;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.CustomViewPager;
import com.hpcc.kursovaya.ui.schedule.DayViewPager.DayViewFragment;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class GroupSubjectTabFragment extends Fragment implements GroupSubjectAdapter.ItemClickListener{
    public static final String ARG_PAGE = "ARG_PAGE";

    //private List<AcademicHour> hours;
    private List<DayViewFragment.EventAgregator> events;
    private GroupSubjectAdapter adapter;
    private RecyclerView recyclerView;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;
    private CustomViewPager customViewPager;


    public List<DayViewFragment.EventAgregator> getEvents() {
        return events;
    }

    public void updateAdapter() {
        adapter = new GroupSubjectAdapter(getActivity(),events);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    public static GroupSubjectTabFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        GroupSubjectTabFragment fragment = new GroupSubjectTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            int page = getArguments().getInt(ARG_PAGE);
            List<AcademicHour> hours = new ArrayList<>();
            switch (page){
                case 0:
                    hours = GroupSubjectCheckActivity.unreadHours;
                    break;
                case 1:
                    hours = GroupSubjectCheckActivity.readHours;
                    break;
                case 2:
                    hours = GroupSubjectCheckActivity.canceledHours;
                    break;
            }
            events = new ArrayList<>();
            List<TemplateAcademicHour> templateAcademicHours = new ArrayList<>();
            for(AcademicHour academicHour : hours){
                DayViewFragment.EventAgregator eventAgregator = new DayViewFragment.EventAgregator();
                eventAgregator.anotherEvent=null;
                eventAgregator.academicHour=academicHour;
                templateAcademicHours.add(academicHour.getTemplateAcademicHour());
                events.add(eventAgregator);
            }
            Log.d("","");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_subject_tab_page,container,false);
        recyclerView = view.findViewById(R.id.hourList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new GroupSubjectAdapter(getActivity(),events);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        actionModeCallback = new ActionModeCallback();

        return  view;
    }

    @Override
    public void onItemClick(View view, int position) {
        if(actionMode==null) {
            DayViewFragment.EventAgregator event = adapter.getItem(position);
            DateTime currentDate = DateTime.now();
            HandleGroupSubjectTabDialog handleDialog = null;
            if (event.academicHour != null) {
                int secondCellHour = position + ((position % ConstantApplication.TWO == ConstantApplication.ZERO) ? 1 : -1);
                AcademicHour secondAcademicHour = null;
                if (secondCellHour < adapter.getItemCount()) {
                    if (adapter.getItem(secondCellHour) != null && adapter.getItem(secondCellHour).academicHour != null) {
                        secondAcademicHour = adapter.getItem(secondCellHour).academicHour;
                    }
                }
                handleDialog = HandleGroupSubjectTabDialog.newInstance(getActivity(), currentDate.getDayOfWeek() - 1, position, currentDate, event.academicHour,
                        secondCellHour, secondAcademicHour, adapter);
            }
            handleDialog.setTargetFragment(GroupSubjectTabFragment.this, 1);
            handleDialog.show(getFragmentManager(), "handleDialog");
        } else {
            toggleSelection(position);
        }
    }

    @Override
    public void onLongItemClick(View view, int position) {
        enableActionMode(position);
    }

    public void setEvents(List<DayViewFragment.EventAgregator> events) {
        this.events = events;
    }


    private void enableActionMode() {
        customViewPager = ((GroupSubjectCheckActivity)getActivity()).getViewPager();
        customViewPager.setPageScrollEnabled(false);
        customViewPager.invalidate();
        if (actionMode == null) {
            ((GroupSubjectCheckActivity) getActivity()).getToolbar().setVisibility(View.GONE);
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }
        actionMode.setTitle(0+" "+getResources().getString(R.string.cab_select_text));
    }

    private void enableActionMode(int position) {
        customViewPager = ((GroupSubjectCheckActivity)getActivity()).getViewPager();
        customViewPager.setPageScrollEnabled(false);
        customViewPager.invalidate();
        if(adapter.getItem(position)==null){
            Toast.makeText(getActivity(), R.string.cant_select_empty_class, Toast.LENGTH_SHORT).show();
        } else {
            if (actionMode == null) {
                ((GroupSubjectCheckActivity) getActivity()).getToolbar().setVisibility(View.GONE);
                actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
            }
            toggleSelection(position);
        }
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position,recyclerView.findViewHolderForAdapterPosition(position));
        int count = adapter.getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
            actionMode = null;
        } else {
            actionMode.setTitle((count)+" "+getResources().getString(R.string.cab_select_text));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            /*if(currentActionMode==DELETE) {
                mode.getMenuInflater().inflate(R.menu.activity_listview, menu);
            } else if (currentActionMode==READ) {
                mode.getMenuInflater().inflate(R.menu.popup_day_read,menu);
            } else {*/
            mode.getMenuInflater().inflate(R.menu.activity_listview,menu);
            //}
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
                case R.id.uncompleted_classes:
                    prepareUncompletedClasessDialog(mode);
                    return true;
                case R.id.completed_classes:
                    prepareCompletedClassesDialog(mode);
                    return true;
                case R.id.canceled_classes:
                    prepareCanceledClassesDialog(mode);
                    return true;
                case R.id.uncancelled_classes:
                    prepareUncancelledClassesDialog(mode);
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelections();
            actionMode = null;
            customViewPager.setPageScrollEnabled(true);
            ((GroupSubjectCheckActivity)getActivity()).getToolbar().setVisibility(View.VISIBLE);
        }

    }

    public GroupSubjectAdapter getAdapter() {
        return adapter;
    }

    private void prepareUncancelledClassesDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.popup_cut_classes_template);
        builder.setMessage(R.string.popup_uncut_classes_content);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onUncancelledClassesClick(mode);
            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void onUncancelledClassesClick(ActionMode mode) {
        List selectedItemPositions = adapter.getSelectedItems();
        for (int i = 0; i < selectedItemPositions.size(); i++) {
            adapter.setCanceled((Integer) selectedItemPositions.get(i),false);
        }
        mode.finish();
        adapter.notifyDataSetChanged();
    }

    private void prepareCanceledClassesDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.popup_cut_classes_template);
        builder.setMessage(R.string.popup_cut_classes_content);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCancelClassesAcceptClick(mode);
            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void onCancelClassesAcceptClick(ActionMode mode) {
        List selectedItemPositions = adapter.getSelectedItems();
        for (int i = 0; i < selectedItemPositions.size(); i++) {
            adapter.setCanceled((Integer) selectedItemPositions.get(i),true);
        }
        mode.finish();
        adapter.notifyDataSetChanged();
    }

    private void prepareUncompletedClasessDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.popup_complete_classes_template);
        builder.setMessage(R.string.popup_uncomplete_classes_content);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCancelCompletedClassesAcceptClick(mode);
            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void onCancelCompletedClassesAcceptClick(ActionMode mode) {
        List selectedItemPositions = adapter.getSelectedItems();
        for (int i = 0; i < selectedItemPositions.size(); i++) {
            adapter.setCompleted((Integer) selectedItemPositions.get(i),false);
        }
        mode.finish();
       adapter.notifyDataSetChanged();
    }

    private void prepareCompletedClassesDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.popup_complete_classes_template);
        builder.setCancelable(false);
        builder.setMessage(R.string.popup_complete_classes_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCompletedClassesAcceptClick(mode);
            }
        });
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void onCompletedClassesAcceptClick(ActionMode mode) {
        List selectedItemPositions = adapter.getSelectedItems();
        for (int i = 0; i < selectedItemPositions.size(); i++) {
           adapter.setCompleted((Integer) selectedItemPositions.get(i),true);
        }
        mode.finish();
        adapter.notifyDataSetChanged();
    }

    private void prepareDeleteDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.popup_delete_classes_template);
        builder.setMessage(R.string.popup_delete_classes_content);
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDeleteClassesAcceptClick(mode);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.delete_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void onDeleteClassesAcceptClick(ActionMode mode) {
        List selectedItemPositions = adapter.getSelectedItems();
        for (int i = 0; i < selectedItemPositions.size(); i++) {
            adapter.delete((Integer) selectedItemPositions.get(i));
        }
        adapter.updateAcademicHourList();
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() == 0)
            ((GroupSubjectCheckActivity)getActivity()).getToolbar().setVisibility(View.VISIBLE);
        mode.finish();
        actionMode = null;

    }
}
