package com.smart.smartdoor.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.smart.smartdoor.R;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.User;
import com.clj.fastble.BleManager;

import java.util.ArrayList;

public class SplashActivity extends BaseActivity {

    private final String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        Global.setFullScreen(this);
        Global.fixTheAllViews((ViewGroup) getWindow().getDecorView());

        checkAppPermission();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            startApp();
        }
    }

    private void checkAppPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ArrayList<String> requestPermissions = new ArrayList<>();
            for (String perm : permissions) {
                if (ContextCompat.checkSelfPermission(this, perm)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions.add(perm);
                }
            }
            if (requestPermissions.size() == 0) {
                startApp();
            } else {
                String[] perms = new String[requestPermissions.size()];
                for (int i = 0; i < perms.length; ++i) {
                    perms[i] = requestPermissions.get(i);
                }
                ActivityCompat.requestPermissions(this, perms, 0);
            }
        } else {
            startApp();
        }
    }

    private void checkBeforeStart() {
        User.getInstance();
        User.instance.initialize();
        Global.initialize();
    }

    private void startApp() {
        checkBeforeStart();
        Global.requestBatteryOptimization(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, 3000);
    }
}