package com.hpcc.kursovaya;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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
import com.hpcc.kursovaya.ui.groups.GroupsFragment;
import com.hpcc.kursovaya.ui.report.GeneratedReportActivity;
import com.hpcc.kursovaya.ui.schedule.ScheduleFragment;
import com.hpcc.kursovaya.ui.settings.SettingsFragment;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
        final Spinner courseSpinner = genReport.findViewById(R.id.courseSpinner);
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
        } else if(fromYear<=toYear && fromMonth<=toMonth && fromDay>=toDay){
            isDatesCorrect = false;
        }
        if(isDatesCorrect){
             String[] groupsName = {"П-61", "Соплежуйки", "Ветродуйки", "Моржи", "Митинг", "Вирджиния", "П-67", "П-68", "П-69", "П-70"};
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

            Chunk course = new Chunk("Курс: "+courseSpinner.getSelectedItem().toString(),fontSmall);
            paragraphCourse.add(course);
            paragraphCourse.setAlignment(Element.ALIGN_CENTER);
            DateTime currentDate = DateTime.now();
            try {
                document.add(paragraphCourse);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            Paragraph paragraphDates = new Paragraph();
            paragraphDates.setIndentationLeft(100f);
            paragraphDates.setIndentationRight(100f);
            paragraphDates.setSpacingAfter(32);
            Chunk glue = new Chunk(new VerticalPositionMark());
            Phrase dates = new Phrase("",fontSmall);
            dates.add("Дата створення: "+currentDate.getDayOfMonth()+"."+currentDate.getMonthOfYear()+"."+currentDate.getYear());
            dates.add(glue);
            dates.add("Період: "+fromDay+"."+fromMonth+"."+fromYear+" - "+toDay+"."+toMonth+"."+toYear);
            paragraphDates.add(dates);
            try {
                document.add(paragraphDates);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            PdfPTable table = new PdfPTable(groupsName.length + 1);
            float[] relWidths = new float[groupsName.length + 1];
            relWidths[0] = 12f;
            for (int i = 1; i < groupsName.length + 1; i++) {
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
            cellGroupHeader.setColspan(groupsName.length);
            cellGroupHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellGroupHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellGroupHeader);

            for (int i = 0; i < groupsName.length; i++) {
                PdfPCell cellGroupName = new PdfPCell(new Phrase(groupsName[i], fontSmall));
                cellGroupName.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellGroupName.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cellGroupName);
            }
            String[] subjectNames = {"Інженерія програмного забезпечення", "Основи метрології", "Інструментальні засоби візуального програмування", "Людино-машинний інтерфейс", "Проектування програмного забезпечення"};
            for(int i = 0 ;i<subjectNames.length;i++) {
                PdfPTable innerTable = new PdfPTable(2);
                PdfPCell subjectCell = new PdfPCell(new Phrase(subjectNames[i], fontSmall));
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
                for (int j = 0; j < groupsName.length; j++) {
                    PdfPTable hoursTable = new PdfPTable(1);
                    hoursTable.addCell(new Phrase("1"/*plan hours*/, fontSmall));
                    hoursTable.addCell(new Phrase("2"/*fact hours*/, fontSmall));
                    hoursTable.addCell(new Phrase("3"/*cancelled hours*/, fontSmall));
                    hoursTable.addCell(new Phrase("4"/*Rest hours hours*/, fontSmall));
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
