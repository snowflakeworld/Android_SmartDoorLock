package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class CheckRegisterRequest {
    private String cid;

    public CheckRegisterRequest(String cid) {
        this.cid = cid;
    }

    public String build() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("cid", cid);
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
