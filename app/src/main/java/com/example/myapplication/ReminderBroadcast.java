package com.example.myapplication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {


    private static final int DAILY_REMINDER_REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

       /* Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(context, Reminder.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(Reminder.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                DAILY_REMINDER_REQUEST_CODE,PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, notification);
        }*/
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());
        Notification notification = builder.setContentTitle("Stock Update")
                .setContentText("Kindly Update your Stock!").setAutoCancel(true)
               .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .build();


        NotificationManager notif=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE
        );
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelId = "stock_upd";
            NotificationChannel channel = new NotificationChannel(channelId,"Daily Reminder",NotificationManager.IMPORTANCE_HIGH);
            assert notif != null;
            notif.createNotificationChannel(channel);
            builder.setChannelId(channelId);

        }
        assert notif != null;
        notif.notify(0, builder.build());

    }
}
