package com.smart.smartdoor.utils;

import android.app.Activity;
import android.content.Intent;

import com.smart.smartdoor.activity.LoginActivity;

public class User {
    public static User instance = null;
    public String cId = TestFlag.isEmulator ? "1281893012" : null;
    public String password = "";
    public String name = "";
    public String gender = "";
    public String birthday = "";
    public String citizenNumber = "";
    public String installPlace = "";
    public String roleType = "";
    public int districtId = 0;
    public String districtInfo = "";
    public String detailInfo = "";
    public String convDetailInfo = "";
    public long deviceId = 0;

    public boolean isSocketConnected = false;
    public boolean isDoorConnected = false;
    public int doorBatteryLevel = -1;

    public static User getInstance() {
        if (instance == null)
            instance = new User();

        return instance;
    }

    public void initialize() {
        password = "";
        name = "";
        gender = "";
        birthday = "";
        citizenNumber = "";
        installPlace = "";
        roleType = "";
        districtId = 0;
        districtInfo = "";
        detailInfo = "";
        convDetailInfo = "";
        deviceId = 0;
    }

    public void login(Activity activity, Runnable runnable) {
        LoginActivity.mRunnable = runnable;
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }
}
