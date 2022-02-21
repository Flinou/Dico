package com.confinement.diconfinement;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmService {
    private Context context;
    private PendingIntent mAlarmSender;
    private long alarmRepeatDelay = 60L*1000*60*24;
    public AlarmService(Context context) {
        this.context = context;
        mAlarmSender = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);
    }

    public void startAlarm(){
        Calendar alarmTime = setAlarmTime();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), alarmRepeatDelay, mAlarmSender);
    }

    private Calendar setAlarmTime() {
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.setTimeInMillis(System.currentTimeMillis());
        alarmTime.set(Calendar.HOUR_OF_DAY, 00);
        alarmTime.set(Calendar.MINUTE, 10);
        alarmTime.set(Calendar.SECOND, 10);
        return alarmTime;
    }
}
