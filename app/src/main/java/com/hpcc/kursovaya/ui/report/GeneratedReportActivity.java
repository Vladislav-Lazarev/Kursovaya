package com.hpcc.kursovaya.ui.report;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.hpcc.kursovaya.R;

public class GeneratedReportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int pixels = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
        setContentView(R.layout.activity_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView textHeader = findViewById(R.id.toolbar_title);
        textHeader.setText(R.string.report);
        TableRow tableRow = findViewById(R.id.groupHeaderRow);
        TextView groupFirst = findViewById(R.id.group00);
        groupFirst.setText("П-61");
        TextView groupSecond = new TextView(this);
        groupSecond.setText("П-62");
        groupSecond.setBackground(getResources().getDrawable(R.drawable.grid));
        groupSecond.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        groupSecond.setTextColor(getResources().getColor(R.color.appDefaultBlack));
        groupSecond.setTextSize(16);
        groupSecond.setWidth(pixels);
        TextView groupThird = new TextView(this);
        groupThird.setText("П-63");
        groupThird.setBackground(getResources().getDrawable(R.drawable.grid));
        groupThird.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        groupThird.setTextColor(getResources().getColor(R.color.appDefaultBlack));
        groupThird.setTextSize(16);
        groupThird.setWidth(pixels);
        TextView groupFourth = new TextView(this);
        groupFourth.setText("П-64");
        groupFourth.setBackground(getResources().getDrawable(R.drawable.grid));
        groupFourth.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        groupFourth.setTextColor(getResources().getColor(R.color.appDefaultBlack));
        groupFourth.setTextSize(16);
        groupFourth.setWidth(pixels);

        TextView groupFifth = new TextView(this);
        groupFifth.setText("П-65");
        groupFifth.setBackground(getResources().getDrawable(R.drawable.grid));
        groupFifth.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        groupFifth.setTextColor(getResources().getColor(R.color.appDefaultBlack));
        groupFifth.setTextSize(16);
        groupFifth.setWidth(pixels);
        TextView groupSixth = new TextView(this);
        groupSixth.setText("П-66");
        groupSixth.setBackground(getResources().getDrawable(R.drawable.grid));
        groupSixth.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        groupSixth.setTextColor(getResources().getColor(R.color.appDefaultBlack));
        groupSixth.setTextSize(16);
        groupSixth.setWidth(pixels);
        TextView groupSeventh = new TextView(this);
        groupSeventh.setText("П-67");
        groupSeventh.setBackground(getResources().getDrawable(R.drawable.grid));
        groupSeventh.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        groupSeventh.setTextColor(getResources().getColor(R.color.appDefaultBlack));
        groupSeventh.setTextSize(16);
        groupSeventh.setWidth(pixels);
        TextView groupEights = new TextView(this);
        groupEights.setText("П-68");
        groupEights.setBackground(getResources().getDrawable(R.drawable.grid));
        groupEights.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        groupEights.setTextColor(getResources().getColor(R.color.appDefaultBlack));
        groupEights.setTextSize(16);
        groupEights.setWidth(pixels);
        TextView groupNineth = new TextView(this);
        groupNineth.setText("П-69");
        groupNineth.setBackground(getResources().getDrawable(R.drawable.grid));
        groupNineth.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        groupNineth.setTextColor(getResources().getColor(R.color.appDefaultBlack));
        groupNineth.setTextSize(16);
        groupNineth.setWidth(pixels);
        TextView groupTenth = new TextView(this);
        groupTenth.setText("П-70");
        groupTenth.setBackground(getResources().getDrawable(R.drawable.grid));
        groupTenth.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        groupTenth.setTextColor(getResources().getColor(R.color.appDefaultBlack));
        groupTenth.setTextSize(16);
        groupTenth.setWidth(pixels);
        tableRow.addView(groupSecond,1);
        tableRow.addView(groupThird,2);
        tableRow.addView(groupFourth,3);
        tableRow.addView(groupFifth,4);
        tableRow.addView(groupSixth,5);
        tableRow.addView(groupSeventh,6);
        tableRow.addView(groupEights,7);
        tableRow.addView(groupNineth,8);
        tableRow.addView(groupTenth,9);
    }
}
