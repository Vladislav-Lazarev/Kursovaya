package com.hpcc.kursovaya;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hpcc.kursovaya.ui.schedule.AddClass;

import org.joda.time.DateTime;

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

        String description = intent.getStringExtra("description");
        StringBuilder notificationHeader = new StringBuilder();
       notificationHeader.append(intent.getStringExtra("yearOfNot"))
                           .append(".")
                           .append(intent.getStringExtra("monthOfYearNot"))
                           .append(".")
                           .append(intent.getStringExtra("dayOfMonthNot"));
        notificationHeader.append("  ЧЧ:ММ");

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
        /*NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(0,builder.build());*/
    }
}
