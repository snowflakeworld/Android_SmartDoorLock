package com.smart.smartdoor.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String loginData = Global.getSharedStringValue(context, Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_RESP_DATA, "");
            if (loginData.isEmpty())
                return;

            Intent serviceIntent = new Intent(context, MyForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}
