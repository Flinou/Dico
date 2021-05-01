package com.confinement.diconfinement;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (FileUtils.needsNotification(context, FileUtils.getDay())) {
            Intent intentMainAct = new Intent(context, MainActivity.class);
            intentMainAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentMainAct, 0);
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(NOTIFICATION_SERVICE);
            Notification notif;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setChannelForNotif(notificationManager);
                notif = createNotif(context, pendingIntent);
            } else {
                notif = createNotifOldAndroid(context, pendingIntent);
            }
            notificationManager.notify(0, notif);
            SharedPrefUtils.updateLastNotificationDate(context, FileUtils.getDay());
        }
    }

    private Notification createNotifOldAndroid(Context context, PendingIntent pendingIntent) {
        return new Notification.Builder(context)
                .setContentTitle(Globals.notification_title)
                .setSmallIcon(R.mipmap.ic_lanceur)
                .setContentText(Globals.notification_content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification createNotif(Context context, PendingIntent pendingIntent) {
        return new Notification.Builder(context, Globals.channel_id)
                .setContentTitle(Globals.notification_title)
                .setSmallIcon(R.mipmap.ic_lanceur)
                .setContentText(Globals.notification_content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setChannelForNotif(NotificationManager notificationManager) {
        NotificationChannel channel = new NotificationChannel(Globals.channel_id, Globals.channel_name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(Globals.channel_description);
        notificationManager.createNotificationChannel(channel);
    }
}
