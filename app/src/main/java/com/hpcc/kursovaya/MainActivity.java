package com.hpcc.kursovaya;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.hpcc.kursovaya.ui.groups.GroupsFragment;
import com.hpcc.kursovaya.ui.report.GeneratedReportActivity;
import com.hpcc.kursovaya.ui.schedule.ScheduleFragment;
import com.hpcc.kursovaya.ui.settings.SettingsFragment;
import com.hpcc.kursovaya.ui.subjects.SubjectsFragment;
import com.hpcc.kursovaya.ui.templates.TemplatesFragment;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Menu fuckingMenu;
    private final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private boolean isSelectMode = false;
    private DrawerLayout drawer;
    private Toolbar toolbar1;
    private Toolbar toolbarCompleteClasses;
    private Toolbar toolbarCanceledClasses;
    private Toolbar toolbar;
    private View actionDatePicker;
    private View importTemplates;
    private View genReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar1 = findViewById(R.id.toolbarEdit);
        toolbarCompleteClasses = findViewById(R.id.toolbarComplete);
        toolbarCanceledClasses = findViewById(R.id.toolbarCancel);
        toolbarCanceledClasses.setVisibility(View.GONE);
        toolbarCompleteClasses.setVisibility(View.GONE);
        toolbar1.setVisibility(View.GONE);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment,
                    new ScheduleFragment(), getResources().getString(R.string.scheduleTag)).commit();
            navigationView.setCheckedItem(R.id.nav_schedule);
        }

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        /*Speciality specialityRPZ = new Speciality(3, "РПЗ", 3);
        DBManager.write(specialityRPZ);

        // TODO ошибка в считывание данных
        Speciality qaw = DBManager.read(Speciality.class, ConstantEntity.NAME, "РПЗ");
        //System.out.println(qaw.toString());*/
    }

    public void setActionBarTitle(String title){
        TextView textCont = (TextView)findViewById(R.id.toolbar_title);
        textCont.setText(title);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if(fragment!=null){
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch(menuItem.getItemId()){
            case R.id.nav_schedule:
                String specTag1 = getResources().getString(R.string.scheduleTag);
                Fragment oldFragment1 = manager.findFragmentByTag(specTag1);
                if (oldFragment1 != null) {
                    transaction.replace(R.id.nav_host_fragment,oldFragment1);
                    ((ScheduleFragment)oldFragment1).setActionBarTitle();// transaction.addToBackStack(null);
                } else {
                    transaction.add(R.id.nav_host_fragment, new ScheduleFragment());
                }
                break;
            case R.id.nav_templates:
                String specTag2 = getResources().getString(R.string.templatesTag);
                Fragment oldFragment2 = manager.findFragmentByTag(specTag2);
                if (oldFragment2 != null) {
                    transaction.replace(R.id.nav_host_fragment, oldFragment2);
                    ((TemplatesFragment) oldFragment2).setActionBarTitle();
                } else {
                    transaction.add(R.id.nav_host_fragment, new TemplatesFragment());
                }
                break;
            case R.id.nav_groups:
                String specTag3 = getResources().getString(R.string.groupsTag);
                Fragment oldFragment3 = manager.findFragmentByTag(specTag3);
                if (oldFragment3 != null) {
                    transaction.replace(R.id.nav_host_fragment, oldFragment3);
                    ((GroupsFragment) oldFragment3).setActionBarTitle();
                } else {
                    transaction.add(R.id.nav_host_fragment, new GroupsFragment());
                }
                break;
            case R.id.nav_subjects:
                String specTag4 = getResources().getString(R.string.subjectsTag);
                Fragment oldFragment4 = manager.findFragmentByTag(specTag4);
                if (oldFragment4 != null) {
                    transaction.replace(R.id.nav_host_fragment, oldFragment4);
                    ((SubjectsFragment) oldFragment4).setActionBarTitle();

                } else {
                    transaction.add(R.id.nav_host_fragment, new SubjectsFragment());
                }
                break;
            case R.id.nav_settings:
                String specTag5 = getResources().getString(R.string.settingsTag);
                Fragment oldFragment5 = manager.findFragmentByTag(specTag5);
                if (oldFragment5 != null) {
                    transaction.replace(R.id.nav_host_fragment, oldFragment5);
                    ((SettingsFragment) oldFragment5).setActionBarTitle();
                } else {
                    transaction.add(R.id.nav_host_fragment, new SettingsFragment());
                }
                break;
        }
        transaction.commit();
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        fuckingMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_datePicker:
                prepareActionDatePicker();
                return true;
            case R.id.action_importTemplates:
                prepareActionImportTemplates();
                return true;
            case R.id.action_checkRead:
                isSelectMode=true;
                toolbar.setVisibility(View.GONE);
                toolbarCompleteClasses.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_checkCanceled:
                isSelectMode=false;
                toolbar.setVisibility(View.GONE);
                toolbarCanceledClasses.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_reportPeriod:
                prepareReporDatePicker();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void prepareReporDatePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.popup_choose_date_report);
        builder.setPositiveButton(R.string.popup_gen_report, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickAcceptReportDates(dialog, which);
        }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.cancel();
            }
        });
        genReport = getLayoutInflater().inflate(R.layout.dialog_choose_report_period, null);
        builder.setView(genReport);
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

    private void onClickAcceptReportDates(DialogInterface dialog, int which) {
        final DatePicker pickerFrom = genReport.findViewById(R.id.dateFromPicker);
        final DatePicker pickerTo = genReport.findViewById(R.id.dateToPicker);
        int fromYear = pickerFrom.getYear();
        int fromMonth = pickerFrom.getMonth();
        int fromDay = pickerFrom.getDayOfMonth();

        int toYear = pickerTo.getYear();
        int toMonth = pickerTo.getMonth();
        int toDay = pickerTo.getDayOfMonth();
        boolean isDatesCorrect = true;
        if(fromYear>toYear){
            isDatesCorrect = false;
        } else if(fromYear<=toYear && fromMonth>toMonth) {
            isDatesCorrect = false;
        } else if(fromYear<=toYear && fromMonth<=toMonth && fromDay>toDay){
            isDatesCorrect = false;
        }
        if(isDatesCorrect){
            Intent intent = new Intent(this, GeneratedReportActivity.class);
            startActivity(intent);
        } else {
            prepareReporDatePicker();
            Toast.makeText(getApplicationContext(),R.string.popup_dates_wrong,Toast.LENGTH_LONG).show();
        }

    }

    private void prepareActionImportTemplates() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_importTemplates);
        builder.setPositiveButton(R.string.popup_import,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickAcceptImportTemplates(dialog,which);
            }
        });
        builder.setNegativeButton(R.string.popup_cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickCancelmportTemplates(dialog,which);
            }
        });
        importTemplates = getLayoutInflater().inflate(R.layout.dialog_import_templates,null);
        final Spinner spinnerSemester = importTemplates.findViewById(R.id.periodSemestrSpinner);
        final TextView periodFromLabel = importTemplates.findViewById(R.id.periodFromLabel);
        final TextView periodToLabel = importTemplates.findViewById(R.id.periodToLabel);
        final DatePicker pickerFrom = importTemplates.findViewById(R.id.dateFromPicker);
        final DatePicker pickerTo = importTemplates.findViewById(R.id.dateToPicker);
        Spinner spinnerRange = importTemplates.findViewById(R.id.periodSpinner);
        spinnerRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        spinnerSemester.setVisibility(View.VISIBLE);
                        periodFromLabel.setVisibility(View.GONE);
                        periodToLabel.setVisibility(View.GONE);
                        pickerFrom.setVisibility(View.GONE);
                        pickerTo.setVisibility(View.GONE);
                        break;
                    case 1:
                        spinnerSemester.setVisibility(View.GONE);
                        periodFromLabel.setVisibility(View.VISIBLE);
                        periodToLabel.setVisibility(View.VISIBLE);
                        pickerFrom.setVisibility(View.VISIBLE);
                        pickerTo.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerRange.setSelection(0);
        builder.setView(importTemplates);
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

    private void onClickCancelmportTemplates(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    private void onClickAcceptImportTemplates(DialogInterface dialog, int which) {
    }

    private void prepareActionDatePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_datePicker);
        builder.setPositiveButton(R.string.popup_Follow,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickAcceptDatePicker(dialog,which);
            }
        });
        builder.setNegativeButton(R.string.popup_cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickCancelDatePicker(dialog,which);
            }
        });
        actionDatePicker = getLayoutInflater().inflate(R.layout.dialog_choose_date, null);
        builder.setView(actionDatePicker);
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

    private void onClickCancelDatePicker(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    private void onClickAcceptDatePicker(DialogInterface dialog, int which) {
        DatePicker datePicker = actionDatePicker.findViewById(R.id.datePicker);
        FragmentManager manager = getSupportFragmentManager();
        Fragment oldFragment1 = manager.findFragmentByTag(getResources().getString(R.string.scheduleTag));
        if (oldFragment1 != null) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            final DateTime startDate = formatter.parseDateTime("01/01/1990 00:00:00");
            Log.d("MainActivity",datePicker.getYear()+""+datePicker.getMonth()+""+datePicker.getDayOfMonth());
            DateTime selectedDate = new DateTime(datePicker.getYear(),datePicker.getMonth()+1,datePicker.getDayOfMonth(),0,0);
            ((ScheduleFragment)oldFragment1).setCurrentWeek(Weeks.weeksBetween(startDate.dayOfWeek().withMinimumValue().minusDays(1), selectedDate.dayOfWeek().withMaximumValue().plusDays(1)).getWeeks() - 1);
            StringBuilder titleSB = new StringBuilder();
            titleSB.append(monthNumberToString(selectedDate.getMonthOfYear())).append(", ").append(selectedDate.getYear());
            setActionBarTitle(titleSB.toString());
        }
        dialog.dismiss();
    }

    private String monthNumberToString(int monthNumber) {
        String monthStr;
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
    public void showOverflowMenu(boolean showMenu){
        if(fuckingMenu == null)
            return;
        fuckingMenu.setGroupVisible(R.id.main_menu_group, showMenu);
    }

    public Toolbar getToolbar1() {
        return toolbar1;
    }

    public void setToolbar1(Toolbar toolbar1) {
        this.toolbar1 = toolbar1;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public boolean isSelectMode() {
        return isSelectMode;
    }

    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
    }

    public Toolbar getToolbarCompleteClasses() {
        return toolbarCompleteClasses;
    }

    public void setToolbarCompleteClasses(Toolbar toolbarCompleteClasses) {
        this.toolbarCompleteClasses = toolbarCompleteClasses;
    }

    public Toolbar getToolbarCanceledClasses() {
        return toolbarCanceledClasses;
    }

    public void setToolbarCanceledClasses(Toolbar toolbarCanceledClasses) {
        this.toolbarCanceledClasses = toolbarCanceledClasses;
    }

}
