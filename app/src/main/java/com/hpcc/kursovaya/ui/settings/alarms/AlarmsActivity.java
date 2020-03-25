package com.hpcc.kursovaya.ui.settings.alarms;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hpcc.kursovaya.R;
import com.hpcc.kursovaya.dao.entity.constant.ConstantEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class AlarmsActivity extends AppCompatActivity {
    private static final String TAG = AppCompatActivity.class.getSimpleName();
    ListView alarmFirstLesson = null;

    ListView alarmSecondLesson = null;

    ListView alarmThirdLesson = null;

    ListView alarmFourthLesson = null;

    ListView alarmFifthLesson = null;

    AlarmListAdapter firstLessonAdapter = null;
    AlarmListAdapter secondLessonAdapter = null;
    AlarmListAdapter thirdLessonAdapter = null;
    AlarmListAdapter fourthLessonAdapter = null;
    AlarmListAdapter fifthLessonAdapter = null;

    int choosedPosition = 0;

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
                String existstoragedir = getExternalFilesDir(null).getAbsolutePath() + "/alarms.dat";
                File file = new File(existstoragedir);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(ConstantEntity.timeArray);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
        alarmFirstLesson= findViewById(R.id.alarmFirstLessonLSV);
        firstLessonAdapter = new AlarmListAdapter(this,R.layout.listview_item_alarm, ConstantEntity.timeArray[0]);
        alarmFirstLesson.setAdapter(firstLessonAdapter);
        setListViewHeightBasedOnChildren(alarmFirstLesson);
        alarmFirstLesson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] hourMinute = (int[]) parent.getItemAtPosition(position);
                choosedPosition = position;
                firstLessonOnClick(hourMinute[0],hourMinute[1]);
            }
        });
        alarmSecondLesson = findViewById(R.id.alarmSecondLessonLSV);
        secondLessonAdapter = new AlarmListAdapter(this,R.layout.listview_item_alarm, ConstantEntity.timeArray[1]);
        alarmSecondLesson.setAdapter(secondLessonAdapter);
        setListViewHeightBasedOnChildren(alarmSecondLesson);
        alarmSecondLesson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] hourMinute = (int[]) parent.getItemAtPosition(position);
                choosedPosition = position;
                secondLessonOnClick(hourMinute[0],hourMinute[1]);
            }
        });
        alarmThirdLesson = findViewById(R.id.alarmThirdLessonLSV);
        thirdLessonAdapter = new AlarmListAdapter(this,R.layout.listview_item_alarm, ConstantEntity.timeArray[2]);
        alarmThirdLesson.setAdapter(thirdLessonAdapter);
        setListViewHeightBasedOnChildren(alarmThirdLesson);
        alarmThirdLesson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] hourMinute = (int[]) parent.getItemAtPosition(position);
                choosedPosition = position;
                thirdLessonOnClick(hourMinute[0],hourMinute[1]);
            }
        });
        alarmFourthLesson = findViewById(R.id.alarmFourthLessonLSV);
        fourthLessonAdapter = new AlarmListAdapter(this,R.layout.listview_item_alarm, ConstantEntity.timeArray[3]);
        alarmFourthLesson.setAdapter(fourthLessonAdapter);
        setListViewHeightBasedOnChildren(alarmFourthLesson);
        alarmFourthLesson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] hourMinute = (int[]) parent.getItemAtPosition(position);
                choosedPosition = position;
                fourthLessonOnClick(hourMinute[0],hourMinute[1]);
            }
        });
        alarmFifthLesson = findViewById(R.id.alarmFifthLessonLSV);
        fifthLessonAdapter = new AlarmListAdapter(this,R.layout.listview_item_alarm, ConstantEntity.timeArray[4]);
        alarmFifthLesson.setAdapter(fifthLessonAdapter);
        setListViewHeightBasedOnChildren(alarmFifthLesson);
        alarmFifthLesson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] hourMinute = (int[]) parent.getItemAtPosition(position);
                choosedPosition = position;
                fifthLessonOnClick(hourMinute[0],hourMinute[1]);
            }
        });
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);


    }

    private void fifthLessonOnClick(int hour, int minute) {
        new TimePickerDialog(AlarmsActivity.this, fifthLessonClick,hour,minute,true).show();
    }

    private void fourthLessonOnClick(int hour, int minute) {
        new TimePickerDialog(AlarmsActivity.this, fourthLessonClick,hour,minute,true).show();
    }

    private void thirdLessonOnClick(int hour, int minute) {
        new TimePickerDialog(AlarmsActivity.this, thirdLessonClick,hour,minute,true).show();
    }

    private void secondLessonOnClick(int hour, int minute) {
        new TimePickerDialog(AlarmsActivity.this, secondLessonClick,hour,minute,true).show();
    }

    private void firstLessonOnClick(int hour, int minute) {
        new TimePickerDialog(AlarmsActivity.this, firstLessonClick,hour,minute,true).show();
    }
    TimePickerDialog.OnTimeSetListener firstLessonClick=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ConstantEntity.timeArray[0][choosedPosition][0] = hourOfDay;
            ConstantEntity.timeArray[0][choosedPosition][1] = minute;
            firstLessonAdapter.notifyDataSetChanged();
        }
    };
    TimePickerDialog.OnTimeSetListener secondLessonClick=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ConstantEntity.timeArray[1][choosedPosition][0] = hourOfDay;
            ConstantEntity.timeArray[1][choosedPosition][1] = minute;
            secondLessonAdapter.notifyDataSetChanged();
        }
    };
    TimePickerDialog.OnTimeSetListener thirdLessonClick=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ConstantEntity.timeArray[2][choosedPosition][0] = hourOfDay;
            ConstantEntity.timeArray[2][choosedPosition][1] = minute;
            thirdLessonAdapter.notifyDataSetChanged();
        }
    };

    TimePickerDialog.OnTimeSetListener fourthLessonClick=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ConstantEntity.timeArray[3][choosedPosition][0] = hourOfDay;
            ConstantEntity.timeArray[3][choosedPosition][1] = minute;
            fourthLessonAdapter.notifyDataSetChanged();
        }
    };
    TimePickerDialog.OnTimeSetListener fifthLessonClick=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ConstantEntity.timeArray[4][choosedPosition][0] = hourOfDay;
            ConstantEntity.timeArray[4][choosedPosition][1] = minute;
            fifthLessonAdapter.notifyDataSetChanged();
        }
    };
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
