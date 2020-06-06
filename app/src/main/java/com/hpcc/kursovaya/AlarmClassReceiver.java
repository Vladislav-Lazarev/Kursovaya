package com.hpcc.kursovaya;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmClassReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "Class";
    private static final String TAG = AlarmClassReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {


        CharSequence name = "Classes notification";// The user-visible name of the channel.
        Log.d(TAG, "alarmClassReceiverStarted");

        int monthOfYear = Integer.valueOf(intent.getStringExtra("monthOfYearNot"));
        int dayOfMonthNot = Integer.valueOf(intent.getStringExtra("dayOfMonthNot"));
        String eventDescription = intent.getStringExtra("groupName");
        String description;
        description = eventDescription+": "+intent.getStringExtra("description");
        StringBuilder notificationHeader = new StringBuilder();
       notificationHeader.append(intent.getStringExtra("yearOfNot"))
                           .append(".")
                           .append(monthOfYear)
                           .append(".")
                           .append(dayOfMonthNot);
        notificationHeader.append("  ");
        int hourOfDay = (intent.getIntExtra("hourOfDay",0));
        int minuteOfHour = (intent.getIntExtra("minuteOfHour",0));
        if(hourOfDay<10){
            notificationHeader.append(0);
        }
        notificationHeader.append(hourOfDay).append(":");
        if(minuteOfHour<10){
            notificationHeader.append(0);
        }
        notificationHeader.append(minuteOfHour);
        Log.d(TAG, "alarmClassReceiver: notification content is ready");


        PackageManager pm = context.getPackageManager();
      //  Intent notificationIntent = pm.getLaunchIntentForPackage("com.hpcc.kursovaya.dao.ApplicationRealm");
        Intent notificationIntent = new Intent(context,MainActivity.class);
        Log.d(TAG, "alarmClassReceiver: notification intent created");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Log.d(TAG, "alarmClassReceiver: notification intent flags were set");
        PendingIntent contentIntent = PendingIntent.getActivity(context,0,notificationIntent,0);
        Log.d(TAG, "alarmClassReceiver: pending intent created is ready");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        Notification notification = builder.setContentTitle(notificationHeader.toString())
                .setContentText(description)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp).build();

        Log.d(TAG, "alarmClassReceiver: notification created and flags were set");

        /*NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("Дата N пара Y година")
                        .setContentText("Вика")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
*/
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Log.d(TAG, "alarmClassReceiver: before notify");
        notificationManager.notify( (int) System.currentTimeMillis(),notification);
        Log.d(TAG, "alarmClassReceiver: after notify");
        try {
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notificationSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(0,builder.build());*/
    }
}
