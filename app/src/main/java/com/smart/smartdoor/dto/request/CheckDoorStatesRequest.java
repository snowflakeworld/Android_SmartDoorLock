package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class CheckDoorStatesRequest {
    private String cid;
    private String password;
    private long deviceId;

    public CheckDoorStatesRequest(String cid, String password, long deviceId) {
        this.cid = cid;
        this.password = password;
        this.deviceId = deviceId;
    }

    public String build() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("cid", cid);
            obj.put("password", password);
            obj.put("deviceId", deviceId);
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
