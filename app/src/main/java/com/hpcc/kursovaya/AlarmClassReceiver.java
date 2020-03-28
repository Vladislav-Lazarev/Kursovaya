package com.hpcc.kursovaya;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.hpcc.kursovaya.ui.schedule.AddClass;

public class AlarmClassReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "Class";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, AddClass.class);
        Log.d("AlarmClassReceiver","onReceive method");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(AddClass.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(228, PendingIntent.FLAG_UPDATE_CURRENT);

        String description = intent.getStringExtra("groupName")+": "+intent.getStringExtra("description");
        StringBuilder notificationHeader = new StringBuilder();
       notificationHeader.append(intent.getStringExtra("yearOfNot"))
                           .append(".")
                           .append(intent.getStringExtra("monthOfYearNot"))
                           .append(".")
                           .append(intent.getStringExtra("dayOfMonthNot"));
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        Notification notification = builder.setContentTitle(notificationHeader.toString())
                .setContentText(description)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp).build();
        /*NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("Дата N пара Y година")
                        .setContentText("Вика")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
*/
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
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
