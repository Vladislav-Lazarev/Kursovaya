package com.hpcc.kursovaya.ui.schedule.DayViewPager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hpcc.kursovaya.MainActivity;
import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.schedule.AddClass;
import com.hpcc.kursovaya.ui.schedule.DayScheduleFragment;
import com.hpcc.kursovaya.ui.schedule.EditClass;
import com.hpcc.kursovaya.ui.schedule.MonthScheduleFragment;
import com.hpcc.kursovaya.ui.schedule.WeekViewPager.WeekViewFragment;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DayViewFragment extends Fragment implements DayClassAdapter.ItemClickListener {
    public static final String ARGUMENT_DAY_FROM_CURRENT="arg_day_from_current";
    private static final String TAG = DayViewFragment.class.getSimpleName();
    private static final int ADD_CLASS = 1;
    private static final int EDIT_CLASS = 2;
    public static final int DELETE = 1;
    public static final int READ = 2;
    public static final int CANCEL = 3;
    private DateTime currentDate;
    private View root;
    private RecyclerView rv;
    private DayClassAdapter adapter;
    private List<AcademicHour> academicHourList;
    private Context context;
    private CustomViewPager customViewPager;
    private int currentActionMode = DELETE;


    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;
    private ImageButton  toCurrentDay;
    private TextView currentDayText;

    public static Fragment newInstance(int position) {
        DayViewFragment pageFragment = new DayViewFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_DAY_FROM_CURRENT,position);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        MainActivity activity = (MainActivity) getActivity();
        switch (item.getItemId()){
            case R.id.action_deleteClasses:
                currentActionMode = DELETE;
                enableActionMode();
                return false;
            case R.id.action_importTemplates:
                activity.prepareActionImportTemplates();
                return true;
            case R.id.action_checkRead:
                currentActionMode = READ;
                enableActionMode();
                return true;
            case R.id.action_checkCanceled:
                currentActionMode = CANCEL;
                enableActionMode();
                return true;
            case R.id.action_reportPeriod:
                if(DBManager.readAll(Speciality.class).size()!=0) {
                    activity.prepareReporDatePicker();
                } else {
                    Toast.makeText(getActivity(), R.string.toast_fragment_no_specialities, Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        int dayFromCurrent = getArguments().getInt(ARGUMENT_DAY_FROM_CURRENT);
        DateTime firstDayOfWeek = DateTime.parse("01/01/1990 00:00:00", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));
        currentDate = firstDayOfWeek.plusDays(dayFromCurrent);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        LocaleManager.setLocale(getActivity());
        root = inflater.inflate(R.layout.fragment_day,null);
        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                    DateTime startDate  =  DateTime.parse("01/01/1990 00:00:00", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));
                    int monthDifference = Months.monthsBetween(startDate,currentDate).getMonths();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_top,R.anim.slide_out_bottom);
                    ((MainActivity)getActivity()).setCurrentViewSelected(MainActivity.MONTH);
                    ft.replace(R.id.nav_host_fragment, new MonthScheduleFragment(monthDifference),MainActivity.MONTH_TAG);
                    ft.commit();
                    return true;
                }
                return false;
            }
        });
        context = getActivity();
        TextView dayOfMonth = root.findViewById(R.id.dayOfMonthLabel);
        dayOfMonth.setText(Integer.toString(currentDate.getDayOfMonth()));
        Log.i(TAG, LocaleManager.getLanguage(getActivity()));
        String lang = LocaleManager.getLanguage(getActivity()).split("-")[0];
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE", new Locale(lang));
        TextView dayOfWeek = root.findViewById(R.id.dayOfWeekLabel);
        dayOfWeek.setText(dayOfWeekFormat.format(currentDate.toDate()));

        Toolbar toolbar = ((MainActivity)getActivity()).getToolbar();
        toCurrentDay = toolbar.findViewById(R.id.toCurrentDay);
        currentDayText = toolbar.findViewById(R.id.currentDayText);
        rv = root.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        academicHourList = Arrays.asList(new AcademicHour[10]);
        List<AcademicHour> actualAcademicHours = DBManager.copyObjectFromRealm(DBManager.readAll(AcademicHour.class,"date",currentDate.toDate()));
        for(AcademicHour academicHour:actualAcademicHours){
            TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
            academicHourList.set(templateAcademicHour.getNumberHalfPairButton(),academicHour);
        }
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DayClassAdapter(getActivity(),academicHourList);
        adapter.setClickListener(this);
        rv.setAdapter(adapter);
        actionModeCallback = new ActionModeCallback();
        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
        AcademicHour academicHour = adapter.getItem(position);
        if(actionMode==null) {
            Intent intent;
            if (academicHour == null) {
                intent = new Intent(getActivity(), AddClass.class);
                intent.putExtra("classDay", currentDate.getDayOfWeek() - 1);
                intent.putExtra("classHour", position);
                intent.putExtra("dayOfWeek", currentDate);
                startActivityForResult(intent, ADD_CLASS);
            } else {
                intent = new Intent(getActivity(), EditClass.class);
                intent.putExtra("classDay", currentDate.getDayOfWeek() - 1);
                intent.putExtra("classHour", position);
                intent.putExtra("dayOfWeek", currentDate);
                intent.putExtra("currentCell", academicHour);
                int secondCellHour = position + ((position % ConstantApplication.TWO == ConstantApplication.ZERO) ? 1 : -1);
                intent.putExtra("secondClassHour", secondCellHour);
                AcademicHour secondAcademicHour = adapter.getItem(secondCellHour);
                intent.putExtra("secondCell", secondAcademicHour);
                startActivityForResult(intent, EDIT_CLASS);
            }
        } else {
            if(academicHour==null){
                Toast.makeText(context,"Ви не можете обрати порожнє заняття", Toast.LENGTH_SHORT).show();
            } else {
                toggleSelection(position);
            }
        }
    }

    @Override
    public void onLongItemClick(View view, int position) {
        enableActionMode(position);
    }

    private void enableActionMode() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        DayScheduleFragment daySchedule = (DayScheduleFragment) manager.findFragmentByTag("daySchedule");
        customViewPager = daySchedule.getViewPager();
        customViewPager.setPageScrollEnabled(false);
        customViewPager.invalidate();
            if (actionMode == null) {
                ((MainActivity) getActivity()).getToolbar().setVisibility(View.GONE);
                actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
            }
        actionMode.setTitle(0+" "+getResources().getString(R.string.cab_select_text));
    }

    private void enableActionMode(int position) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        DayScheduleFragment daySchedule = (DayScheduleFragment) manager.findFragmentByTag(MainActivity.DAY_TAG);
        customViewPager = daySchedule.getViewPager();
        customViewPager.setPageScrollEnabled(false);
        customViewPager.invalidate();
        if(adapter.getItem(position)==null){
            Toast.makeText(context,"Ви не можете обрати порожнє заняття", Toast.LENGTH_SHORT).show();
        } else {
            if (actionMode == null) {
                ((MainActivity) getActivity()).getToolbar().setVisibility(View.GONE);
                actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
            }
            toggleSelection(position);
        }
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position,rv.findViewHolderForAdapterPosition(position));
        int count = adapter.getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
            actionMode = null;
        } else {
            actionMode.setTitle((count)+" "+getResources().getString(R.string.cab_select_text));
            actionMode.invalidate();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,resultCode+""+requestCode);
        if(resultCode== Activity.RESULT_OK){
            boolean isTwoHours = data.getBooleanExtra("isTwoHour",false);
            int classDay = data.getIntExtra("classDay", 0);
            int classHour = data.getIntExtra("classHour", 0);
            switch (requestCode){
                case ADD_CLASS:
                    AcademicHour academicHourFirst = data.getParcelableExtra("firstHour");
                    academicHourList.set(classHour,academicHourFirst);

                    WeekViewFragment.repeatForWeeks(academicHourFirst,classDay,classHour,getActivity());
                    if(isTwoHours){
                        AcademicHour academicHourSecond = data.getParcelableExtra("secondHour");
                        int secondCellShift = data.getIntExtra("secondCellPosition",0);
                        academicHourList.set(classHour+secondCellShift,academicHourSecond);
                        WeekViewFragment.repeatForWeeks(academicHourSecond,classDay,classHour+secondCellShift,getActivity());
                    }
                    Log.d(TAG,classDay + " " + classHour);
                    break;
                case EDIT_CLASS:
                    AcademicHour academicHourEditedFirst = data.getParcelableExtra("firstHour");
                    boolean isHourChanged = data.getBooleanExtra("clearSecondCell",false);
                    academicHourList.set(classHour,academicHourEditedFirst);
                    //classes.get(classDay).get(classHour).setAcademicHour(academicHourEditedFirst);
                    WeekViewFragment.repeatForWeeks(academicHourEditedFirst,classDay,classHour,getActivity());
                    if(isHourChanged){
                        int secondCellShift = data.getIntExtra("secondClassHour",0);
                        AcademicHour academicHourSecond = academicHourList.get(secondCellShift);
                        if(academicHourSecond!=null && academicHourSecond.getTemplateAcademicHour()!=null) {
                            DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID, academicHourSecond.getTemplateAcademicHour().getId());
                            DBManager.delete(AcademicHour.class, ConstantApplication.ID, academicHourSecond.getId());
                        }
                        academicHourList.set(secondCellShift,null);
                        //classes.get(classDay).get(secondCellShift).clearButtonContent();
                    }
                    if(isTwoHours){
                        AcademicHour academicHourEditedSecond = data.getParcelableExtra("secondHour");
                        int secondCellShift = data.getIntExtra("secondClassHour",0);
                        academicHourList.set(secondCellShift,academicHourEditedSecond);
                        //classes.get(classDay).get(secondCellShift).setAcademicHour(academicHourEditedSecond);
                        WeekViewFragment.repeatForWeeks(academicHourEditedSecond,classDay,secondCellShift,getActivity());
                    }

                    Log.d(TAG,classDay + " " + classHour);
                    break;
                default:
                    return;
            }
            adapter.notifyDataSetChanged();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if(currentActionMode==DELETE) {
                mode.getMenuInflater().inflate(R.menu.activity_listview, menu);
            } else if (currentActionMode==READ) {
                mode.getMenuInflater().inflate(R.menu.popup_day_read,menu);
            } else {
                mode.getMenuInflater().inflate(R.menu.popup_day_cancelled,menu);
            }
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
            ((MainActivity)getActivity()).getToolbar().setVisibility(View.VISIBLE);
        }
    }

    private void prepareUncancelledClassesDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() == 0)
            ((MainActivity)getActivity()).getToolbar().setVisibility(View.VISIBLE);
        mode.finish();
        actionMode = null;

    }


    @Override
    public void onResume() {
        super.onResume();
        if (isResumed() && !((MainActivity) getActivity()).isLanguageChanged()) {
            //((MainActivity)getActivity()).setWeeksFromCurrent();
            if (((MainActivity) getActivity()).isScheduleSelected()) {
                StringBuilder title = new StringBuilder();
                title.append(monthNumberToString(currentDate.getMonthOfYear() - 1)).append(", ").append(currentDate.getYear());
                if (currentDate.getYear() == DateTime.now().getYear() && currentDate.getMonthOfYear() == DateTime.now().getMonthOfYear() && currentDate.getDayOfMonth()==DateTime.now().getDayOfMonth()) {
                    toCurrentDay.setVisibility(View.GONE);
                    currentDayText.setVisibility(View.GONE);
                } else {
                    toCurrentDay.setVisibility(View.VISIBLE);
                    currentDayText.setVisibility(View.VISIBLE);
                }
                //Log.d(TAG, firstDayOfWeek.toString());
                //refreshGrid(firstDayOfWeek,firstDayOfWeek.plusDays(6));
                ((MainActivity) getActivity()).setActionBarTitle(title.toString());
            }
        } else if (((MainActivity) getActivity()).isLanguageChanged()) {
            ((MainActivity) getActivity()).showOverflowMenu(false);
        }
    }

        private String monthNumberToString(int monthNumber) {
            String monthStr = new String();
            Resources res = getResources();
            switch (monthNumber) {
                case 1:
                    monthStr = res.getString(R.string.January);
                    break;
                case 2:
                    monthStr = res.getString(R.string.February);
                    break;
                case 3:
                    monthStr = res.getString(R.string.March);
                    break;
                case 4:
                    monthStr = res.getString(R.string.April);
                    break;
                case 5:
                    monthStr = res.getString(R.string.May);
                    break;
                case 6:
                    monthStr = res.getString(R.string.June);
                    break;
                case 7:
                    monthStr = res.getString(R.string.July);
                    break;
                case 8:
                    monthStr = res.getString(R.string.August);
                    break;
                case 9:
                    monthStr = res.getString(R.string.September);
                    break;
                case 10:
                    monthStr = res.getString(R.string.October);
                    break;
                case 11:
                    monthStr = res.getString(R.string.November);
                    break;
                default:
                    monthStr = res.getString(R.string.December);
            }
            return monthStr;
        }
}
