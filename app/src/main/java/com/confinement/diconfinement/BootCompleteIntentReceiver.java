package com.confinement.diconfinement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            AlarmService amService = new AlarmService(context);
            amService.startAlarm();
            SharedPrefUtils.setAlarmSharedPref(context);
        }
    }
}
