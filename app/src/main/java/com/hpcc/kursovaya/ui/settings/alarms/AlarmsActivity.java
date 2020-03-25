package com.hpcc.kursovaya.ui.settings.alarms;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

public class AlarmsActivity extends AppCompatActivity {
    private static final String TAG = AppCompatActivity.class.getSimpleName();
    ListView alarmFirstLesson = null;

    ListView alarmSecondLesson = null;

    ListView alarmThirdLesson = null;

    ListView alarmFourthLesson = null;

    ListView alarmFifthLesson = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_rings);
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
        alarmFirstLesson= findViewById(R.id.alarmFirstLessonLSV);
        AlarmListAdapter firstLessonAdapter = new AlarmListAdapter(this,R.layout.listview_item_alarm, ConstantEntity.timeArray[0]);
        alarmFirstLesson.setAdapter(firstLessonAdapter);
        setListViewHeightBasedOnChildren(alarmFirstLesson);
        alarmSecondLesson = findViewById(R.id.alarmSecondLessonLSV);
        AlarmListAdapter secondLessonAdapter = new AlarmListAdapter(this,R.layout.listview_item_alarm, ConstantEntity.timeArray[1]);
        alarmSecondLesson.setAdapter(secondLessonAdapter);
        setListViewHeightBasedOnChildren(alarmSecondLesson);
        alarmThirdLesson = findViewById(R.id.alarmThirdLessonLSV);
        AlarmListAdapter thirdLessonAdapter = new AlarmListAdapter(this,R.layout.listview_item_alarm, ConstantEntity.timeArray[2]);
        alarmThirdLesson.setAdapter(thirdLessonAdapter);
        setListViewHeightBasedOnChildren(alarmThirdLesson);
        alarmFourthLesson = findViewById(R.id.alarmFourthLessonLSV);
        AlarmListAdapter fourthLessonAdapter = new AlarmListAdapter(this,R.layout.listview_item_alarm, ConstantEntity.timeArray[3]);
        alarmFourthLesson.setAdapter(fourthLessonAdapter);
        setListViewHeightBasedOnChildren(alarmFourthLesson);
        alarmFifthLesson = findViewById(R.id.alarmFifthLessonLSV);
        AlarmListAdapter fifthLessonAdapter = new AlarmListAdapter(this,R.layout.listview_item_alarm, ConstantEntity.timeArray[4]);
        alarmFifthLesson.setAdapter(fifthLessonAdapter);
        setListViewHeightBasedOnChildren(alarmFifthLesson);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);


    }

    public static void setListViewHeightBasedOnChildren
            (ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
