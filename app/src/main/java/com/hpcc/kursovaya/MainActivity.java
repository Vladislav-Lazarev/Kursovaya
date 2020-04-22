package com.hpcc.kursovaya;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Speciality;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.query.DBManager;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateAcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.lesson.template.TemplateScheduleWeek;
import com.hpcc.kursovaya.ui.groups.GroupsFragment;
import com.hpcc.kursovaya.ui.schedule.ScheduleFragment;
import com.hpcc.kursovaya.ui.settings.SettingsFragment;
import com.hpcc.kursovaya.ui.settings.language.LocaleManager;
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
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_CODE = 26;
    private Menu fuckingMenu;
    private final String TAG = MainActivity.class.getSimpleName();
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
    private long mLastClickTime = 0;
    private Locale savedLocale = null;
    private boolean isOverflowShown = true;
    private TemplateScheduleWeek upperTemplate;
    private TemplateScheduleWeek bottomTemplate;

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
           Log.d(TAG,"Чо теперь сейвд инстанс стейт?");
               getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment,
                       new ScheduleFragment(), getResources().getString(R.string.scheduleTag)).commit();
               navigationView.setCheckedItem(R.id.nav_schedule);

        } else {
           Log.d(TAG,"открывай сука");
           isLanguageChanged = true;
       }
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
                String specTag1 = getResources().getString(R.string.scheduleTag);
                Fragment oldFragment1 = manager.findFragmentByTag(specTag1);
                if (oldFragment1 != null) {
                    transaction.replace(R.id.nav_host_fragment,oldFragment1);
                    ((ScheduleFragment)oldFragment1).setActionBarTitle();
                    ((ScheduleFragment)oldFragment1).setCoupleHeaders();// transaction.addToBackStack(null);
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
        showOverflowMenu(isOverflowShown);
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
                isSelectMode=true;
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
        List<Speciality> specialities = new ArrayList<>();
        specialities.add(new Speciality());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.popup_choose_date_report);
        builder.setPositiveButton(R.string.popup_gen_report, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickAcceptReportDates(dialog, which,specialities.get(0));
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
        Spinner spinnerSpeciality =
                ConstantApplication.fillingSpinner(this, genReport.findViewById(R.id.specialitySpinner),
                        new Speciality().entityToNameList());
        listenerSpinnerSpeciality(spinnerSpeciality,specialities);
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
    private void listenerSpinnerSpeciality(Spinner spinner, List<Speciality> specialities) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String item = (String) parent.getItemAtPosition(selectedItemPosition);
                Speciality speciality = DBManager.read(Speciality.class, ConstantApplication.NAME, item);
                specialities.set(0,speciality);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onClickAcceptReportDates(DialogInterface dialog, int which,Speciality speciality) {
        final DatePicker pickerFrom = genReport.findViewById(R.id.dateFromPicker);
        final DatePicker pickerTo = genReport.findViewById(R.id.dateToPicker);
        final Spinner courseSpinner = genReport.findViewById(R.id.courseSpinner);
        int fromYear = pickerFrom.getYear();
        int fromMonth = pickerFrom.getMonth();
        int fromDay = pickerFrom.getDayOfMonth();
        DateTime from = new DateTime(fromYear,fromMonth+1,fromDay,0,0);

        int toYear = pickerTo.getYear();
        int toMonth = pickerTo.getMonth();
        int toDay = pickerTo.getDayOfMonth();
        DateTime to = new DateTime(toYear,toMonth+1,toDay,0,0);

        boolean isDataCorrect = true;
        if(speciality.getCountCourse()<Integer.valueOf(courseSpinner.getSelectedItem().toString())){
            isDataCorrect = false;
        }
        if(pickerFrom.getYear()<1990 && (pickerFrom.getMonth())+1<1 && pickerFrom.getDayOfMonth()<1
                && pickerTo.getYear()<1990 && pickerTo.getMonth()+1<1 && pickerTo.getDayOfMonth()<1
                && from.isBefore(to)) {
            isDataCorrect = false;
        }
        if(isDataCorrect){
            String existstoragedir = getExternalFilesDir(null).getAbsolutePath() + "/report.pdf";
            File file = new File(existstoragedir);


            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
            try {
                PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            document.open();

            document.setPageSize(PageSize.A4);
            Paragraph paragraphCourse = new Paragraph();
            paragraphCourse.setSpacingAfter(32);

            Chunk course = new Chunk(getString(R.string.courseLabelPDF)+" "+courseSpinner.getSelectedItem().toString(),fontSmall);
            paragraphCourse.add(course);
            paragraphCourse.setAlignment(Element.ALIGN_CENTER);

            Paragraph paragraphSpeciality = new Paragraph();
            paragraphSpeciality.setSpacingAfter(32);

            Chunk specialityLabel = new Chunk(getString(R.string.specialityLabelPDF)+" "+speciality.getName(),fontSmall);
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
            Phrase dates = new Phrase("",fontSmall);
            String currentDayOfMonth = (currentDate.getDayOfMonth()<10) ? "0"+currentDate.getDayOfMonth() : currentDate.getDayOfMonth()+"";
            String currentMonth = (currentDate.getMonthOfYear()<10) ? "0"+currentDate.getMonthOfYear() : currentDate.getMonthOfYear()+"";
            String fromMonthStr = (fromMonth < 10) ? "0"+fromMonth : fromMonth+"";
            String fromDayStr = (fromDay < 10) ? "0"+fromDay : fromDay+"";
            String toMonthStr = (toMonth < 10) ? "0"+toMonth : toMonth+"";
            String toDayStr = (toDay < 10) ? "0" + toDay : toDay + "";

            dates.add(getString(R.string.creationDatePDF)+" "+currentDayOfMonth+"."+currentMonth+"."+currentDate.getYear());
            dates.add(glue);
            dates.add(getString(R.string.periodLabelPDF)+" "+fromDayStr+"."+fromMonthStr+"."+fromYear+" - "+toDayStr+"."+toMonthStr+"."+toYear);
            paragraphDates.add(dates);
            try {
                document.add(paragraphDates);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            List<AcademicHour> academicHourList = new ArrayList<>();
            List<Group> groupList = new ArrayList<>();
            List<Subject> subjectList = new ArrayList<>();
            for(AcademicHour academicHour : AcademicHour.academicHourListFromPeriod(from.toDate(),to.plusDays(1).toDate())){
                try{
                    TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                    if(templateAcademicHour!=null){
                        Group group = templateAcademicHour.getGroup();
                        Subject subject = templateAcademicHour.getSubject();
                        if(group!=null && subject!=null){
                            Speciality specialityFromHour = group.getSpecialty();
                            if(specialityFromHour!=null && speciality.equals(specialityFromHour) && group.getNumberCourse()==Integer.valueOf(courseSpinner.getSelectedItem().toString())){
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
            groupList = new ArrayList<>(new LinkedHashSet<>(groupList));
            subjectList = new ArrayList<>(new LinkedHashSet<>(subjectList));

            Map<Group,List<AcademicHour>> academicHoursOfGroup = new HashMap<>();
            for(Group group : groupList){
                List<AcademicHour> academicHours = new ArrayList<>();
                for(AcademicHour academicHour : academicHourList){
                    TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                    if(templateAcademicHour.getGroup()!=null && group.equals(templateAcademicHour.getGroup())){
                        academicHours.add(academicHour);
                    }
                }
                academicHoursOfGroup.put(group,academicHours);
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
            for(int i = 0 ;i<subjectList.size();i++) {
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
                    int planHours = subjectList.get(i).getSpecialityCountHourMap().get(speciality);
                    int factHours = groupList.get(j).getReadHours(subjectList.get(i),academicHoursOfGroup.get(groupList.get(j)));
                    int cancelledHours = groupList.get(j).getCanceledHours(subjectList.get(i),academicHoursOfGroup.get(groupList.get(j)));
                    hoursTable.addCell(new Phrase(Integer.toString(planHours)/*plan hours*/, fontSmall));
                    hoursTable.addCell(new Phrase(Integer.toString(factHours)/*fact hours*/, fontSmall));
                    hoursTable.addCell(new Phrase(Integer.toString(cancelledHours)/*cancelled hours*/, fontSmall));
                    hoursTable.addCell(new Phrase(Integer.toString(planHours-factHours-cancelledHours)/*Rest hours hours*/, fontSmall));
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
            document.close();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(MainActivity.this,BuildConfig.APPLICATION_ID+".provider",file),"application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent intentOpen = Intent.createChooser(intent,"Open PDF");
            try {
                startActivity(intentOpen);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            prepareReporDatePicker();
            Toast.makeText(getApplicationContext(),R.string.popup_dates_wrong,Toast.LENGTH_LONG).show();
        }

    }

    private void prepareActionImportTemplates() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_importTemplates);
        AtomicBoolean isUpperFirst = new AtomicBoolean(true);
        builder.setPositiveButton(R.string.popup_import,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onClickAcceptImportTemplates(dialog,which,isUpperFirst.get());
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
        final DatePicker pickerFrom = importTemplates.findViewById(R.id.dateFromPicker);
        final DatePicker pickerTo = importTemplates.findViewById(R.id.dateToPicker);
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

    private void onClickAcceptImportTemplates(DialogInterface dialog, int which, boolean isUpperTemplate) {
        DatePicker dateFromPicker = importTemplates.findViewById(R.id.dateFromPicker);
        DatePicker dateToPicker = importTemplates.findViewById(R.id.dateToPicker);
        List<TemplateAcademicHour> templateListUpper = upperTemplate.getTemplateAcademicHourList();
        List<TemplateAcademicHour> templateListBottom = bottomTemplate.getTemplateAcademicHourList();
        DateTime from = new DateTime(dateFromPicker.getYear(),dateFromPicker.getMonth()+1,dateFromPicker.getDayOfMonth(),0,0);
        DateTime to = new DateTime(dateToPicker.getYear(),dateToPicker.getMonth()+1,dateToPicker.getDayOfMonth(),0,0);
        boolean isDatesCorrect = true;
        if(dateFromPicker.getYear()<1990 && (dateFromPicker.getMonth())+1<1 && dateFromPicker.getDayOfMonth()<1
                && dateToPicker.getYear()<1990 && dateToPicker.getMonth()+1<1 && dateToPicker.getDayOfMonth()<1
                && from.isBefore(to)) {
            isDatesCorrect = false;
        }
        if(isDatesCorrect){
            List<AcademicHour> academicHours = DBManager.copyObjectFromRealm(AcademicHour.academicHourListFromPeriod(from.toDate(),to.toDate()));
            for(AcademicHour academicHour : academicHours){
                TemplateAcademicHour templateAcHour = academicHour.getTemplateAcademicHour();
                DBManager.delete(AcademicHour.class, ConstantApplication.ID,academicHour.getId());
                DBManager.delete(TemplateAcademicHour.class,ConstantApplication.ID,templateAcHour.getId());
            }

            int weekCount = countWeeks(from,to);
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
                            academicHour.setDate(current.toDate());
                            academicHour.setTemplateAcademicHour(templateAcademicHourCopy);
                            academicHourList.add(academicHour);
                        }
                    }
                    current = current.dayOfWeek().withMaximumValue().plusDays(1);
                    isUpperTemplate = true;
                }
            }
            for(AcademicHour academicHour : academicHourList){
                try {
                    DBManager.write(academicHour.createEntity());
                } catch (Exception e) {
                    Log.e(TAG,"error when writing templates"+e.toString());
                }
            }
            if(academicHourList.size()>0){
                FragmentManager manager = getSupportFragmentManager();
                Fragment oldFragment1 = manager.findFragmentByTag(getResources().getString(R.string.scheduleTag));
                if (oldFragment1 != null){
                    ((ScheduleFragment)oldFragment1).refreshGrid(from.dayOfWeek().withMinimumValue(),from.dayOfWeek().withMinimumValue().plusDays(6));
                }
            }
        } else {
            prepareActionImportTemplates();
            Toast.makeText(getApplicationContext(),R.string.popup_dates_wrong,Toast.LENGTH_LONG).show();
        }
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
        bottomTemplate = DBManager.read(TemplateScheduleWeek.class,ConstantApplication.NAME,item);
    }

    private void upperSpinnerOnItemSelected(AdapterView<?> parent, int position) {
        String item = (String) parent.getItemAtPosition(position);
        upperTemplate = DBManager.read(TemplateScheduleWeek.class,ConstantApplication.NAME,item);
    }


    private void prepareActionDatePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_datePicker);
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
        }
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
