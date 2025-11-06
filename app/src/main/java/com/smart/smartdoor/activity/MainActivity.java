package com.smart.smartdoor.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.smart.smartdoor.R;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends BaseActivity {

    private static MainActivity instance = null;

    public static MainActivity getInstance() {
        return instance;
    }

    boolean isRealExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        Global.setOverscan(this, true);
        Global.setStatusBarColor(this, Color.TRANSPARENT, true);
        Global.setNavigationBarColorResId(this, R.color.white, true);

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setItemIconSizeRes(R.dimen.icon_size_small);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

        Global.fixTheAllViews((ViewGroup) getWindow().getDecorView());
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        if (navView.getSelectedItemId() == R.id.navigation_home) {
            if (isRealExit)
                super.onBackPressed();
            else {
                Snackbar.make(findViewById(R.id.nav_host_fragment_activity_main), R.string.finish_message, Snackbar.LENGTH_LONG)
                        .show();
                isRealExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isRealExit = false;
                    }
                }, 3000);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        instance = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_BATTERY_OPTIMIZATION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (pm.isIgnoringBatteryOptimizations(getPackageName())) {
//                    Log.d("BatteryOpt", "User allowed to ignore battery optimizations.");
                } else {
//                    Log.d("BatteryOpt", "User denied ignore battery optimizations request.");
                    Global.requestBatteryOptimization(this);
                }
            }
        }
    }
}