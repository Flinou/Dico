package com.confinement.diconfinement;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
            Notification notif = new Notification.Builder(context)
                    .setContentTitle("Scientia potentia est")
                    .setSmallIcon(R.mipmap.ic_lanceur)
                    .setContentText("Il y a un nouveau mot du jour !")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true).build();

            notificationManager.notify(0, notif);
            SharedPrefUtils.updateLastNotificationDate(context, FileUtils.getDay());
        }
    }
}
