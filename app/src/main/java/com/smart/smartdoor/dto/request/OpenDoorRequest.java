package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class OpenDoorRequest {
    private String cid;
    private String password;

    public OpenDoorRequest(String cid, String password) {
        this.cid = cid;
        this.password = password;
    }

    public String build() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("cid", cid);
            obj.put("password", password);
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
