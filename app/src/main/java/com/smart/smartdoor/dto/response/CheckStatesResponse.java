package com.smart.smartdoor.dto.response;

import org.json.JSONObject;

public class CheckStatesResponse extends BaseResponse {
    private final static String KEY_SOCKET_CONNECTED = "socketConnected";
    private final static String KEY_DOOR_CONNECTED = "doorConnected";
    private final static String KEY_BATTERY_LEVEL = "batteryLevel";

    public boolean isSocketConnected = false;
    public boolean isDoorConnected = false;
    public int doorBatteryLevel = -1;

    public CheckStatesResponse(String msg) {
        super(msg);

        try {
            JSONObject obj = new JSONObject(msg);

            isSocketConnected = obj.getInt(KEY_SOCKET_CONNECTED) == 1;
            isDoorConnected = obj.getInt(KEY_DOOR_CONNECTED) == 1;
            doorBatteryLevel = obj.getInt(KEY_BATTERY_LEVEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSocketConnected() {
        return isSocketConnected;
    }

    public void setSocketConnected(boolean socketConnected) {
        isSocketConnected = socketConnected;
    }

    public boolean isDoorConnected() {
        return isDoorConnected;
    }

    public void setDoorConnected(boolean doorConnected) {
        isDoorConnected = doorConnected;
    }

    public int getDoorBatteryLevel() {
        return doorBatteryLevel;
    }

    public void setDoorBatteryLevel(int doorBatteryLevel) {
        this.doorBatteryLevel = doorBatteryLevel;
    }
}
