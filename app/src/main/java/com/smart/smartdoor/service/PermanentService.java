package com.smart.smartdoor.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import com.smart.smartdoor.activity.MainActivity;
import com.smart.smartdoor.utils.Global;

public class PermanentService {
    public static void startService(Activity activity) {
        Global.requestBatteryOptimization(MainActivity.getInstance());

        Intent serviceIntent = new Intent(activity, MyForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.startForegroundService(serviceIntent);
        } else {
            activity.startService(serviceIntent);
        }
    }
}
