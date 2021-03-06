package com.hpcc.kursovaya;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
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
import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateScheduleWeek;
import com.hpcc.kursovaya.dao.query.DBManager;
import com.hpcc.kursovaya.ui.groups.GroupsFragment;
import com.hpcc.kursovaya.ui.hourChecker.activity.GroupCheckActivity;
import com.hpcc.kursovaya.ui.schedule.DayScheduleFragment;
import com.hpcc.kursovaya.ui.schedule.MonthScheduleFragment;
import com.hpcc.kursovaya.ui.schedule.ScheduleFragment;
import com.hpcc.kursovaya.ui.settings.SettingsFragment;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;
import com.hpcc.kursovaya.ui.specialities.SpecialitiesFragment;
import com.hpcc.kursovaya.ui.subjects.SubjectsFragment;
import com.hpcc.kursovaya.ui.templates.TemplatesFragment;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String DAY_TAG = "DAYVIEW";
    private final Context currentContext = this;
    public static final String WEEKVIEW_TAG="WEEKVIEW";
    public static final String MONTH_TAG = "MONTHVIEW";
    private static final int REQUEST_CODE = 26;
    public static final int WEEKVIEW = 1;
    public static final int MONTH = 2;
    public static final int DAY = 3;
    private int currentViewSelected = WEEKVIEW;

    private Menu fuckingMenu;
    private final String TAG = MainActivity.class.getSimpleName();
    private AppBarConfiguration mAppBarConfiguration;
    private boolean isSelectMode = false;
    private boolean isScheduleSelected = true;
    private DrawerLayout drawer;
    private Toolbar toolbar1;
    private Toolbar toolbarCompleteClasses;
    private Toolbar toolbarCanceledClasses;
    private Toolbar toolbarSearch;
    private Toolbar toolbar;
    private View actionDatePicker;
    private View importTemplates;
    private View genReport;
    private long mLastClickTime = 0;
    private Locale savedLocale = null;
    private boolean isOverflowShown = true;
    private TemplateScheduleWeek upperTemplate;
    private TemplateScheduleWeek bottomTemplate;
    private ImageButton backToCurrentDay;
    public ImageButton openGroupCheckActivity;
    private ImageButton searchButton;
    private TextView currentDayText;
    private int weeksFromCurrent;

    private DateTime dateFrom = DateTime.now().withMillisOfDay(0);
    private DateTime dateTo = DateTime.now().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999);
    private boolean isImportSuccesfull;

    public void setToolbarSearch(Toolbar toolbarSearch) {
        this.toolbarSearch = toolbarSearch;
    }


    public int getWeeksFromCurrent() {
        return weeksFromCurrent;
    }

    public void setWeeksFromCurrent(int weeksFromCurrent) {
        this.weeksFromCurrent = weeksFromCurrent;
    }

    public boolean isLanguageChanged() {
        return isLanguageChanged;
    }

    public void setLanguageChanged(boolean languageChanged) {
        isLanguageChanged = languageChanged;
    }


    private boolean isLanguageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        savedLocale = LocaleManager.getLocale(getResources());
        final Activity activity = this;
        Thread t = new Thread(){
            public void run(){
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                deserializeAlarms();
            }
        };
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        TextView textCont = findViewById(R.id.toolbar_title);
        textCont.setOnClickListener(v -> {
            if(isScheduleSelected){
                prepareModeSelection();
                //prepareActionDatePicker();
            }
        });
        backToCurrentDay = toolbar.findViewById(R.id.toCurrentDay);
        openGroupCheckActivity = toolbar.findViewById(R.id.group_check_button);
        currentDayText = toolbar.findViewById(R.id.currentDayText);
        currentDayText.setText(Integer.toString(DateTime.now().getDayOfMonth()));
        toolbar1 = findViewById(R.id.toolbarEdit);
        toolbarCompleteClasses = findViewById(R.id.toolbarComplete);
        toolbarCanceledClasses = findViewById(R.id.toolbarCancel);
        toolbarSearch = findViewById(R.id.toolbarSearch);
        toolbarCanceledClasses.setVisibility(View.GONE);
        toolbarCompleteClasses.setVisibility(View.GONE);
        searchButton = findViewById(R.id.search_button);
        toolbarSearch.setVisibility(View.GONE);
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
            Log.d(TAG,"Чо теперь сейвд инстанс стейт?");
            getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment,
                    new ScheduleFragment(), WEEKVIEW_TAG).commit();
            navigationView.setCheckedItem(R.id.nav_schedule);

        } else {
            Log.d(TAG,"открывай сука");
            isLanguageChanged = true;
            backToCurrentDay.setVisibility(View.GONE);
            currentDayText.setVisibility(View.GONE);
        }
        ImageButton searchButton = toolbar.findViewById(R.id.search_button);
        ImageButton turnOffSearch = toolbarSearch.findViewById(R.id.turnOff_search);ImageButton clearButton = toolbarSearch.findViewById(R.id.clear_search);
        EditText textSearch = toolbarSearch.findViewById(R.id.textView_search);
        clearButton.setOnClickListener(v -> {
            textSearch.setText("");
        });
        turnOffSearch.setOnClickListener(v ->{
            toolbar.setVisibility(View.VISIBLE);
            toolbarSearch.setVisibility(View.GONE);
        });
        searchButton.setOnClickListener(v -> {
            toolbar.setVisibility(View.GONE);
            toolbarSearch.setVisibility(View.VISIBLE);
        });

        openGroupCheckActivity.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), GroupCheckActivity.class);
            startActivity(intent);
        });
    }


    public int getCurrentViewSelected() {
        return currentViewSelected;
    }

    public void setCurrentViewSelected(int currentViewSelected) {
        this.currentViewSelected = currentViewSelected;
    }

    private void prepareModeSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.schedule_mode_dialog_title);
        View modeChooserView = getLayoutInflater().inflate(R.layout.dialog_switch_view,null);
        RadioGroup rg = modeChooserView.findViewById(R.id.rg);
        if(currentViewSelected==WEEKVIEW) {
            rg.check(R.id.week);
        } else {
            rg.check(R.id.month);
        }
        builder.setPositiveButton(R.string.accept, (dialog, which) -> {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_top,R.anim.slide_out_bottom);
            Fragment fragment = null;
            DateTime startDate = DateTime.parse(ConstantApplication.MIN_DATE_TIME, DateTimeFormat.forPattern(ConstantApplication.PATTERN_DATE_TIME));
            DateTime firstDate = DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            String tag = "";
            switch(rg.getCheckedRadioButtonId()){
                case R.id.week:
                    currentViewSelected = WEEKVIEW;
                    tag = WEEKVIEW_TAG;
                    int weekFromCurrent = Weeks.weeksBetween(startDate.dayOfWeek().withMinimumValue().minusDays(1), firstDate.dayOfWeek().withMaximumValue().plusDays(1)).getWeeks();
                    fragment = new ScheduleFragment();
                    break;
                case R.id.month:
                    currentViewSelected = MONTH;
                    tag = MONTH_TAG;
                    int monthFromCurrent = Months.monthsBetween(startDate,firstDate.withDayOfMonth(1)).getMonths();
                    fragment = new MonthScheduleFragment(monthFromCurrent);

            }
            invalidateOptionsMenu();
            transaction.replace(R.id.nav_host_fragment,fragment,tag);
            transaction.commit();
        });
        builder.setNegativeButton(R.string.cancel, ((dialog, which) -> dialog.cancel()));
        builder.setView(modeChooserView);
        builder.create().show();

    }

    //This method creates folder in external storage for backups
    private void createBackupFolder() {
        String externalStorage = "TeachersDiaryBackups";
        Log.d(TAG,externalStorage);
        File file = new File(Environment.getExternalStorageDirectory(), externalStorage);
        if(!file.exists()){
            boolean isCreated = file.mkdirs();
            if(isCreated){
                Log.i(TAG,"Successfully created folder TeachersDiaryBackups");
            } else {
                Log.i(TAG,"Failed to create a folder: TeachersDiaryBackups");
            }
        }
    }

    public boolean isScheduleSelected() {
        return isScheduleSelected;
    }

    public void setScheduleSelected(boolean scheduleSelected) {
        isScheduleSelected = scheduleSelected;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            createBackupFolder();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    private void deserializeAlarms() {
        String existstoragedir = getExternalFilesDir(null).getAbsolutePath() + "/alarms.dat";
        File file = new File(existstoragedir);
        FileInputStream fis = null;
        ObjectInputStream iis = null;
        int timeArrayRecover[][][] = ConstantApplication.timeArray.clone();
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            fis = new FileInputStream(file.getAbsolutePath());
            iis = new ObjectInputStream(fis);
            ConstantApplication.timeArray = (int[][][]) iis.readObject();

        } catch (IOException | ClassNotFoundException e) {
            Log.d(TAG,e.toString());
            ConstantApplication.timeArray = timeArrayRecover;
        }
    }

    public void setActionBarTitle(String title){
        TextView textCont = (TextView)findViewById(R.id.toolbar_title);
        textCont.setText(title);
    }


    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onResume"+LocaleManager.getLanguage(this));
        Log.d(TAG,"onResume"+savedLocale.toString());
        String lm = LocaleManager.getLanguage(this);
        if(savedLocale!=null && lm!=null &&!savedLocale.toString().toLowerCase().equals(lm.toLowerCase())){
            Log.d(TAG, "onResume if savedLocale doesnt equals");
            savedLocale = LocaleManager.getLocale(getResources());
            recreate();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch(menuItem.getItemId()){
            case R.id.nav_schedule:
                DateTime startDate = DateTime.parse(ConstantApplication.MIN_DATE_TIME, DateTimeFormat.forPattern(ConstantApplication.PATTERN_DATE_TIME));
                DateTime firstDate = DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
                Fragment oldWeek = manager.findFragmentByTag(WEEKVIEW_TAG);
                Fragment oldMonth = manager.findFragmentByTag(MONTH_TAG);
                Fragment oldDay = manager.findFragmentByTag(DAY_TAG);
                if (!isLanguageChanged()) {
                    if(currentViewSelected == WEEKVIEW && oldWeek!=null){
                        transaction.replace(R.id.nav_host_fragment,oldWeek,WEEKVIEW_TAG);
                        ((ScheduleFragment)oldWeek).refreshGrid(weeksFromCurrent);
                        ((ScheduleFragment)oldWeek).setActionBarTitle();
                        ((ScheduleFragment)oldWeek).setCoupleHeaders();
                    } else if(currentViewSelected==MONTH && oldMonth!=null){
                        transaction.replace(R.id.nav_host_fragment,oldMonth,MONTH_TAG);
                        ((MonthScheduleFragment)oldMonth).setActionBarTitle();
                    } else if (currentViewSelected==DAY && oldDay!=null) {
                        transaction.replace(R.id.nav_host_fragment,oldDay,DAY_TAG);
                        ((DayScheduleFragment)oldDay).setActionBarTitle();
                    }
                } else {
                    if(currentViewSelected==WEEKVIEW) {
                        transaction.add(R.id.nav_host_fragment, new ScheduleFragment(),WEEKVIEW_TAG);
                    } else if(currentViewSelected==MONTH){
                        int monthFromCurrent = Months.monthsBetween(startDate,firstDate.withDayOfMonth(1)).getMonths();
                        transaction.add(R.id.nav_host_fragment,new MonthScheduleFragment(monthFromCurrent),MONTH_TAG);
                    } else {
                        int daysFromCurrent = Days.daysBetween(startDate,firstDate).getDays();
                        transaction.add(R.id.nav_host_fragment,new DayScheduleFragment(daysFromCurrent),DAY_TAG);
                    }

                    isLanguageChanged = false;
                }
                isScheduleSelected = true;
                searchButton.setVisibility(View.GONE);
                openGroupCheckActivity.setVisibility(View.VISIBLE);
                toolbarSearch.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                showOverflowMenu(true);
                break;
            case R.id.nav_templates:
                transaction.add(R.id.nav_host_fragment, new TemplatesFragment());
                isScheduleSelected = false;
                toolbar.setVisibility(View.VISIBLE);
                toolbarSearch.setVisibility(View.GONE);
                openGroupCheckActivity.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);
                showOverflowMenu(false);
                break;
            case R.id.nav_groups:
                backToCurrentDay.setVisibility(View.GONE);
                currentDayText.setVisibility(View.GONE);
                transaction.add(R.id.nav_host_fragment, new GroupsFragment());
                isScheduleSelected = false;
                toolbar.setVisibility(View.VISIBLE);
                toolbarSearch.setVisibility(View.GONE);
                openGroupCheckActivity.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_subjects:
                transaction.add(R.id.nav_host_fragment, new SubjectsFragment());
                isScheduleSelected = false;
                toolbar.setVisibility(View.VISIBLE);
                toolbarSearch.setVisibility(View.GONE);
                openGroupCheckActivity.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_specialities:
                transaction.add(R.id.nav_host_fragment, new SpecialitiesFragment());
                isScheduleSelected = false;
                toolbar.setVisibility(View.VISIBLE);
                toolbarSearch.setVisibility(View.GONE);
                openGroupCheckActivity.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);
                showOverflowMenu(false);
                break;
            case R.id.nav_settings:
                String specTag5 = getResources().getString(R.string.settingsTag);
                Fragment oldFragment5 = manager.findFragmentByTag(specTag5);
                backToCurrentDay.setVisibility(View.GONE);
                currentDayText.setVisibility(View.GONE);
                if (oldFragment5 != null) {
                    transaction.replace(R.id.nav_host_fragment, oldFragment5);
                    ((SettingsFragment) oldFragment5).setActionBarTitle();
                } else {
                    transaction.add(R.id.nav_host_fragment, new SettingsFragment());
                }
                isScheduleSelected = false;
                toolbar.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.GONE);
                openGroupCheckActivity.setVisibility(View.GONE);
                toolbarSearch.setVisibility(View.GONE);
                showOverflowMenu(false);
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
        showOverflowMenu(isOverflowShown);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(currentViewSelected==MONTH){
            menu.findItem(R.id.action_deleteClasses).setEnabled(false);
            menu.findItem(R.id.action_checkRead).setEnabled(false);
            menu.findItem(R.id.action_checkCanceled).setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            /*case R.id.action_deleteClasses:
                setSelectMode(true);
                toolbar.setVisibility(View.GONE);
                toolbar1.setVisibility(View.VISIBLE);
                //prepareActionDatePicker();
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
                isSelectMode=true;
                toolbar.setVisibility(View.GONE);
                toolbarCanceledClasses.setVisibility(View.VISIBLE);
                return true;
            case R.id.action_reportPeriod:
                if(DBManager.readAll(Speciality.class).size()!=0) {
                    prepareReporDatePicker();
                } else {
                    Toast.makeText(currentContext, R.string.toast_fragment_no_specialities, Toast.LENGTH_LONG).show();
                }*/
            default:
                return false;
        }
    }

    public void prepareReporDatePicker() {
        List<Speciality> specialities = new ArrayList<>();
        specialities.add(new Speciality());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.popup_choose_date_report);

        genReport = getLayoutInflater().inflate(R.layout.dialog_choose_report_period, null);
        builder.setView(genReport);

        dateFrom = DateTime.now();
        dateTo = DateTime.now();
        pickerFrom = genReport.findViewById(R.id.dateFromPicker);
        pickerFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(currentContext, dFromListener, dateFrom.getYear(),dateFrom.getMonthOfYear()-1,dateFrom.getDayOfMonth()).show();
            }
        });
        pickerTo = genReport.findViewById(R.id.dateToPicker);
        pickerTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(currentContext, dToListener, dateTo.getYear(),dateTo.getMonthOfYear()-1,dateTo.getDayOfMonth()).show();
            }
        });
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy",LocaleManager.getLocale(getResources()));
        pickerFrom.setText(simpleDateFormat.format(dateFrom.toDate()));
        pickerTo.setText(simpleDateFormat.format(dateTo.toDate()));
        Spinner spinnerSpeciality =
                ConstantApplication.fillingSpinner(currentContext, genReport.findViewById(R.id.specialitySpinner),
                        new Speciality().entityToNameList());
        Spinner spinnerCourse = genReport.findViewById(R.id.courseSpinner);
        listenerSpinnerSpeciality(spinnerSpeciality,spinnerCourse,specialities);

        builder.setPositiveButton(R.string.popup_gen_report, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showAcceptReportDatesProgressBar(dialog,which,specialities.get(0));
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.popup_cancel, new DialogInterface.OnClickListener() {
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

    private void showAcceptReportDatesProgressBar(DialogInterface dialog, int which, Speciality speciality) {
        final ProgressDialog progressBarDialog= new ProgressDialog(this);
        progressBarDialog.setTitle(getString(R.string.gen_report_progress));
        progressBarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressBarDialog.setCancelable(false);
        progressBarDialog.setProgress(0);
        Thread t = new Thread(() -> {
            onClickAcceptReportDates(progressBarDialog, which,speciality);
            progressBarDialog.dismiss();
        });
        DateTime from = dateFrom;
        DateTime to = dateTo;

        boolean isDatesCorrect = true;
        if(dateFrom.getYear()>=1990 && (dateFrom.getMonthOfYear())>=1 && dateFrom.getDayOfMonth()>=1
                && dateTo.getYear()>=1990 && dateTo.getMonthOfYear()>=1 && dateTo.getDayOfMonth()>=1
                && !from.isBefore(to)) {
            isDatesCorrect = false;
        }
            if (isDatesCorrect) {
                progressBarDialog.show();
                t.start();
            } else {
                dialog.dismiss();
                prepareReporDatePicker();
                Toast.makeText(getApplicationContext(), R.string.popup_dates_wrong, Toast.LENGTH_LONG).show();
            }
    }

    private void listenerSpinnerSpeciality(Spinner spinner, Spinner spinnerCourse, List<Speciality> specialities) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                Speciality speciality = DBManager.copyObjectFromRealm(DBManager.read(Speciality.class, ConstantApplication.NAME, item));
                specialities.set(0,speciality);
                List<String> courses = new ArrayList<>();
                for(int i=1; i<5;i++){
                    courses.add(Integer.toString(i));
                }
                courses.add(getResources().getString(R.string.all_courses));
                ConstantApplication.fillingSpinner(currentContext,spinnerCourse, courses);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onClickAcceptReportDates(ProgressDialog dialog, int which,Speciality speciality) {
        /*final DatePicker pickerFrom = genReport.findViewById(R.id.dateFromPicker);
        final DatePicker pickerTo = genReport.findViewById(R.id.dateToPicker);
        int fromYear = pickerFrom.getYear();
        int fromMonth = pickerFrom.getMonth();
        int fromDay = pickerFrom.getDayOfMonth();*/
        SimpleDateFormat fileNameFormat = new SimpleDateFormat("ddMMyyyyHHmmss",LocaleManager.getLocale(getResources()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy",LocaleManager.getLocale(getResources()));
        DateTime from = dateFrom;
        final Spinner courseSpinner = genReport.findViewById(R.id.courseSpinner);

        /*int toYear = pickerTo.getYear();
        int toMonth = pickerTo.getMonth();
        int toDay = pickerTo.getDayOfMonth();*/
        DateTime to = dateTo;



            dialog.setProgress(10);
            StringBuilder reportName = new StringBuilder("/report")
                    .append(fileNameFormat.format(DateTime.now().toDate()))
                    .append(".pdf");

            String existstoragedir = getExternalFilesDir(null).getAbsolutePath() + reportName.toString();
            File file = new File(existstoragedir);


            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dialog.setProgress(20);
            String FONT = "/assets/fonts/arial.ttf";
            BaseFont bf = null;
            try {
                bf = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Font fontSmall = new Font(bf, 10, Font.NORMAL);
            Document document = new Document();
            PdfWriter writer = null;
            try {
                writer = PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            document.open();
            document.setPageSize(PageSize.A4);
            dialog.setProgress(30);
            if(courseSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.all_courses))){
                for(int i=1; i<5;i++){
                    genDocument(document,i,fontSmall,speciality);
                    document.newPage();
                    dialog.setProgress(30+(i*70/5));
                }
            } else {
                genDocument(document,Integer.parseInt(courseSpinner.getSelectedItem().toString()),fontSmall,speciality);
            }
            dialog.setProgress(100);
            document.close();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(MainActivity.this,BuildConfig.APPLICATION_ID+".provider",file),"application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent intentOpen = Intent.createChooser(intent,getString(R.string.open_pdf));
            try {
                startActivity(intentOpen);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, e.toString());
            }
    }

    private void genDocument(Document document, int courseNumber, Font fontSmall, Speciality speciality){
        List<AcademicHour> academicHourList = new ArrayList<>();
        List<Group> groupList = new ArrayList<>();
        List<Subject> subjectList = new ArrayList<>();
        for(AcademicHour academicHour : AcademicHour.academicHourListFromPeriod(dateFrom.toDate(),dateTo.plusDays(1).toDate())){
            try{
                TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                if(templateAcademicHour!=null){
                    Group group = templateAcademicHour.getGroup();
                    Subject subject = templateAcademicHour.getSubject();
                    if(group!=null && subject!=null){
                        Speciality specialityFromHour = group.getSpecialty();
                        if(specialityFromHour!=null && speciality.equals(specialityFromHour) && group.getNumberCourse()==courseNumber){
                            groupList.add(group);
                            subjectList.add(subject);
                            academicHourList.add(academicHour);
                        }
                    }
                }
            } catch (Exception ex){
                Log.e(TAG, "reportGen:"+ex.toString());
            }

        }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", LocaleManager.getLocale(getResources()));
            Paragraph paragraphCourse = new Paragraph();
            paragraphCourse.setSpacingAfter(32);

            Chunk course = new Chunk(getString(R.string.courseLabelPDF) + " " + courseNumber, fontSmall);
            paragraphCourse.add(course);
            paragraphCourse.setAlignment(Element.ALIGN_CENTER);

            Paragraph paragraphSpeciality = new Paragraph();
            paragraphSpeciality.setSpacingAfter(32);

            Chunk specialityLabel = new Chunk(getString(R.string.specialityLabelPDF) + " " + speciality.getName(), fontSmall);
            paragraphSpeciality.add(specialityLabel);
            paragraphSpeciality.setAlignment(Element.ALIGN_CENTER);
            try {
                document.add(paragraphCourse);
                document.add(paragraphSpeciality);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            DateTime currentDate = DateTime.now();

            Paragraph paragraphDates = new Paragraph();
            paragraphDates.setIndentationLeft(100f);
            paragraphDates.setIndentationRight(100f);
            paragraphDates.setSpacingAfter(32);
            Chunk glue = new Chunk(new VerticalPositionMark());
            Phrase dates = new Phrase("", fontSmall);

            dates.add(getString(R.string.creationDatePDF) + " " + simpleDateFormat.format(currentDate.toDate()));
            dates.add(glue);
            dates.add(getString(R.string.periodLabelPDF) + " " + simpleDateFormat.format(dateFrom.toDate()) + " - " + simpleDateFormat.format(dateTo.toDate()));
            paragraphDates.add(dates);
            try {
                document.add(paragraphDates);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            if(academicHourList.size()!=0) {
                groupList = new ArrayList<>(new LinkedHashSet<>(groupList));
                subjectList = new ArrayList<>(new LinkedHashSet<>(subjectList));

                Map<Group, List<AcademicHour>> academicHoursOfGroup = new HashMap<>();
                for (Group group : groupList) {
                    List<AcademicHour> academicHours = new ArrayList<>();
                    for (AcademicHour academicHour : academicHourList) {
                        TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                        if (templateAcademicHour.getGroup() != null && group.equals(templateAcademicHour.getGroup())) {
                            academicHours.add(academicHour);
                        }
                    }
                    academicHoursOfGroup.put(group, academicHours);
                }


                PdfPTable table = new PdfPTable(groupList.size() + 1);
                float[] relWidths = new float[groupList.size() + 1];
                relWidths[0] = 12f;
                for (int i = 1; i < groupList.size() + 1; i++) {
                    relWidths[i] = 2f;
                }
                try {
                    table.setWidths(relWidths);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }


                PdfPCell cellDiscipline = new PdfPCell(new Phrase(getResources().getString(R.string.tableDisciplineHeader), fontSmall));
                cellDiscipline.setRowspan(2);
                cellDiscipline.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellDiscipline.setVerticalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellDiscipline);
                PdfPCell cellGroupHeader = new PdfPCell(new Phrase(getResources().getString(R.string.tableGroupHeader), fontSmall));
                cellGroupHeader.setColspan(groupList.size());
                cellGroupHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellGroupHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cellGroupHeader);

                for (int i = 0; i < groupList.size(); i++) {
                    PdfPCell cellGroupName = new PdfPCell(new Phrase(groupList.get(i).getName(), fontSmall));
                    cellGroupName.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellGroupName.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(cellGroupName);
                }
                for (int i = 0; i < subjectList.size(); i++) {
                    PdfPTable innerTable = new PdfPTable(2);
                    PdfPCell subjectCell = new PdfPCell(new Phrase(subjectList.get(i).getName(), fontSmall));
                    subjectCell.setRowspan(4);
                    subjectCell.setBorder(Rectangle.NO_BORDER);
                    innerTable.addCell(subjectCell);
                    innerTable.addCell(new Phrase(getResources().getString(R.string.tablePlanHeader), fontSmall));
                    innerTable.addCell(new Phrase(getResources().getString(R.string.tableFactHeader), fontSmall));
                    innerTable.addCell(new Phrase(getResources().getString(R.string.tableCanceledHeader), fontSmall));
                    innerTable.addCell(new Phrase(getResources().getString(R.string.tableRestHeader), fontSmall));
                    PdfPCell inOutPadding = new PdfPCell(innerTable);
                    inOutPadding.setPadding(0);
                    table.addCell(inOutPadding);
                    for (int j = 0; j < groupList.size(); j++) {
                        PdfPTable hoursTable = new PdfPTable(1);
                        int planHours = groupList.get(j).getPlanHours(subjectList.get(i), academicHoursOfGroup.get(groupList.get(j)));
                        int factHours = groupList.get(j).getReadHours(subjectList.get(i), academicHoursOfGroup.get(groupList.get(j)));
                        int cancelledHours = groupList.get(j).getCanceledHours(subjectList.get(i), academicHoursOfGroup.get(groupList.get(j)));
                        hoursTable.addCell(new Phrase(Integer.toString(planHours)/*plan hours*/, fontSmall));
                        hoursTable.addCell(new Phrase(Integer.toString(factHours)/*fact hours*/, fontSmall));
                        hoursTable.addCell(new Phrase(Integer.toString(cancelledHours)/*cancelled hours*/, fontSmall));
                        hoursTable.addCell(new Phrase(Integer.toString(planHours - factHours - cancelledHours)/*Rest hours hours*/, fontSmall));
                        PdfPCell temp = new PdfPCell((hoursTable));
                        temp.setPadding(0);
                        table.addCell(temp);
                    }
                }

                try {
                    document.add(table);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            } else {
                Paragraph thereIsNoGroups = new Paragraph();
                Chunk noSuitableGroups = new Chunk(getString(R.string.there_is_no_groups), fontSmall);
                thereIsNoGroups.add(noSuitableGroups);
                thereIsNoGroups.setAlignment(Element.ALIGN_CENTER);
                try {
                    document.add(thereIsNoGroups);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
    }


    Button pickerFrom = null;
    Button pickerTo = null;
    public void prepareActionImportTemplates() {
        dateFrom = DateTime.now();
        dateTo = DateTime.now();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_importTemplates);
        AtomicBoolean isUpperFirst = new AtomicBoolean(true);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.popup_import,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                //onClickAcceptImportTemplates(dialog,which,isUpperFirst.get());
            }
        });
        builder.setNegativeButton(R.string.popup_cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();                onClickCancelmportTemplates(dialog,which);
            }
        });
        importTemplates = getLayoutInflater().inflate(R.layout.dialog_import_templates,null);
        RadioGroup rg = importTemplates.findViewById(R.id.popup_firstWeek_rgroup);
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.popup_firstWeek_rgroup_upper:
                    isUpperFirst.set(true);
                    break;
                case R.id.popup_firstWeek_rgroup_bottom:
                    isUpperFirst.set(false);
                    break;
            }
        });
        Spinner upperTemplateSpinner = importTemplates.findViewById(R.id.upperTemplateSpinner);
        upperTemplate = new TemplateScheduleWeek();
        Spinner bottomTemplateSpinner = importTemplates.findViewById(R.id.bottomTemplateSpinner);
        bottomTemplate = new TemplateScheduleWeek();
        List<TemplateScheduleWeek> templates = DBManager.copyObjectFromRealm(
                DBManager.readAll(TemplateScheduleWeek.class, ConstantApplication.NAME));
        List<String> stringList = TemplateScheduleWeek.entityToNameList(templates);
        ConstantApplication.fillingSpinner(this,upperTemplateSpinner,stringList);
        ConstantApplication.fillingSpinner(this,bottomTemplateSpinner,stringList);
        upperTemplateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                upperSpinnerOnItemSelected(parent,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bottomTemplateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bottomSpinnerOnItemSelected(parent, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final TextView periodFromLabel = importTemplates.findViewById(R.id.periodFromLabel);
        final TextView periodToLabel = importTemplates.findViewById(R.id.periodToLabel);
        pickerFrom = importTemplates.findViewById(R.id.dateFromPicker);
        pickerFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(currentContext,dFromListener, dateFrom.getYear(),dateFrom.getMonthOfYear()-1,dateFrom.getDayOfMonth()).show();
            }
        });
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy",LocaleManager.getLocale(getResources()));
        pickerTo = importTemplates.findViewById(R.id.dateToPicker);
        pickerTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(currentContext,dToListener, dateTo.getYear(),dateTo.getMonthOfYear()-1,dateTo.getDayOfMonth()).show();
            }
        });
        pickerFrom.setText(simpleDateFormat.format(dateFrom.toDate()));
        pickerTo.setText(simpleDateFormat.format(dateTo.toDate()));
        builder.setView(importTemplates);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.sideBar));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.sideBar));
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        showImportTemplatesProgressBar(dialog,isUpperFirst.get());
                        //Dismiss once everything is OK
                    }
                });
            }
        });
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    DatePickerDialog.OnDateSetListener dFromListener=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateFrom = dateFrom.withYear(year).withMonthOfYear(monthOfYear+1).withDayOfMonth(dayOfMonth);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy",LocaleManager.getLocale(getResources()));
            pickerFrom.setText(simpleDateFormat.format(dateFrom.toDate()));
        }
    };

    DatePickerDialog.OnDateSetListener dToListener=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTo = dateTo.withYear(year).withMonthOfYear(monthOfYear+1).withDayOfMonth(dayOfMonth);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy",LocaleManager.getLocale(getResources()));
            pickerTo.setText(simpleDateFormat.format(dateTo.toDate()));
        }
    };

    private void onClickCancelmportTemplates(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    private void showImportTemplatesProgressBar(DialogInterface dialog, boolean isUpperTemplate){
        final ProgressDialog progressBarDialog= new ProgressDialog(this);
        progressBarDialog.setTitle(getString(R.string.progress_importing_dialog));
        progressBarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        isImportSuccesfull = false;
        progressBarDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                FragmentManager manager = getSupportFragmentManager();
                Fragment oldFragment1 = manager.findFragmentByTag(WEEKVIEW_TAG);
                Fragment oldFragment2 = manager.findFragmentByTag(MONTH_TAG);
                Fragment oldFragment3 = manager.findFragmentByTag(DAY_TAG);
                if (oldFragment1 != null){
                    ((ScheduleFragment)oldFragment1).refreshGrid(/*from.dayOfWeek().withMinimumValue(),from.dayOfWeek().withMinimumValue().plusDays(6),*/weeksFromCurrent);
                } else if(oldFragment2!=null){
                    ((MonthScheduleFragment)oldFragment2).invokeRefresh();
                } else if (oldFragment3!=null){
                    ((DayScheduleFragment)oldFragment3).invokeRefresh();
                }
            }
        });
        progressBarDialog.setCancelable(false);
        progressBarDialog.setProgress(0);
        Thread t = new Thread(() -> {
            onClickAcceptImportTemplates(progressBarDialog, isUpperTemplate);
            progressBarDialog.dismiss();
            if(isImportSuccesfull){
                dialog.dismiss();
            }
            isImportSuccesfull = false;
        });
        DateTime from = dateFrom;
        DateTime to = dateTo;
        boolean isDatesCorrect = true;
        if(dateFrom.getYear()>=1990 && (dateFrom.getMonthOfYear())>=1 && dateFrom.getDayOfMonth()>=1
                && dateTo.getYear()>=1990 && dateTo.getMonthOfYear()>=1 && dateTo.getDayOfMonth()>=1
                && !from.isBefore(to)) {
            isDatesCorrect = false;
        }
        if(isDatesCorrect){
            progressBarDialog.show();
            t.start();
        } else {
            dialog.dismiss();
            prepareActionImportTemplates();
            Toast.makeText(getApplicationContext(),R.string.popup_dates_wrong,Toast.LENGTH_LONG).show();
        }
    }

    private void onClickAcceptImportTemplates(ProgressDialog dialog,  boolean isUpperTemplate) {
        /*DatePicker dateFromPicker = importTemplates.findViewById(R.id.dateFromPicker);
        DatePicker dateToPicker = importTemplates.findViewById(R.id.dateToPicker);*/
//        TemplateScheduleWeek upperTemplate = DBManager.copyObjectFromRealm(DBManager.read(TemplateScheduleWeek.class,ConstantApplication.ID,this.upperTemplate.getId()));
  //      TemplateScheduleWeek bottomTemplate = DBManager.copyObjectFromRealm(DBManager.read(TemplateScheduleWeek.class,ConstantApplication.ID,this.bottomTemplate.getId()));
        List<TemplateAcademicHour> templateListUpper = upperTemplate.getTemplateAcademicHourList();
        List<TemplateAcademicHour> templateListBottom = bottomTemplate.getTemplateAcademicHourList();
        DateTime from = dateFrom;
        DateTime to = dateTo;

            int weekCount = countWeeks(from,to);
            List<AcademicHour> academicHours = DBManager.copyObjectFromRealm(AcademicHour.academicHourListFromPeriod(from.toDate(),to.toDate()));
            Collections.sort(academicHours, (o1, o2) -> {
                if (o1.getDate() == null || o2.getDate() == null)
                    return 0;
                return o1.getDate().compareTo(o2.getDate());
            });
            dialog.setProgress(10);
            boolean isUpperCopy = isUpperTemplate;
            for(int i = 0; i<weekCount;i++){
                if(isUpperCopy) {
                    for (int j = 0; j<academicHours.size();j++) {
                        AcademicHour academicHour = academicHours.get(j);
                        TemplateAcademicHour templateAcHour = academicHour.getTemplateAcademicHour();
                        for(TemplateAcademicHour templateAcademicHour:templateListUpper){
                            if(templateAcademicHour.getNumberDayOfWeek()==templateAcHour.getNumberDayOfWeek() && templateAcademicHour.getNumberHalfPairButton() == templateAcHour.getNumberHalfPairButton()) {
                                DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID, templateAcHour.getId());
                                DBManager.delete(AcademicHour.class, ConstantApplication.ID, academicHour.getId());
                                academicHours.remove(academicHour);
                            }
                        }
                        if((new DateTime(academicHour.getDate())).getDayOfWeek()== DateTimeConstants.SUNDAY){
                            isUpperCopy = false;
                        }
                    }
                } else {
                    for (int j = 0; j<academicHours.size();j++) {
                        AcademicHour academicHour = academicHours.get(j);
                        TemplateAcademicHour templateAcHour = academicHour.getTemplateAcademicHour();
                        for(TemplateAcademicHour templateAcademicHour:templateListUpper){
                            if(templateAcademicHour.getNumberDayOfWeek()==templateAcHour.getNumberDayOfWeek() && templateAcademicHour.getNumberHalfPairButton() == templateAcHour.getNumberHalfPairButton()) {
                                DBManager.delete(TemplateAcademicHour.class, ConstantApplication.ID, templateAcHour.getId());
                                DBManager.delete(AcademicHour.class, ConstantApplication.ID, academicHour.getId());
                                academicHours.remove(academicHour);
                            }
                        }
                        if((new DateTime(academicHour.getDate())).getDayOfWeek()== DateTimeConstants.SUNDAY){
                            isUpperCopy = true;
                        }
                    }
                }
            }
            dialog.setProgress(20);
            DateTime current = new DateTime(from);
            List<AcademicHour> academicHourList = new ArrayList<>();
            for(int i = 0; i<weekCount;i++){
                if(isUpperTemplate){
                    for(TemplateAcademicHour templateAcademicHour: templateListUpper){
                        int dayDiff = templateAcademicHour.getNumberDayOfWeek() - (current.getDayOfWeek()-1);
                        if(templateAcademicHour.getNumberDayOfWeek()>=(current.getDayOfWeek()-1) &&
                                (current.plusDays(dayDiff).equals(to)||current.plusDays(dayDiff).isBefore(to))){
                            current = current.plusDays(dayDiff);
                            AcademicHour academicHour = new AcademicHour();
                            TemplateAcademicHour templateAcademicHourCopy = new TemplateAcademicHour();
                            templateAcademicHourCopy.setGroup(templateAcademicHour.getGroup());
                            templateAcademicHourCopy.setDayAndPair(templateAcademicHour.getDayAndPairButton());
                            templateAcademicHourCopy.setSubject(templateAcademicHour.getSubject());
                            try {
                                DBManager.write(templateAcademicHourCopy.createEntity());
                            } catch (Exception e) {
                                Log.d(TAG,"import templates" + e.toString());
                            }
                            int halfPair = templateAcademicHour.getNumberHalfPairButton();
                            int numberPair = templateAcademicHour.getNumberHalfPairButton()/2;
                            int numberHalf = (templateAcademicHour.getNumberHalfPairButton()%2==0) ? 0 : 1;

                            current = current.withHourOfDay(ConstantApplication.timeArray[numberPair][numberHalf][0]).withMinuteOfHour(ConstantApplication.timeArray[numberPair][numberHalf][1])
                                    .withSecondOfMinute(0).withMillisOfSecond(0);
                            academicHour.setDate(current.toDate());
                            academicHour.setTemplateAcademicHour(templateAcademicHourCopy);
                            academicHourList.add(academicHour);
                        }
                    }
                    //current.dayOfWeek().withMaximumValue()
                    current = current.dayOfWeek().withMaximumValue().plusDays(1);
                    isUpperTemplate = false;
                } else {
                    for(TemplateAcademicHour templateAcademicHour : templateListBottom){
                        int dayDiff = templateAcademicHour.getNumberDayOfWeek() - (current.getDayOfWeek()-1);
                        if(templateAcademicHour.getNumberDayOfWeek()>=(current.getDayOfWeek()-1) && (current.equals(to)||current.isBefore(to))){
                            current = current.plusDays(dayDiff);
                            AcademicHour academicHour = new AcademicHour();
                            TemplateAcademicHour templateAcademicHourCopy = new TemplateAcademicHour();
                            templateAcademicHourCopy.setGroup(templateAcademicHour.getGroup());
                            templateAcademicHourCopy.setDayAndPair(templateAcademicHour.getDayAndPairButton());
                            templateAcademicHourCopy.setSubject(templateAcademicHour.getSubject());
                            try {
                                DBManager.write(templateAcademicHourCopy.createEntity());
                            } catch (Exception e) {
                                Log.d(TAG,"import templates" + e.toString());
                            }
                            int numberPair = templateAcademicHour.getNumberHalfPairButton()/2;
                            int numberHalf = (templateAcademicHour.getNumberHalfPairButton()%2==0) ? 0 : 1;

                            current = current.withHourOfDay(ConstantApplication.timeArray[numberPair][numberHalf][0]).withMinuteOfHour(ConstantApplication.timeArray[numberPair][numberHalf][1])
                                    .withSecondOfMinute(0).withMillisOfSecond(0);
                            academicHour.setDate(current.toDate());
                            academicHour.setTemplateAcademicHour(templateAcademicHourCopy);
                            academicHourList.add(academicHour);
                        }
                    }
                    current = current.dayOfWeek().withMaximumValue().plusDays(1);
                    isUpperTemplate = true;
                }
                int finalI = i;
            }
            dialog.setProgress(30);
            int hoursWritten = 0;
            for(AcademicHour academicHour : academicHourList){
                try {
                    DBManager.write(academicHour.createEntityWithoutChecking());
                    academicHour.refreshAllNumberPair(null);
                    ++hoursWritten;
                    dialog.setProgress(30+getBarProgress(academicHourList.size(),hoursWritten));
                } catch (Exception e) {
                    Log.e(TAG,"error when writing templates"+e.toString());
                }
            }
            
            isImportSuccesfull = true;

    }

    private int getBarProgress(int total, int current) {
        return (current*70/total);
    }

    private int countWeeks(DateTime from, DateTime to) {
        /*DateTime current = new DateTime(from);
        int startPosition = current.getDayOfWeek();
        while(true){
            for(int i = startPosition; i<=7;i++){
                current.plusDays(i);
            }
            if(current.equals(to)){
                break;
            } else {
                result++;
                startPosition = 1;
            }
        }*/
        return Weeks.weeksBetween(from.dayOfWeek().withMinimumValue().minusDays(1), to.dayOfWeek().withMaximumValue().plusDays(1)).getWeeks();
    }

    private void bottomSpinnerOnItemSelected(AdapterView<?> parent, int position) {
        String item = (String) parent.getItemAtPosition(position);
        bottomTemplate = DBManager.copyObjectFromRealm(DBManager.read(TemplateScheduleWeek.class, ConstantApplication.NAME, item));
        RadioGroup rg = importTemplates.findViewById(R.id.popup_firstWeek_rgroup);
        TextView label = importTemplates.findViewById(R.id.popupFirstWeek);
        if(bottomTemplate.equals(upperTemplate)){
            rg.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
        } else {
            rg.setVisibility(View.VISIBLE);
            label.setVisibility(View.VISIBLE);
        }
    }

    private void upperSpinnerOnItemSelected(AdapterView<?> parent, int position) {
        String item = (String) parent.getItemAtPosition(position);
        upperTemplate = DBManager.copyObjectFromRealm(DBManager.read(TemplateScheduleWeek.class,ConstantApplication.NAME,item));
        RadioGroup rg = importTemplates.findViewById(R.id.popup_firstWeek_rgroup);
        TextView label = importTemplates.findViewById(R.id.popupFirstWeek);
        if(upperTemplate.equals(bottomTemplate)){
            rg.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
        } else {
            rg.setVisibility(View.VISIBLE);
            label.setVisibility(View.VISIBLE);
        }
    }


    private void prepareActionDatePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.popup_Follow,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onClickAcceptDatePicker(dialog,which);
            }
        });
        builder.setNegativeButton(R.string.popup_cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();                onClickCancelDatePicker(dialog,which);
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
        Log.d(TAG, datePicker.getYear()+"");
        FragmentManager manager = getSupportFragmentManager();
        Fragment oldFragment1 = manager.findFragmentByTag(getResources().getString(R.string.scheduleTag));
        if (oldFragment1 != null && datePicker.getYear()>=1990 && (datePicker.getMonth()+1)>=1 && datePicker.getDayOfMonth()>=1) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
            final DateTime startDate = formatter.parseDateTime("01/01/1990 00:00:00");
            Log.d("MainActivity",datePicker.getYear()+""+datePicker.getMonth()+""+datePicker.getDayOfMonth());
            DateTime selectedDate = new DateTime(datePicker.getYear(),datePicker.getMonth()+1,datePicker.getDayOfMonth(),0,0);
            ((ScheduleFragment)oldFragment1).setCurrentWeek(Weeks.weeksBetween(startDate.dayOfWeek().withMinimumValue().minusDays(1), selectedDate.dayOfWeek().withMaximumValue().plusDays(1)).getWeeks() - 1);
            StringBuilder titleSB = new StringBuilder();
            titleSB.append(monthNumberToString(selectedDate.getMonthOfYear())).append(", ").append(selectedDate.getYear());
            setActionBarTitle(titleSB.toString());
            dialog.dismiss();
        } else {
            prepareActionDatePicker();
            Toast.makeText(getApplicationContext(),R.string.popup_dates_wrong,Toast.LENGTH_LONG).show();
        }

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
    public void showOverflowMenu(boolean showMenu) {
        if (fuckingMenu == null){
            isOverflowShown = showMenu;
            return;
        } else if(isLanguageChanged){
            isOverflowShown = showMenu;
        }
        fuckingMenu.setGroupVisible(R.id.main_menu_group, showMenu);
        fuckingMenu.setGroupVisible(R.id.menu_subject, showMenu);
        fuckingMenu.setGroupVisible(R.id.menu_group, showMenu);
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

    public Toolbar getToolbarSearch() {
        return toolbarSearch;
    }
}
