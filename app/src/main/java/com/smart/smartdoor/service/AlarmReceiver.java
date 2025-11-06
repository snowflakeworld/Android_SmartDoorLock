package com.smart.smartdoor.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String JOB_TAG = "ForeverApp::JobWakeLock";
    PowerManager.WakeLock wl = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Global.writeLog("AlarmReceiver", "Alarm triggered, running job...");

        // Acquire wake lock for this job
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        try {
            wl = pm.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    JOB_TAG
            );
            wl.acquire(Constants.ALARM_ACQUIRE_TIME); // hold for max 30 seconds
        } catch (Exception e) {
            Global.writeLog("Wake Lock Exception", e.getMessage());
        }

        doBackgroundJob();

        // Reschedule next job
        Intent serviceIntent = new Intent(context, MyForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }

        rescheduleAlarm(context);
    }

    private void doBackgroundJob() {
    }

    private void rescheduleAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        long triggerAt = System.currentTimeMillis() + Constants.ALARM_SERVICE_INTERVAL;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
            );
        }

        Global.writeLog("AlarmReceiver", "Rescheduled");
    }

}