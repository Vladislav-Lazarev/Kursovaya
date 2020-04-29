package com.hpcc.kursovaya;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hpcc.kursovaya.dao.constant.ConstantApplication;
import com.hpcc.kursovaya.dao.entity.Group;
import com.hpcc.kursovaya.dao.entity.Subject;
import com.hpcc.kursovaya.dao.entity.schedule.AcademicHour;
import com.hpcc.kursovaya.dao.entity.schedule.template.TemplateAcademicHour;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DeviceBootReceiver extends BroadcastReceiver {
    private final String TAG = DeviceBootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Log.d(TAG,"Boot service: is ready");
            Realm realm = Realm.getDefaultInstance();
            List<RealmResults<AcademicHour>> list = new ArrayList<>();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    list.add(realm.where(AcademicHour.class).greaterThanOrEqualTo("date", DateTime.now().toDate()).findAll());
                }
            });
            List<AcademicHour> academicHourList = list.get(0);
            Log.d(TAG,"Boot service: academic hours read. Length:"+academicHourList.size());
            for(AcademicHour academicHour : academicHourList){
                TemplateAcademicHour templateAcademicHour = academicHour.getTemplateAcademicHour();
                Group group = templateAcademicHour.getGroup();
                Subject subject = templateAcademicHour.getSubject();
                if(templateAcademicHour!=null && group!=null && subject!=null && academicHour.getNotificationBefore()!=0 ) {
                    Log.d(TAG,"Boot service: template, subject, group non null");
                    DateTime timeOfRing = DateTime.now();
                    DateTime dayOfWeek = new DateTime(academicHour.getDate());
                    Log.d(TAG,dayOfWeek.toString());
                    int numberOfLesson = templateAcademicHour.getNumberHalfPairButton();
                    int numberOfHalf =  ((numberOfLesson+1) % 2 == 0)? 1 : 0;
                    numberOfLesson = numberOfLesson/2;
                    int hourOfDay = ConstantApplication.timeArray[numberOfLesson][numberOfHalf][0];
                    int minuteOfHour = ConstantApplication.timeArray[numberOfLesson][numberOfHalf][1];
                    Log.d(TAG,numberOfLesson+" "+numberOfHalf);
                    Log.d(TAG,"Notification before:"+academicHour.getNotificationBefore());
                    switch (academicHour.getNotificationBefore()) {
                        case 1:
                            timeOfRing = dayOfWeek.withHourOfDay(hourOfDay/*<-- place for hour local variable*/ - 1).withMinuteOfHour(minuteOfHour/*minute of class*/);
                            break;
                        case 2:
                            timeOfRing = dayOfWeek.withHourOfDay(hourOfDay/*<-- place for hour local variable*/ - 2).withMinuteOfHour(minuteOfHour);
                            break;
                        case 3:
                            timeOfRing = dayOfWeek.withHourOfDay(hourOfDay/*<-- place for hour local variable*/ - 3).withMinuteOfHour(minuteOfHour);
                            break;
                        case 4:
                            timeOfRing = dayOfWeek.withHourOfDay(hourOfDay).withMinuteOfHour(minuteOfHour).minusDays(1);
                            break;
                        case 5:
                            timeOfRing = dayOfWeek.withHourOfDay(hourOfDay).withMinuteOfHour(minuteOfHour).minusDays(2);
                            break;
                    }
                    Log.d(TAG,timeOfRing.toString());
                    DateTime now = DateTime.now();
                    Seconds difference = Seconds.secondsBetween(now, timeOfRing);
                    Log.d(TAG, "Difference in seconds:" + difference);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, difference.getSeconds());
                    Intent _intent = new Intent(context, AlarmClassReceiver.class);
                    _intent.putExtra("groupName", group.getName());
                    _intent.putExtra("yearOfNot", Integer.toString(dayOfWeek.getYear()));
                    _intent.putExtra("monthOfYearNot", Integer.toString(dayOfWeek.getMonthOfYear()));
                    _intent.putExtra("dayOfMonthNot", Integer.toString(dayOfWeek.getDayOfMonth()));
                    _intent.putExtra("description", academicHour.getNote());
                    _intent.putExtra("hourOfDay", hourOfDay);
                    _intent.putExtra("minuteOfHour", minuteOfHour);
                    String strFlag = dayOfWeek.getMonthOfYear() + "" + dayOfWeek.getDayOfMonth() + hourOfDay + minuteOfHour;
                    final int id = Integer.valueOf(strFlag);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, _intent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

        }
    }
}
