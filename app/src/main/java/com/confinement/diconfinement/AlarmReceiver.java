package com.confinement.diconfinement;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentMainAct = new Intent(context, MainActivity.class);
        intentMainAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //Put Extra String to handle application start from notification (must lands to word of the day tab at startup)
        intentMainAct.putExtra(Globals.NOTIFICATION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentMainAct, 0);
        NotificationManager notifManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notif;
        String wordOfTheDay = WordOfTheDayUtils.retrieveWordOfTheDay(context.getApplicationContext());
        Spannable notifContent = generateNotifContent(context, wordOfTheDay);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setChannelForNotif(notifManager);
            notif = createNotif(context, pendingIntent, notifContent);
        } else {
            notif = createNotifOldAndroid(context, pendingIntent, notifContent);
        }
        notifManager.notify(0, notif);
    }

    @NonNull
    private Spannable generateNotifContent(Context context, String notifContent) {
        String wordOdTheDayFormat = context.getString(R.string.wordOfTheDay_notif);
        int wordDayStartPos = wordOdTheDayFormat.indexOf("%1$s");
        String lineFormatted = context.getString(R.string.wordOfTheDay_notif, notifContent);
        Spannable wordOfTheDayNotif = new SpannableString(lineFormatted);
        wordOfTheDayNotif.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), wordDayStartPos, wordDayStartPos + notifContent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return wordOfTheDayNotif;
    }

    private Notification createNotifOldAndroid(Context context, PendingIntent pendingIntent, Spannable notifContent) {
        return new Notification.Builder(context)
                .setContentTitle(Globals.NOTIFICATION_TITLE)
                .setSmallIcon(R.drawable.ic_dico_notif)
                .setContentText(notifContent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification createNotif(Context context, PendingIntent pendingIntent, Spannable wordOfTheDay) {
        return new Notification.Builder(context, Globals.CHANNEL_ID)
                .setContentTitle(Globals.NOTIFICATION_TITLE)
                .setSmallIcon(R.drawable.ic_dico_notif)
                .setContentText(wordOfTheDay)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setChannelForNotif(NotificationManager notificationManager) {
        NotificationChannel channel = new NotificationChannel(Globals.CHANNEL_ID, Globals.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(Globals.CHANNEL_DESCRIPTION);
        notificationManager.createNotificationChannel(channel);
    }
}
