package com.hpcc.kursovaya.ui.report;

import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.hpcc.kursovaya.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GeneratedReportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] groupsName = {"П-61", "Соплежуйки", "Ветродуйки", "Моржи", "Митинг", "Вирджиния", "П-67", "П-68", "П-69", "П-70"};
        setContentView(R.layout.activity_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_path_150));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        String existstoragedir = getExternalFilesDir(null).getAbsolutePath() + "/report.pdf";
        File file = new File(existstoragedir);


            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }


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
            PdfPCell subjectCell = new PdfPCell(new Phrase(subjectNames[0], fontSmall));
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

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        TextView textHeader = findViewById(R.id.toolbar_title);
        textHeader.setText(R.string.report);



    }
}
